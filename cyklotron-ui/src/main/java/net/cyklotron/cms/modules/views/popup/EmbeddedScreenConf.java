package net.cyklotron.cms.modules.views.popup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import net.labeo.services.resource.table.NameComparator;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

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

	protected PreferencesService preferencesService;

	protected SkinService skinService;

    public EmbeddedScreenConf()
    {
        integrationService = (IntegrationService)broker.
            getService(IntegrationService.SERVICE_NAME);
		preferencesService = (PreferencesService)broker.
			getService(PreferencesService.SERVICE_NAME);
		skinService = (SkinService)broker.
			getService(SkinService.SERVICE_NAME);
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
		String instance = parameters.get("component_instance");
		templatingContext.put("instance", instance);
		
		NavigationNodeResource node = getNode();
		Parameters prefs = preferencesService.getCombinedNodePreferences(node);
		String app = prefs.get("screen.app",null);
		String screen = prefs.get("screen.class",null);
		String variant = "Default";
		if(app != null && screen != null)
		{
            variant = prefs.get("screen.variant."+app+"."+
                screen.replace(',','.'),"Default");
			ScreenResource screenRes = integrationService.getScreen(app, screen);
			templatingContext.put("selected", screenRes);
		}			
   	
   	    ApplicationResource[] apps = integrationService.getApplications();
        Collections.sort(Arrays.asList(apps), new PriorityComparator());
        templatingContext.put("apps", apps);
    	
		NameComparator comparator = new NameComparator(StringUtils.getLocale("en_US"));

        Map map = new HashMap();
        for(int i=0; i<apps.length; i++)
        {
            if(apps[i].getEnabled())
            {
                ScreenResource[] comps = integrationService.getScreens(apps[i]);
                ArrayList compList = new ArrayList(Arrays.asList(comps));
                Collections.sort(compList, comparator);
                map.put(apps[i], compList);
            }
        }
        templatingContext.put("apps_map", map);
        
        try
        {
			SiteResource site = getSite();
			String skin = skinService.getCurrentSkin(site);
			ScreenVariantResource[] variants =
							skinService.getScreenVariants(site, skin, app, screen);
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
