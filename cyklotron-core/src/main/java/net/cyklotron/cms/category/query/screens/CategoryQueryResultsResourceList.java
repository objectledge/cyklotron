package net.cyklotron.cms.category.query.screens;
    
import net.cyklotron.cms.category.components.BaseResourceListConfiguration;
import net.cyklotron.cms.category.components.DocumentResourceList;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryResultsConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableState;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * This class contains logic of a screen which displays lists of resources assigned
 * to queried categories.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryResultsResourceList.java,v 1.1 2005-01-12 20:45:25 pablo Exp $
 */
public class CategoryQueryResultsResourceList
extends DocumentResourceList
{
    protected CategoryQueryResource query;
	protected CategoryQueryResultsConfiguration config;
	
    public CategoryQueryResultsResourceList(
        CategoryQueryResource query,
        CategoryQueryResultsConfiguration config,
        ResourceService resourceService,
        CategoryQueryService categoryQueryService)
    {
        super(resourceService, categoryQueryService);
        this.query = query;
        this.config = config;
	}

    public BaseResourceListConfiguration createConfig(RunData data)
    throws ProcessingException
    {
        return config;
    }
    
    public String getQuery(RunData data, BaseResourceListConfiguration config)
    throws ProcessingException
    {
		return query.getQuery();
    }
    
    public String getTableStateName(RunData data)
    {
        return "net.cyklotron.cms.category.category_query_results."+query.getPath();
    }

    /* (non-Javadoc)
     * @see net.cyklotron.cms.modules.components.category.BaseResourceList#getResourceClasses(net.labeo.webcore.RunData, net.cyklotron.cms.category.BaseResourceListConfiguration)
     */
    protected String[] getResourceClasses(RunData data, BaseResourceListConfiguration config)
	throws ProcessingException
    {
        int offset = ((CategoryQueryResultsConfiguration) config).getPublicationTimeOffset();
        if(offset == -1)
        {
    		String[] resClassNames = null;
    		CategoryQueryResource categoryQuery = getCategoryQueryRes(data, config);
        	if (categoryQuery != null)
            {
    			resClassNames = categoryQuery.getAcceptedResourceClassNames();
            } 
    		if(resClassNames == null)
    		{
    			resClassNames = new String[0];
    		}
    		return resClassNames;
        }
        return null;
    }
    
    protected void setupPaging(
        RunData data,
        BaseResourceListConfiguration config,
        TableState state)
    {
        if(state.isNew())
        {
            state.setPageSize(config.getMaxResNumber());
            state.setCurrentPage(1);
        }
        
        // WARN: duplicate setPage action
        if(data.getParameters().get(TableConstants.TABLE_ID_PARAM_KEY).isDefined() &&
            data.getParameters().get(TableConstants.TABLE_ID_PARAM_KEY).asInt() == state.getId())
        {
            state.setCurrentPage(
                data.getParameters().get(TableConstants.PAGE_NO_PARAM_KEY).asInt(1));
        }
        else
        {
            state.setCurrentPage(1);
        }
    }

    // implementation /////////////////////////////////////////////////////////////////////////////
    
    protected CategoryQueryResource getCategoryQueryRes(
    	RunData data, BaseResourceListConfiguration config)
    	throws ProcessingException
	{
		return query;
	}
}
