package net.cyklotron.cms.modules.actions.search;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.PoolResourceData;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.structure.StructureService;

/**
 * An action for index pool modification.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdatePool.java,v 1.5 2005-03-08 10:53:37 pablo Exp $
 */
public class UpdatePool extends BaseSearchAction
{
    private final CoralSessionFactory coralSessionFactory;

    public UpdatePool(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SearchService searchService, 
        CoralSessionFactory coralSessionFactory)
    {
        super(logger, structureService, cmsDataFactory, searchService);
        
        this.coralSessionFactory = coralSessionFactory;
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        
        Subject subject = coralSession.getUserSubject();

        PoolResource pool = getPool(coralSession, parameters);

        PoolResourceData poolData = PoolResourceData.getData(httpContext, pool);
        poolData.update(parameters);
        PoolResourceData.removeData(httpContext, pool);
        
        pool.setDescription(poolData.getDescription());

        // set pool indexes
        ResourceList newIndexes = new ResourceList(coralSessionFactory, poolData.getIndexesSelectionState()
            .getEntities(coralSession, "selected").keySet());
        pool.setIndexes(newIndexes);

        pool.update();
        
        mvcContext.setView("search,PoolList");
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.search.pool.modify");
    }
}
