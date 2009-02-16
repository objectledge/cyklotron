package net.cyklotron.cms.modules.components.category.query;

import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.ResourceListTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryListConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * Category Query List component.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryList.java,v 1.2 2005-01-25 11:24:24 pablo Exp $ 
 */
public class CategoryQueryList extends SkinableCMSComponent
{
	private CategoryQueryService categoryQueryService;
	
    private TableStateManager tableStateManager;

    public CategoryQueryList(org.objectledge.context.Context context, Logger logger,
        Templating templating, CmsDataFactory cmsDataFactory, SkinService skinService,
        MVCFinder mvcFinder, CategoryQueryService categoryQueryService,
        TableStateManager tableStateManager)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        this.categoryQueryService = categoryQueryService;
		this.tableStateManager = tableStateManager;
	}

	public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
		throws ProcessingException
	{
		try
		{
			CategoryQueryListConfiguration config =
				new CategoryQueryListConfiguration(getConfiguration());

			// setup category query result node
			SiteResource site = cmsDataFactory.getCmsData(context).getSite();
			NavigationNodeResource queryResultNode = categoryQueryService.getResultsNode(coralSession, site);
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
				categoryQueryService.getCategoryQueryPoolRoot(coralSession, getSite(context)), queryPoolName);
			if(res.length == 0)
			{
				componentError(context, "configured category query pool not found");
				return;
			}
			CategoryQueryPoolResource queryPool = (CategoryQueryPoolResource)res[0];
			
			// setup table state
			TableState state = tableStateManager.getState(context, "cms:components:category,query,CategoryQueryList");
			if(state.isNew())
			{
				state.setTreeView(false);
				state.setCurrentPage(0); // no paging
			}
			state.setSortColumnName(config.getSortColumn());
            
			state.setAscSort(config.getSortDir());

			// get query list and setup table tool
			List queries = queryPool.getQueries();
			if(queries.size() == 0)
			{
				componentError(context, "configured category query pool has no queries");
				return;
			}
			TableModel model = new ResourceListTableModel(queries, i18nContext.getLocale());
			templatingContext.put("table", new TableTool(state,null, model));
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
