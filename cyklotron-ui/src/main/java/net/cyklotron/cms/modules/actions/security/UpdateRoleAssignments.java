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

import net.cyklotron.cms.security.RoleResource;

public class UpdateRoleAssignments
    extends BaseSecurityAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long roleId = parameters.getLong("role_id");
            RoleResource roleRes = (RoleResource)coralSession.getStore().getResource(roleId);
            Role role = roleRes.getRole();
            Parameter[] subjectIds = parameters.getArray("subject_id");
            HashSet subjects = new HashSet();
            for(int i=0; i<subjectIds.length; i++)
            {
                subjects.add(coralSession.getSecurity().getSubject(subjectIds[i].asLong()));
            }
            Parameter[] selectedSubjectIds = parameters.getArray("selected_subject_id");
            HashSet selectedSubjects = new HashSet();
            for(int i=0; i<selectedSubjectIds.length; i++)
            {
                selectedSubjects.add(coralSession.getSecurity().getSubject(selectedSubjectIds[i].asLong()));
            }
            RoleAssignment[] roleAssignments = role.getRoleAssignments();
            HashSet roleSubjects = new HashSet();
            for(int i=0; i<roleAssignments.length; i++)
            {
                roleSubjects.add(roleAssignments[i].getSubject());
            }
            Subject root = coralSession.getSecurity().getSubject(Subject.ROOT);
            Iterator i = subjects.iterator();
            while(i.hasNext())
            {
                Subject subject = (Subject)i.next();
                if(selectedSubjects.contains(subject))
                {
                    if(!roleSubjects.contains(subject))
                    {
                        coralSession.getSecurity().grant(role, subject, false, root);
                    }
                }
                else
                {
                    if(roleSubjects.contains(subject))
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
            data.getContext().put("trace", new StackTrace(e));
        }
    }
}
