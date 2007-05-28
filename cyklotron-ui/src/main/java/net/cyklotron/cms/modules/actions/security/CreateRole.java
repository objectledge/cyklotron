package net.cyklotron.cms.modules.actions.security;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

public class CreateRole
    extends BaseSecurityAction
{
    private CoralSessionFactory coralSessionFactory;
    
    public CreateRole(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SecurityService cmsSecurityService, UserManager userManager,
        CoralSessionFactory coralSessionFactory)
    {
        super(logger, structureService, cmsDataFactory, cmsSecurityService, userManager);
        this.coralSessionFactory = coralSessionFactory;    
    }
    
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        CoralSession rootSession = coralSessionFactory.getRootSession();
        try
        {
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
            Resource resource = rootSession.getStore().getResource(resourceId);
            
            long parentRoleId = parameters.getLong("parent_id", -1);
            Role parentRole = null;
            if(parentRoleId != -1)
            {
                parentRole = rootSession.getSecurity().getRole(parentRoleId);
            }
            else
            {
                SiteResource site = getSite(context);
                if(site == null)
                {
                    parentRole = rootSession.getSecurity().
                        getUniqueRole("cms.administrator");
                }
                else
                {
                    parentRole = site.getAdministrator();
                }
            }
            cmsSecurityService.createRole(rootSession, parentRole, roleName, resource);
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));

        }
        finally
        {
            rootSession.close();
        }
    }
}
