package net.cyklotron.cms.modules.actions.category;

import java.util.Iterator;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryException;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: RemoveResources.java,v 1.2 2005-01-24 10:27:04 pablo Exp $
 */
public class RemoveResources extends BaseCategorizationAction
{
    
    public RemoveResources(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CategoryService categoryService,
        IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, categoryService, integrationService);
        // TODO Auto-generated constructor stub
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();

        // get resources id
        Set resourceIds = ResourceSelectionState.getIds(parameters, "res_id");

        Resource[] resources = new Resource[resourceIds.size()];
        try
        {
            int j = 0;
            for(Iterator i=resourceIds.iterator(); i.hasNext();)
            {
               long id = ((Long)(i.next())).longValue();
               Resource res = coralSession.getStore().getResource(id);
               resources[j] = res;
               j++;
            }
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            logger.error("EntityDoesNotExistException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }

        CategoryResource category = getCategory(coralSession, parameters);

        // remove resources
        try
        {
            categoryService.removeFromCategory(coralSession, resources, category);
        }
        catch(CategoryException e)
        {
            templatingContext.put("result","exception");
            logger.error("CategoryException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }

        templatingContext.put("result","removed_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.category.modify");
    }
}
