package net.cyklotron.cms.modules.actions.security;

import java.util.HashSet;

import net.labeo.services.resource.Role;
import net.labeo.services.resource.RoleAssignment;
import net.labeo.services.resource.Subject;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.site.SiteResource;

public class DismissMember
    extends BaseSecurityAction
{
    public DismissMember()
    {
        super();
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long subjectId = parameters.getLong("subject_id");
            Subject subject = coralSession.getSecurity().getSubject(subjectId);
            SiteResource site = getSite(context);
            RoleResource[] siteRoles = cmsSecurityService.getRoles(site);
            Subject root = coralSession.getSecurity().getSubject(Subject.ROOT);
            HashSet roles = new HashSet();
            RoleAssignment[] assignments = subject.getRoleAssignments();
            for(int i=0; i<assignments.length; i++)
            {
                roles.add(assignments[i].getRole());
            }
            for(int i=0; i<siteRoles.length; i++)
            {
                Role role = siteRoles[i].getRole();
                if(roles.contains(role))
                {
                    coralSession.getSecurity().revoke(role, subject, root);
                }
            }
            data.getContext().put("result", "deleted_successfully");
        }
        catch(Exception e)
        {
            // log.error("AddMember", e);
            data.getContext().put("result", "exception");
            data.getContext().put("trace", new StackTrace(e));
        }
    }
}

