package net.cyklotron.cms.modules.actions.security;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.structure.StructureService;

public class DeleteRole
    extends BaseSecurityAction
{
    
    public DeleteRole(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SecurityService cmsSecurityService, UserManager userManager)
    {
        super(logger, structureService, cmsDataFactory, cmsSecurityService, userManager);
        
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
            cmsSecurityService.deleteRole(coralSession, roleName, resource, false);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to delete role", e);
        }
    }
}
