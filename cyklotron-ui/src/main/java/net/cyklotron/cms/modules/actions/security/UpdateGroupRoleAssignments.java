package net.cyklotron.cms.modules.actions.security;

import java.util.HashSet;
import java.util.Iterator;

import net.labeo.services.resource.Role;
import net.labeo.services.resource.RoleImplication;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.security.RoleResource;

public class UpdateGroupRoleAssignments
    extends BaseSecurityAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long roleId = parameters.getLong("the_role_id");
            RoleResource roleRes = (RoleResource)coralSession.getStore().getResource(roleId);
            Role role = roleRes.getRole();
            Parameter[] roleIds = parameters.getArray("role_id");
            HashSet roles = new HashSet();
            for(int i=0; i<roleIds.length; i++)
            {
                roles.add(coralSession.getSecurity().getRole(roleIds[i].asLong()));
            }
            Parameter[] selectedRoleIds = parameters.getArray("selected_role_id");
            HashSet selectedRoles = new HashSet();
            for(int i=0; i<selectedRoleIds.length; i++)
            {
                selectedRoles.add(coralSession.getSecurity().getRole(selectedRoleIds[i].asLong()));
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
            if(parameters.get("registered").asBoolean(false))
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
            if(parameters.get("anonymous").asBoolean(false))
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
            data.getContext().put("result", "updated_successfully");
        }
        catch(Exception e)
        {
            // log.error("AddMember", e);
            data.getContext().put("result", "exception");
            data.getContext().put("trace", new StackTrace(e));
        }
    }
}
