package net.cyklotron.cms.modules.views.category.query;

import java.util.Set;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryResultsConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.category.query.screens.CategoryQueryResultsResourceList;
import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.labeo.services.resource.Resource;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Category Query Resutls screen.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryResults.java,v 1.1 2005-01-24 04:35:06 pablo Exp $ 
 */
public class CategoryQueryResults 
    extends BaseSkinableScreen
{
	/** Table service used to display resource lists. */
	protected TableService tableService;

	/** category query service */
	protected CategoryQueryService categoryQueryService;

	public CategoryQueryResults()
	{
		tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
		categoryQueryService = (CategoryQueryService)broker.getService(CategoryQueryService.SERVICE_NAME);
	}
	
	public void prepareDefault(RunData data, Context context)
		throws ProcessingException
	{
		CmsData cmsData = cmsDataFactory.getCmsData(context);

		// get query object
		CategoryQueryResource categoryQuery;        
		try
		{
			if(parameters.get(CategoryQueryUtil.QUERY_PARAM).isDefined())
			{
                categoryQuery = CategoryQueryUtil.getQuery(coralSession, data);
			}
			else
			{
                categoryQuery = categoryQueryService.getDefaultQuery(cmsData.getSite());
			}
            
            if(categoryQuery == null)
            {
                screenError(cmsData.getNode(), context, "default category query not configured");
                return;
            }
		}
		catch (CategoryQueryException e1)
		{
			screenError(cmsData.getNode(), context, "cannot get catgory query root for site "+
                cmsData.getSite().getName());
			return;
		}    
		templatingContext.put("category_query", categoryQuery);    

		// get config
		CategoryQueryResultsConfiguration config = 
			new CategoryQueryResultsConfiguration(getConfiguration(), categoryQuery);

        CategoryQueryResultsResourceList resList = new CategoryQueryResultsResourceList(
                categoryQuery, config, coralSession, categoryQueryService);

        // get resources based on category query
        Resource[] resources = null;
        String query = resList.getQuery(data, config);
        Set idSet = resList.getIdSet(data, config);
        try
        {
            if(idSet != null)
            {
                resources = categoryQueryService.forwardQuery(query, idSet);
            }
            else
            {
                resources = categoryQueryService.forwardQuery(query);
            }
        }
        catch(Exception e)
        {
            screenError(cmsData.getNode(), context, "Cannot execute category query");
            return;
        }

        // setup table tool
        TableState state = tableService.getGlobalState(data, resList.getTableStateName(data));
        TableTool tool = resList.getTableTool(data, config, state, resources);
        templatingContext.put("table", tool);
    }
}
