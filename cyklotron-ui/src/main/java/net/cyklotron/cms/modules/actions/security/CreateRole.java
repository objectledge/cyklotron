package net.cyklotron.cms.modules.actions.security;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

public class CreateRole
    extends BaseSecurityAction
{
    public CreateRole()
    {
        super();
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            Subject subject = coralSession.getUserSubject();
            String roleName = parameters.get("role","");
            if(roleName.length() == 0)
            {
                throw new ProcessingException("Couldn't find role_name parameter");
            }
            long resourceId = parameters.getLong("on_id", -1);
            if(resourceId == -1)
            {
                throw new ProcessingException("Couldn't find resource_id parameter");
            }
            Resource resource = coralSession.getStore().getResource(resourceId);
            
            long parentRoleId = parameters.getLong("parent_id", -1);
            Role parentRole = null;
            if(parentRoleId != -1)
            {
                parentRole = coralSession.getSecurity().getRole(parentRoleId);
            }
            else
            {
                parentRole = getSite(context).getAdministrator();
            }
            cmsSecurityService.createRole(parentRole, roleName, resource, subject);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to create role", e);
        }
    }
}
