package net.cyklotron.cms.modules.actions.category;

import java.util.Iterator;
import java.util.Set;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.util.ResourceSelectionState;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.CategoryException;
import net.cyklotron.cms.category.CategoryResource;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: RemoveResources.java,v 1.1 2005-01-24 04:33:58 pablo Exp $
 */
public class RemoveResources extends BaseCategorizationAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
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
            log.error("EntityDoesNotExistException: ",e);
            templatingContext.put("trace", StringUtils.stackTrace(e));
            return;
        }

        CategoryResource category = getCategory(data);

        // remove resources
        try
        {
            categoryService.removeFromCategory(resources, category, subject);
        }
        catch(CategoryException e)
        {
            templatingContext.put("result","exception");
            log.error("CategoryException: ",e);
            templatingContext.put("trace", StringUtils.stackTrace(e));
            return;
        }

        templatingContext.put("result","removed_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.modify");
    }
}
