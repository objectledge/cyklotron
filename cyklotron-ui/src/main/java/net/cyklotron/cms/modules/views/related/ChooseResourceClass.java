package net.cyklotron.cms.modules.views.related;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;

public class ChooseResourceClass
    extends BaseRelatedScreen
{
    protected IntegrationService integrationService;

    
    public ChooseResourceClass(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, RelatedService relatedService,
        IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        relatedService);
        this.integrationService = integrationService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        ApplicationResource[] apps = integrationService.getApplications(coralSession);
        List appList = new ArrayList();
        NameComparator comparator = new NameComparator(StringUtils.getLocale("en_US"));
        Map map = new HashMap();
        for(int i=0; i<apps.length; i++)
        {
            if(apps[i].getEnabled())
            {
                appList.add(apps[i]);
                ResourceClassResource[] resClasses = integrationService.getResourceClasses(coralSession, apps[i]);
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
