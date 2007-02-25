package net.cyklotron.cms.modules.views.popup;

import net.cyklotron.cms.CmsComponentData;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ComponentResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseComponentClass
    extends BaseCMSScreen
{
    protected IntegrationService integrationService;

    public ChooseComponentClass(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.integrationService = integrationService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        String instance = parameters.get("component_instance");
        templatingContext.put("instance", instance);
        
        Parameters preferences;
        CmsData cmsData = getCmsData();
        if(cmsData.getNode() != null)
        {
            preferences = preferencesService.getCombinedNodePreferences(coralSession, cmsData.getNode());
        }
        else
        {
            preferences = preferencesService.getSystemPreferences(coralSession);
        }

        String app = CmsComponentData.getParameter(preferences,"component."+instance+".app",null);
        String cClass = CmsComponentData.getParameter(preferences,"component."+instance+".class",null);
        if(app != null && cClass != null)
        {
            ComponentResource component = integrationService.getComponent(coralSession, app, cClass);
            templatingContext.put("selected", component);
        }	
		
        ApplicationResource[] apps = integrationService.getApplications(coralSession);
        List appList = new ArrayList();
        NameComparator comparator = new NameComparator(StringUtils.getLocale("en_US"));

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
                appList.add(apps[i]);
                ComponentResource[] comps = integrationService.getComponents(coralSession, apps[i]);
                ArrayList compList = new ArrayList(Arrays.asList(comps));
                Collections.sort(compList, comparator);
                map.put(apps[i], compList);
            }
        }
        Collections.sort(appList, new PriorityComparator());
        templatingContext.put("apps", appList);
        templatingContext.put("apps_map", map);
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
