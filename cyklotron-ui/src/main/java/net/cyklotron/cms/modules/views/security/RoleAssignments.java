package net.cyklotron.cms.modules.views.security;

import java.util.HashSet;
import java.util.Set;

import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.RoleResourceImpl;
import net.cyklotron.cms.site.SiteResource;

public class RoleAssignments
    extends BaseRoleScreen
{
    public RoleAssignments()
    {
        super();
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();

            templatingContext.put("subjects", site.getTeamMember().getSubjects());
            long roleId = parameters.getLong("role_id");
            RoleResource role = RoleResourceImpl.getRoleResource(coralSession, roleId);
            templatingContext.put("role", role);
            templatingContext.put("assigned", getAssignedSubjects(role.getRole()));
            templatingContext.put("path_tool", new PathTool(site));
        }
        catch(Exception e)
        {
            throw new ProcessingException("data access failed", e);
        }
    }

    public Set getAssignedSubjects(Role role)
    {
        Set subjects = new HashSet();
        Subject assigned[] = role.getSubjects();
        for(int i=0; i<assigned.length; i++)
        {
            subjects.add(assigned[i]);
        }
        return subjects;
    }

    protected String getTableName()
    {
        throw new UnsupportedOperationException("no tables used here");
    }
}


