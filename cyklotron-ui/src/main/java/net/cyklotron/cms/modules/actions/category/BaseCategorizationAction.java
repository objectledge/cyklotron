package net.cyklotron.cms.modules.actions.category;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.CategoryResource;

/**
 * Base action for all category actions dealing with categorized resource.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BaseCategorizationAction.java,v 1.1 2005-01-24 04:33:58 pablo Exp $
 */
public abstract class BaseCategorizationAction extends BaseCategoryAction
{
    /**
     * Returns a resource to be operated on. Relies on <code>res_id</code> parameter.
     */
    protected Resource getResource(RunData data)
        throws ProcessingException
    {
        // prepare categorized resource
        long res_id = parameters.getLong("res_id", -1);
        Resource resource;
        if(res_id == -1)
        {
            throw new ProcessingException("Parameter res_id is not defined");
        }
        else
        {
            try
            {
                resource = coralSession.getStore().getResource(res_id);
                if(resource instanceof CategoryResource)
                {
                    throw new ProcessingException("Cannot categorize categories");
                }
            }
            catch(EntityDoesNotExistException e)
            {
                throw new ProcessingException("resource does not exist",e);
            }
        }
        return resource;
    }

    /**
     * Checks the current user's privileges to running this action.
     *
     * <p>This action requires that the current user has "cms.category.categorize"
     * on a categorized resource specified by the "res_id" request parameter.</p>
     * 
     * @param data the RunData.
     * @throws ProcessingException if the privileges could not be determined.
     */
    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        try
        {
            // prepare categorized resource
            Resource res = getResource(data);
            Permission perm = coralSession.getSecurity().
                getUniquePermission("cms.category.categorize");
            return coralSession.getUserSubject().hasPermission(res, perm);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check access privileges", e);
        }
    }
}
