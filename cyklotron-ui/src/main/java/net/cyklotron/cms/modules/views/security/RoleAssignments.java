package net.cyklotron.cms.modules.views.security;

import java.util.HashSet;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.RoleResourceImpl;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;

public class RoleAssignments
    extends BaseRoleScreen
{

    
    public RoleAssignments(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SecurityService securityService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        securityService);
        
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


