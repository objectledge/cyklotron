package net.cyklotron.cms.category;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsTool;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryUtil.java,v 1.1 2005-01-12 20:44:28 pablo Exp $
 */
public class CategoryUtil
{
    public static CategoryResource getCategory(ResourceService resourceService, RunData data)
        throws ProcessingException
    {
        long cat_id = data.getParameters().get("cat_id").asLong(-1);
        if(cat_id == -1)
        {
            throw new ProcessingException("The parameter cat_id is not defined");
        }

        try
        {
            return CategoryResourceImpl.getCategoryResource(resourceService, cat_id);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("category resource does not exist",e);
        }
    }
    
    /**
     * Checks if the current user has the specific permission on the current category.
     */
    public static boolean checkPermission(ResourceService resourceService, RunData data,
                                          String permissionName)
        throws ProcessingException
    {
        try
        {
            long id;
            if(data.getParameters().get("cat_id").isDefined())
            {
                id = data.getParameters().get("cat_id").asLong();
            }
            else if(data.getParameters().get("site_id").isDefined())
            {
                id = data.getParameters().get("site_id").asLong();
            }
            else
            {
                id = -1;
            }

            Resource res;
            if(id != -1)
            {
                res = resourceService.getStore().getResource(id);
            }
            else
            {
                Resource[] ress = resourceService.getStore().getResourceByPath("/cms/categories");
                res = ress[0];
            }
            Permission permission = resourceService.getSecurity().
                getUniquePermission(permissionName);
            return CmsTool.getSubject(data).hasPermission(res, permission);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check security", e);
        }
    }
}
