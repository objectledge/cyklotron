package net.cyklotron.cms.modules.views.aggregation;

import java.util.Arrays;

import net.cyklotron.cms.aggregation.AggregationException;
import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.CreationTimeComparator;
import net.labeo.services.resource.table.CreatorNameComparator;
import net.labeo.services.resource.table.NameComparator;
import net.labeo.services.table.ListTableModel;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Screen with sites that can be chosen for resource recommendation.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: RecommendResource.java,v 1.1 2005-01-24 04:34:51 pablo Exp $
 */
public class RecommendResource
    extends BaseAggregationScreen
{
    protected TableService tableService;

    public RecommendResource() throws ProcessingException
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        long resourceId = parameters.getLong("res_id", -1L);
        if(resourceId == -1)
        {
            throw new ProcessingException("Resource id not parameter not found");
        }
        try
        {
            Resource resource = coralSession.getStore().getResource(resourceId);
            templatingContext.put("resource",resource);
            SiteResource[] sites = aggregationService.getValidRecommendationSites(resource);
            TableColumn[] columns = new TableColumn[3];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()()));
            columns[1] = new TableColumn("creator", new CreatorNameComparator(i18nContext.getLocale()()));
            columns[2] = new TableColumn("creation_time", new CreationTimeComparator());
            TableState state = tableService.getLocalState(data, "cms:screens:aggregation:ImporterAssignments");
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(Arrays.asList(sites), columns);
            templatingContext.put("table", new TableTool(state, model, null));
        }
        catch(AggregationException e)
        {
            throw new ProcessingException("AggregationException", e);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("Resource not found", e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize table toolkit", e);
        }
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        try
        {
            SiteResource node = getSite();
            Permission permission = coralSession.getSecurity()
                .getUniquePermission("cms.aggregation.export");
            return coralSession.getUserSubject().hasPermission(node, permission);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check security", e);
        }
    }
}

