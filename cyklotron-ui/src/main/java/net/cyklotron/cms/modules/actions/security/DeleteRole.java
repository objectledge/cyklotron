package net.cyklotron.cms.modules.actions.security;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

public class DeleteRole
    extends BaseSecurityAction
{
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
            cmsSecurityService.deleteRole(roleName, resource, subject, false);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to delete role", e);
        }
    }
}
