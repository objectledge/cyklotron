package net.cyklotron.cms.modules.actions.category.query;

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
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryPoolResourceData;
import net.cyklotron.cms.category.query.CategoryQueryPoolResourceImpl;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Category query pool adding action.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryPoolAdd.java,v 1.5 2005-03-09 09:59:03 pablo Exp $
 */
public class CategoryQueryPoolAdd
    extends BaseCategoryQueryAction
{
    
    private final CoralSessionFactory coralSessionFactory;

    public CategoryQueryPoolAdd(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CategoryQueryService categoryQueryService,
        CategoryService categoryService, SiteService siteService, 
        CoralSessionFactory coralSessionFactory)
    {
        super(logger, structureService, cmsDataFactory, categoryQueryService, categoryService,
                        siteService);
        this.coralSessionFactory = coralSessionFactory;
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();

		CategoryQueryPoolResourceData poolData = CategoryQueryPoolResourceData.getData(httpContext, null);
        poolData.update(parameters);
        
        if(poolData.getName().equals(""))
        {
            templatingContext.put("result", "name_empty");
            return;
        }
        
        SiteResource site = getSite(context);
        try
        {
			Resource root = categoryQueryService.getCategoryQueryPoolRoot(coralSession, site);

            if(coralSession.getStore().getResource(root, poolData.getName()).length > 0)
            {
                templatingContext.put("result","cannot_add_pools_with_the_same_name");
                return;
            }
            
			CategoryQueryPoolResource pool = CategoryQueryPoolResourceImpl
                .createCategoryQueryPoolResource(coralSession, poolData.getName(), root);
            
            pool.setDescription(poolData.getDescription());
            // set pool queries
            ResourceList newQueries = new ResourceList(coralSessionFactory, poolData.getQueriesSelectionState()
                .getEntities(coralSession, "selected").keySet());
            pool.setQueries(newQueries);
            pool.update();
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            logger.error("problem adding a category query pool for site '"+site.getName()+"'", e);
            return;
        }

		CategoryQueryPoolResourceData.removeData(httpContext, null);
        mvcContext.setView("category.query.CategoryQueryPoolList");
        templatingContext.put("result","added_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.category.query.pool.add");
    }
}
