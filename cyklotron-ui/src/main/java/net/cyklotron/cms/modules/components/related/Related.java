package net.cyklotron.cms.modules.components.related;

import java.util.ArrayList;
import java.util.Arrays;

import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.ResourceListTableModel;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.util.CmsResourceClassFilter;
import net.cyklotron.cms.util.CmsResourceListTableModel;
import net.cyklotron.cms.util.ProtectedViewFilter;

/**
 * Related component.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Related.java,v 1.1 2005-01-24 04:35:33 pablo Exp $
 */

public class Related
    extends SkinableCMSComponent
{
    private RelatedService relatedService;

    private TableService tableService;

    public Related()
    {
        relatedService = (RelatedService)broker.getService(RelatedService.SERVICE_NAME);
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME))
            .getFacility(RelatedService.LOGGING_FACILITY);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) throws ProcessingException
    {
        if(getNode() == null)
        {
            componentError(context, "No node selected");
            return;
        }
        NavigationNodeResource currentNode = getNode();
        try
        {
            Parameters componentConfig = getConfiguration();
            String resPath = componentConfig.get("related_path","");
            Resource resource = currentNode;
            if(resPath.length() != 0)
            {
                Resource[] resources = coralSession.getStore().getResourceByPath(resPath);
                if(resources.length == 1)
                {
                    resource = resources[0];
                }
            }
            Resource[] related = relatedService.getRelatedTo(resource);
            TableState state = tableService.getGlobalState(data, "cms:components:related,Related");
            String[] resourceClassResourceNames = componentConfig.getStrings("related_classes");
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setPageSize(0);
                // TODO: Add configuration support
                state.setSortColumnName("index.title");
                state.setSortDir(TableConstants.SORT_ASC);
            }
            TableModel model = new CmsResourceListTableModel(Arrays.asList(related), i18nContext.getLocale()());
            ArrayList filters = new ArrayList();
            filters.add(new ProtectedViewFilter(coralSession.getUserSubject()));
            filters.add(new CmsResourceClassFilter(resourceClassResourceNames));
            TableTool helper = new TableTool(state, model, filters);
            
            templatingContext.put("table", helper);
        }
        catch(Exception e)
        {
            componentError(context, "Related Exception", e);
        }
    }
}
