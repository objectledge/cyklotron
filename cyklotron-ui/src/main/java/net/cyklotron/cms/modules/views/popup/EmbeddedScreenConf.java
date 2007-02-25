package net.cyklotron.cms.modules.views.popup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.ScreenVariantResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResource;

public class EmbeddedScreenConf
    extends BaseCMSScreen
{
	protected IntegrationService integrationService;

	protected SkinService skinService;

    public EmbeddedScreenConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, IntegrationService integrationService,
        SkinService skinService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.integrationService = integrationService;
		this.skinService = skinService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
		String instance = parameters.get("component_instance");
		templatingContext.put("instance", instance);
		
		NavigationNodeResource node = getNode();
		Parameters prefs = preferencesService.getCombinedNodePreferences(coralSession, node);
		String app = prefs.get("screen.app",null);
		String screen = prefs.get("screen.class",null);
		String variant = "Default";
		if(app != null && screen != null)
		{
			screen = screen.replace(",",".");
            variant = prefs.get("screen.variant."+app+"."+
                screen,"Default");
			ScreenResource screenRes = integrationService.getScreen(coralSession, app, screen);
			templatingContext.put("selected", screenRes);
		}			
   	    ApplicationResource[] apps = integrationService.getApplications(coralSession);
        Collections.sort(Arrays.asList(apps), new PriorityComparator());
        templatingContext.put("apps", apps);
    	
		NameComparator comparator = new NameComparator(StringUtils.getLocale("en_US"));
        CmsData cmsData = getCmsData();
        SiteResource site = cmsData.getSite();
        if(site == null)
        {
            site = cmsData.getGlobalComponentsDataSite();
        }
        Map map = new HashMap();
        for(int i=0; i<apps.length; i++)
        {
            if(integrationService.isApplicationEnabled(coralSession, site, apps[i]))
            {
                ScreenResource[] comps = integrationService.getScreens(coralSession, apps[i]);
                ArrayList compList = new ArrayList(Arrays.asList(comps));
                Collections.sort(compList, comparator);
                map.put(apps[i], compList);
            }
        }
        templatingContext.put("apps_map", map);
        
        try
        {
			String skin = skinService.getCurrentSkin(coralSession, site);
			ScreenVariantResource[] variants =
							skinService.getScreenVariants(coralSession, site, skin, app, screen);
			templatingContext.put("variants", Arrays.asList(variants));
            
			for(int i=0; i<variants.length; i++)
			{
				if(variants[i].getName().equals(variant))
				{
					templatingContext.put("current_variant", variants[i]);
					break;
				}
			}        
        }
        catch(Exception e)
        {
        	throw new ProcessingException("Failed to load skin information", e);
        }
    }

    public static class PriorityComparator
        implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            ApplicationResource a1 = (ApplicationResource)o1;
            ApplicationResource a2 = (ApplicationResource)o2;
            return a1.getPriority() - a2.getPriority();
        }
    }
}
