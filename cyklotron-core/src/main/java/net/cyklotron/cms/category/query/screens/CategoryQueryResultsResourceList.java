package net.cyklotron.cms.category.query.screens;
    
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableConstants;
import org.objectledge.table.TableState;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.components.BaseResourceListConfiguration;
import net.cyklotron.cms.category.components.DocumentResourceList;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryResultsConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.site.SiteService;

/**
 * This class contains logic of a screen which displays lists of resources assigned
 * to queried categories.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryResultsResourceList.java,v 1.5 2005-02-09 22:22:13 rafal Exp $
 */
public class CategoryQueryResultsResourceList
extends DocumentResourceList
{
    protected CategoryQueryResource query;
	protected CategoryQueryResultsConfiguration config;
	
    public CategoryQueryResultsResourceList(
        Context context, IntegrationService integrationService,
        CmsDataFactory cmsDataFactory,  CategoryQueryService categoryQueryService,
        SiteService siteService,
        CategoryQueryResource query,
        CategoryQueryResultsConfiguration config)
    {
        super(context,integrationService, cmsDataFactory, categoryQueryService, siteService);
        this.query = query;
        this.config = config;
	}

    public BaseResourceListConfiguration createConfig()
    throws ProcessingException
    {
        return config;
    }
    
    public String getQuery(CoralSession coralSession, BaseResourceListConfiguration config)
    throws ProcessingException
    {
		return query.getQuery();
    }
    
    public String getTableStateName()
    {
        return "net.cyklotron.cms.category.category_query_results."+query.getPath();
    }

    /* (non-Javadoc)
     * @see net.cyklotron.cms.modules.components.category.BaseResourceList#getResourceClasses(net.labeo.webcore.RunData, net.cyklotron.cms.category.BaseResourceListConfiguration)
     */
    protected String[] getResourceClasses(CoralSession coralSession, BaseResourceListConfiguration config)
	throws ProcessingException
    {
        int offset = ((CategoryQueryResultsConfiguration) config).getPublicationTimeOffset();
        if(offset == -1)
        {
    		String[] resClassNames = null;
    		CategoryQueryResource categoryQuery = getCategoryQueryRes(coralSession, config);
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
        BaseResourceListConfiguration config, TableState state, Parameters parameters)
    {
        if(state.isNew())
        {
            state.setPageSize(config.getMaxResNumber());
            state.setCurrentPage(1);
        }
        
        // WARN: duplicate setPage action
        if(parameters.isDefined(TableConstants.TABLE_ID_PARAM_KEY) &&
            parameters.getInt(TableConstants.TABLE_ID_PARAM_KEY) == state.getId())
        {
            state.setCurrentPage(
                parameters.getInt(TableConstants.PAGE_NO_PARAM_KEY,1));
        }
        else
        {
            state.setCurrentPage(1);
        }
    }

    // implementation /////////////////////////////////////////////////////////////////////////////
    
    protected CategoryQueryResource getCategoryQueryRes(
    	CoralSession coralSession, BaseResourceListConfiguration config)
    	throws ProcessingException
	{
		return query;
	}
}
