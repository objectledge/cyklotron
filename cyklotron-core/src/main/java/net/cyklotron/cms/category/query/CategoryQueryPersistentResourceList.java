package net.cyklotron.cms.category.query;
    
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
import net.cyklotron.cms.structure.StructureService;

/**
 * This class contains logic of a screen which displays lists of resources assigned
 * to queried categories.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryResultsResourceList.java,v 1.6 2005-05-17 06:20:21 zwierzem Exp $
 */
public class CategoryQueryPersistentResourceList
extends DocumentResourceList
{
    protected CategoryQueryResource query;
	protected CategoryQueryPersistentListConfiguration config;
	
    public CategoryQueryPersistentResourceList(
        Context context, IntegrationService integrationService,
        CmsDataFactory cmsDataFactory,  CategoryQueryService categoryQueryService,
        SiteService siteService, StructureService structureService,
        CategoryQueryResource query,
        CategoryQueryPersistentListConfiguration config)
    {
        super(context,integrationService, cmsDataFactory, categoryQueryService, siteService, structureService);
        this.query = query;
        this.config = config;
	}

    public BaseResourceListConfiguration createConfig()
    throws ProcessingException
    {
        return config;
    }
    
    public CategoryQueryResource getQueryResource()
    {
        return query;
    }
    
    public String getQuery(CoralSession coralSession, BaseResourceListConfiguration config)
    throws ProcessingException
    {
		return query.getQuery();
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
