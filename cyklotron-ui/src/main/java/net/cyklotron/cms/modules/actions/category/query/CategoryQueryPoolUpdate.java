package net.cyklotron.cms.modules.actions.category.query;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryPoolResourceData;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 * An action for index pool modification.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryPoolUpdate.java,v 1.3 2005-01-25 03:22:19 pablo Exp $
 */
public class CategoryQueryPoolUpdate
	extends BaseCategoryQueryAction
{
    public CategoryQueryPoolUpdate(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CategoryQueryService categoryQueryService,
        CategoryService categoryService, SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, categoryQueryService, categoryService,
                        siteService);
        // TODO Auto-generated constructor stub
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();

        CategoryQueryPoolResource pool = getPool(coralSession, parameters);

        CategoryQueryPoolResourceData poolData = CategoryQueryPoolResourceData.getData(httpContext, pool);
        poolData.update(parameters);
       
		if(poolData.getName().equals(""))
		{
			templatingContext.put("result", "name_empty");
			return;
		}

		if(!poolData.getName().equals(pool.getName()))
		{
			Resource parent = pool.getParent(); 
			if(coralSession.getStore().getResource(parent, poolData.getName()).length > 0)
			{
				templatingContext.put("result","cannot_have_the_same_name_as_other");
				return;
			}
			coralSession.getStore().setName(pool, poolData.getName());
		}
       
        pool.setDescription(poolData.getDescription());

        // set pool indexes
        ResourceList newQueries = new ResourceList(coralSession.getStore(), poolData.getQueriesSelectionState()
            .getEntities(coralSession, "selected").keySet());
        pool.setQueries(newQueries);

        pool.update();
        
		CategoryQueryPoolResourceData.removeData(httpContext, pool);
        mvcContext.setView("category,query,CategoryQueryPoolList");
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.category.query.pool.modify");
    }
}
