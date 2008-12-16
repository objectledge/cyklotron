package net.cyklotron.cms.modules.views.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.SubjectListTableModel;
import org.objectledge.coral.table.filter.PersonalDataFilter;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.RoleResourceImpl;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;

public class RoleUserAssignments
    extends BaseRoleScreen
{
    private final UserManager userManager;

    public RoleUserAssignments(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SecurityService securityService,
        UserManager userManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        securityService);
        this.userManager = userManager;
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            Subject[] subjects;
            if(site != null)
            {
                subjects = site.getTeamMember().getSubjects();
            }
            else
            {
                subjects = coralSession.getSecurity().getSubject();
            }
            String properties[] = { "uid", "sn", "givenName", "mail" };
            TableModel model = new SubjectListTableModel(subjects, i18nContext.getLocale(),
                properties, userManager);
            String suffix = "";
            if(site != null)
            {
                suffix = site.getName();
            }
            TableState state = tableStateManager.getState(context, "cms.security.RoleAssignments:"+suffix);
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(20);
            }
            List<TableFilter<Subject>> filters = new ArrayList<TableFilter<Subject>>();
            String filterPattern = parameters.get("filter", "");
            if(filterPattern.length() > 0)
            {
                filters.add(new PersonalDataFilter(properties, parameters.get("filter"),
                    userManager));
                templatingContext.put("filter", filterPattern);
            }
            templatingContext.put("table", new TableTool(state, filters, model));
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


