package net.cyklotron.cms.modules.actions.security;

import java.util.HashSet;
import java.util.Iterator;

import net.labeo.services.resource.Role;
import net.labeo.services.resource.RoleAssignment;
import net.labeo.services.resource.Subject;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

public class UpdateMemberAssignments
    extends BaseSecurityAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long subjectId = parameters.getLong("subject_id");
            Subject subject = coralSession.getSecurity().getSubject(subjectId);
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
            RoleAssignment[] subjectAssignments = subject.getRoleAssignments();
            HashSet subjectRoles = new HashSet();
            for(int i=0; i<subjectAssignments.length; i++)
            {
                subjectRoles.add(subjectAssignments[i].getRole());
            }
            Subject root = coralSession.getSecurity().getSubject(Subject.ROOT);
            Iterator i = roles.iterator();
            while(i.hasNext())
            {
                Role role = (Role)i.next();
                if(selectedRoles.contains(role))
                {
                    if(!subjectRoles.contains(role))
                    {
                        coralSession.getSecurity().grant(role, subject, false, root);
                    }
                }
                else
                {
                    if(subjectRoles.contains(role))
                    {
                        coralSession.getSecurity().revoke(role, subject, root);
                    }
                }
            }
            data.getContext().put("result", "updated_successfully");
        }
        catch(Exception e)
        {
            // log.error("AddMember", e);
            data.getContext().put("result", "exception");
            data.getContext().put("trace", StringUtils.stackTrace(e));
        }
    }
}
