package net.cyklotron.cms.modules.actions.security;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
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
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;

public class CreateRole
    extends BaseSecurityAction
{
    public CreateRole(Logger logger, StructureService structureService,
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
            
            long parentRoleId = parameters.getLong("parent_id", -1);
            Role parentRole = null;
            if(parentRoleId != -1)
            {
                parentRole = coralSession.getSecurity().getRole(parentRoleId);
            }
            else
            {
                SiteResource site = getSite(context);
                if(site == null)
                {
                    parentRole = coralSession.getSecurity().
                        getUniqueRole("cms.administrator");
                }
                else
                {
                    parentRole = site.getAdministrator();
                }
            }
            cmsSecurityService.createRole(coralSession, parentRole, roleName, resource);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to create role", e);
        }
    }
}
