package net.cyklotron.cms.modules.components.category.query;

import java.util.List;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryListConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.ResourceListTableModel;
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
 * Category Query List component.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryList.java,v 1.1 2005-01-24 04:35:14 pablo Exp $ 
 */
public class CategoryQueryList extends SkinableCMSComponent
{
	private CategoryQueryService categoryQueryService;
	private TableService tableService;

	public CategoryQueryList()
	{
		categoryQueryService = (CategoryQueryService)broker.getService(CategoryQueryService.SERVICE_NAME);
		tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
	}

	public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
		throws ProcessingException
	{
		try
		{
			CategoryQueryListConfiguration config =
				new CategoryQueryListConfiguration(getConfiguration());

			// setup category query result node
			SiteResource site = cmsDataFactory.getCmsData(context).getSite();
			NavigationNodeResource queryResultNode = categoryQueryService.getResultsNode(site);
			if(queryResultNode == null)
			{
				componentError(context, "query result node not configured");
				return;
			}
			templatingContext.put("category_query_node", queryResultNode); 

			// setup header
			templatingContext.put("header", config.getHeader());
			
			// get category query pool
			String queryPoolName = config.getQueryPoolName();
			if(queryPoolName == null)
			{
				componentError(context, "category query pool not configured");
				return;
			}
			Resource[] res = coralSession.getStore().getResource(
				categoryQueryService.getCategoryQueryPoolRoot(getSite(context)), queryPoolName);
			if(res.length == 0)
			{
				componentError(context, "configured category query pool not found");
				return;
			}
			CategoryQueryPoolResource queryPool = (CategoryQueryPoolResource)res[0];
			
			// setup table state
			TableState state = tableService.getGlobalState(data, "cms:components:category,query,CategoryQueryList");
			if(state.isNew())
			{
				state.setViewType(TableConstants.VIEW_AS_LIST);
				state.setCurrentPage(0); // no paging
			}
			state.setSortColumnName(config.getSortColumn());
			state.setSortDir(config.getSortDir());

			// get query list and setup table tool
			List queries = queryPool.getQueries();
			if(queries.size() == 0)
			{
				componentError(context, "configured category query pool has no queries");
				return;
			}
			TableModel model = new ResourceListTableModel(queries, i18nContext.getLocale()());
			templatingContext.put("table", new TableTool(state, model, null));
		}
		catch (CategoryQueryException e)
		{
			componentError(context, "category query pool root not found");
			return;
		}
		catch (TableException e)
        {
			componentError(context, "error setting up the table tool");
			return;
        }
	}
}
