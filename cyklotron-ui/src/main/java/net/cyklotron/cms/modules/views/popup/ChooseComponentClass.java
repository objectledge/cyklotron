package net.cyklotron.cms.modules.views.popup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.labeo.services.resource.table.NameComparator;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ComponentResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;

public class ChooseComponentClass
    extends BaseCMSScreen
{
    protected IntegrationService integrationService;

	protected PreferencesService preferencesService;

    public ChooseComponentClass()
    {
        integrationService = (IntegrationService)broker.
            getService(IntegrationService.SERVICE_NAME);
		preferencesService = (PreferencesService)broker.
			getService(PreferencesService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        String instance = parameters.get("component_instance");
        templatingContext.put("instance", instance);
        
        Parameters preferences;
        CmsData cmsData = getCmsData();
        if(cmsData.getNode() != null)
        {
            preferences = preferencesService.getCombinedNodePreferences(cmsData.getNode());
        }
        else
        {
            preferences = preferencesService.getSystemPreferences();
        }

        String app = preferences.get("component."+instance+".app",null);
        String cClass = preferences.get("component."+instance+".class",null);
        if(app != null && cClass != null)
        {
            ComponentResource component = integrationService.getComponent(app, cClass);
            templatingContext.put("selected", component);
        }	
		
        ApplicationResource[] apps = integrationService.getApplications();
        List appList = new ArrayList();
        NameComparator comparator = new NameComparator(StringUtils.getLocale("en_US"));

        Map map = new HashMap();
        for(int i=0; i<apps.length; i++)
        {
            if(apps[i].getEnabled())
            {
                appList.add(apps[i]);
                ComponentResource[] comps = integrationService.getComponents(apps[i]);
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
