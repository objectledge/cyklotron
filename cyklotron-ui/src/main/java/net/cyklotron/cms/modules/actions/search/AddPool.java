package net.cyklotron.cms.modules.actions.search;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.PoolResourceData;
import net.cyklotron.cms.search.PoolResourceImpl;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;

/**
 * Index pool adding action.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AddPool.java,v 1.5 2005-03-08 10:53:37 pablo Exp $
 */
public class AddPool
    extends BaseSearchAction
{
    private final CoralSessionFactory coralSessionFactory;

    public AddPool(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
        SearchService searchService, CoralSessionFactory coralSessionFactory)
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

        PoolResourceData poolData = PoolResourceData.getData(httpContext, null);
        poolData.update(parameters);
        
        if(poolData.getName().equals(""))
        {
            templatingContext.put("result", "name_empty");
            return;
        }
        
        SiteResource site = getSite(context);
        try
        {
            Resource root = searchService.getPoolsRoot(coralSession, site);

            if(coralSession.getStore().getResource(root, poolData.getName()).length > 0)
            {
                templatingContext.put("result","cannot_add_pools_with_the_same_name");
                return;
            }
            
            PoolResource pool = PoolResourceImpl
                .createPoolResource(coralSession, poolData.getName(), root);
            
            pool.setDescription(poolData.getDescription());
            // set pool indexes
            ResourceList newIndexes = new ResourceList(coralSessionFactory,
                poolData.getIndexesSelectionState()
                .getEntities(coralSession, "selected").keySet());
            pool.setIndexes(newIndexes);
            
            pool.update();
        }
        catch(SearchException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("problem adding an index pool for site '"+site.getName()+"'", e);
            return;
        }
        PoolResourceData.removeData(httpContext, null);
        mvcContext.setView("search,PoolList");
        templatingContext.put("result","added_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.search.pool.add");
    }
}
