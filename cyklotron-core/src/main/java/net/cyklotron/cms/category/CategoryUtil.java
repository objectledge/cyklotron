package net.cyklotron.cms.category;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryUtil.java,v 1.2 2005-01-18 16:12:04 pablo Exp $
 */
public class CategoryUtil
{
    public static CategoryResource getCategory(CoralSession coralSession, Parameters parameters)
        throws ProcessingException
    {
        long cat_id = parameters.getLong("cat_id",-1);
        if(cat_id == -1)
        {
            throw new ProcessingException("The parameter cat_id is not defined");
        }

        try
        {
            return CategoryResourceImpl.getCategoryResource(coralSession, cat_id);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("category resource does not exist",e);
        }
    }
    
    /**
     * Checks if the current user has the specific permission on the current category.
     */
    public static boolean checkPermission(CoralSession coralSession, Parameters parameters,
                                          String permissionName)
        throws ProcessingException
    {
        try
        {
            long id;
            if(parameters.isDefined("cat_id"))
            {
                id = parameters.getLong("cat_id");
            }
            else if(parameters.isDefined("site_id"))
            {
                id = parameters.getLong("site_id");
            }
            else
            {
                id = -1;
            }

            Resource res;
            if(id != -1)
            {
                res = coralSession.getStore().getResource(id);
            }
            else
            {
                Resource[] ress = coralSession.getStore().getResourceByPath("/cms/categories");
                res = ress[0];
            }
            Permission permission = coralSession.getSecurity().
                getUniquePermission(permissionName);
            return coralSession.getUserSubject().hasPermission(res, permission);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check security", e);
        }
    }
}
