package net.cyklotron.cms.modules.views.related;

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

import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.integration.IntegrationService;

public class ChooseResourceClass
    extends BaseRelatedScreen
{
    protected IntegrationService integrationService;

    public ChooseResourceClass()
    {
        integrationService = (IntegrationService)broker.
            getService(IntegrationService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        ApplicationResource[] apps = integrationService.getApplications();
        List appList = new ArrayList();
        NameComparator comparator = new NameComparator(StringUtils.getLocale("en_US"));
        Map map = new HashMap();
        for(int i=0; i<apps.length; i++)
        {
            if(apps[i].getEnabled())
            {
                appList.add(apps[i]);
                ResourceClassResource[] resClasses = integrationService.getResourceClasses(apps[i]);
                ArrayList resClassesList = new ArrayList();
                for(int j = 0; j < resClasses.length; j++)
                {
                    if(resClasses[j].getRelatedSupported())
                    {
                        resClassesList.add(resClasses[j]);
                    }
                }
                Collections.sort(resClassesList, comparator);
                map.put(apps[i], resClassesList);
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
