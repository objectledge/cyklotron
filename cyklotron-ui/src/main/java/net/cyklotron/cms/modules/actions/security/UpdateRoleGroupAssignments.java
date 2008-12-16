package net.cyklotron.cms.modules.actions.security;

import java.util.HashSet;
import java.util.Iterator;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.RoleImplication;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.structure.StructureService;

public class UpdateRoleGroupAssignments
    extends BaseSecurityAction
{
    
    public UpdateRoleGroupAssignments(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SecurityService cmsSecurityService, UserManager userManager)
    {
        super(logger, structureService, cmsDataFactory, cmsSecurityService, userManager);
        
    }
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long roleId = parameters.getLong("the_role_id");
            RoleResource roleRes = (RoleResource)coralSession.getStore().getResource(roleId);
            Role role = roleRes.getRole();
            long[] roleIds = parameters.getLongs("role_id");
            HashSet roles = new HashSet();
            for(int i=0; i<roleIds.length; i++)
            {
                roles.add(coralSession.getSecurity().getRole(roleIds[i]));
            }
            long[] selectedRoleIds = parameters.getLongs("selected_role_id");
            HashSet selectedRoles = new HashSet();
            for(int i=0; i<selectedRoleIds.length; i++)
            {
                selectedRoles.add(coralSession.getSecurity().getRole(selectedRoleIds[i]));
            }
            RoleImplication[] implications = role.getImplications();
            HashSet superRoles = new HashSet();
            for(int i=0; i<implications.length; i++)
            {
                if(implications[i].getSubRole().equals(role))
                {
                    superRoles.add(implications[i].getSuperRole());
                }
            }
            Iterator i = roles.iterator();
            while(i.hasNext())
            {
                Role aRole = (Role)i.next();
                if(selectedRoles.contains(aRole))
                {
                    if(!superRoles.contains(aRole))
                    {
                        coralSession.getSecurity().addSubRole(aRole, role);
                    }
                }
                else
                {
                    if(superRoles.contains(aRole))
                    {
                        coralSession.getSecurity().deleteSubRole(aRole, role);
                    }
                }
            }
            Role registered = coralSession.getSecurity().
                getUniqueRole("cms.registered");
            if(parameters.getBoolean("registered",false))
            {
                if(!registered.isSubRole(role))
                {
                    coralSession.getSecurity().addSubRole(registered, role);
                }
            }
            else
            {
                if(registered.isSubRole(role))
                {
                    coralSession.getSecurity().deleteSubRole(registered, role);
                }
            }
            Role anonymous = coralSession.getSecurity().
                getUniqueRole("cms.anonymous");
            if(parameters.getBoolean("anonymous",false))
            {
                if(!anonymous.isSubRole(role))
                {
                    coralSession.getSecurity().addSubRole(anonymous, role);
                }
            }
            else
            {
                if(anonymous.isSubRole(role))
                {
                    coralSession.getSecurity().deleteSubRole(anonymous, role);
                }
            }
            templatingContext.put("result", "updated_successfully");
        }
        catch(Exception e)
        {
            // log.error("AddMember", e);
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
    }
}
