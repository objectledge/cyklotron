package net.cyklotron.cms.modules.actions.search;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
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
 * @version $Id: UpdatePool.java,v 1.3 2005-01-25 07:15:11 pablo Exp $
 */
public class UpdatePool extends BaseSearchAction
{
    public UpdatePool(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SearchService searchService)
    {
        super(logger, structureService, cmsDataFactory, searchService);
        // TODO Auto-generated constructor stub
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
        ResourceList newIndexes = new ResourceList(coralSession.getStore(), poolData.getIndexesSelectionState()
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
