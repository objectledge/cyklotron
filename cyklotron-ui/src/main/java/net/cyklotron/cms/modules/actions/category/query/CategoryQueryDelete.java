package net.cyklotron.cms.modules.actions.category.query;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryDelete.java,v 1.4 2005-03-09 09:59:03 pablo Exp $
 */
public class CategoryQueryDelete
	extends BaseCategoryQueryAction
{
    
    
    public CategoryQueryDelete(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CategoryQueryService categoryQueryService,
        CategoryService categoryService, SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, categoryQueryService, categoryService,
                        siteService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        CategoryQueryResource query = getQuery(coralSession, parameters);

        SiteResource site = getSite(context);
        Resource pools[];
        try
        {
            Resource poolRoot = categoryQueryService.getCategoryQueryPoolRoot(coralSession, site);
            pools = coralSession.getStore().getResource(poolRoot);
        }
        catch (CategoryQueryException e)
        {
            throw new ProcessingException("failed to lookup query pool root", e);
        }
        for(int i=0; i<pools.length; i++)
        {
            CategoryQueryPoolResource pool = (CategoryQueryPoolResource)pools[i];
            if(pool.getQueries().contains(query))
            {
                mvcContext.setView("category.query.CategoryQueryInUse");
                return;
            }
        }

        try
        {
            coralSession.getStore().deleteResource(query);
        }
        catch(EntityInUseException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            logger.error("problem deleting the category query '"+query.getIdString()+"'", e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.category.query.delete");
    }
}
