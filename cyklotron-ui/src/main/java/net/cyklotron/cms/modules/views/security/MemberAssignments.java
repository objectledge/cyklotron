package net.cyklotron.cms.modules.views.security;

import java.util.HashSet;
import java.util.Set;

import net.labeo.services.resource.RoleAssignment;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;

public class MemberAssignments
    extends BaseRoleScreen
{
    private static String TABLE_NAME = "cms.security.MemberAssignments";

    public MemberAssignments()
    {
        super();
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            templatingContext.put("roles", getRoleTable(data, site));
            templatingContext.put("path_tool", new PathTool(site));
            long subjectId = parameters.getLong("subject_id");
            Subject subject = coralSession.getSecurity().getSubject(subjectId);
            templatingContext.put("subject", subject);
            templatingContext.put("assigned", getAssignedRoles(subject));
        }
        catch(Exception e)
        {
            throw new ProcessingException("data access failed", e);
        }
    }

    public Set getAssignedRoles(Subject subject)
    {
        Set roles = new HashSet();
        RoleAssignment assignments[] = subject.getRoleAssignments();
        for(int i=0; i<assignments.length; i++)
        {
            roles.add(assignments[i].getRole());
        }
        return roles;
    }

    protected String getTableName()
    {
        return TABLE_NAME;
    }
}
