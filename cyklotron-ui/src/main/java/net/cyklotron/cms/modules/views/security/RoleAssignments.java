package net.cyklotron.cms.modules.views.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.SubjectListTableModel;
import org.objectledge.coral.table.filter.PersonalDataFilter;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.PathTreeElement;
import org.objectledge.table.generic.PathTreeTableModel;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.security.BaseRoleScreen.PathTool;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.RoleResourceImpl;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

public class RoleAssignments
    extends BaseSecurityScreen
{
    private final UserManager userManager;

    private final SiteService siteService;

    public RoleAssignments(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SecurityService securityService, UserManager userManager, SiteService siteService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        securityService);
        this.userManager = userManager;
        this.siteService = siteService;
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            String prefix = "cms.security.RoleAssignments:";
            if(site != null)
            {
                prefix = prefix + site.getName();
            }
            templatingContext.put("subjectTable", getSubjectTable(site, prefix, parameters,
                templatingContext, i18nContext, coralSession));
            templatingContext.put("groupTable", getGroupTable(site, prefix, coralSession));
            long roleId = parameters.getLong("role_id");
            RoleResource role = RoleResourceImpl.getRoleResource(coralSession, roleId);
            templatingContext.put("role", role);
            templatingContext.put("assignedSubjects", getAssignedSubjects(role.getRole()));
            // assignedRoles
            // cms.registered
            // cms.anonymous
            templatingContext.put("path_tool", new PathTool(site));
        }
        catch(Exception e)
        {
            throw new ProcessingException("data access failed", e);
        }
    }

    private TableTool<Subject> getSubjectTable(SiteResource site, String prefix, Parameters parameters,
        TemplatingContext templatingContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException, TableException
    {
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
        TableModel<Subject> model = new SubjectListTableModel(subjects, i18nContext.getLocale(),
            properties, userManager);
        TableState state = tableStateManager.getState(context, prefix + ":subjects");
        if(state.isNew())
        {
            state.setTreeView(false);
            state.setPageSize(20);
        }
        List<TableFilter<Subject>> filters = new ArrayList<TableFilter<Subject>>();
        String filterPattern = parameters.get("filter", "");
        if(filterPattern.length() > 0)
        {
            filters.add(new PersonalDataFilter(properties, parameters.get("filter"), userManager));
            templatingContext.put("filter", filterPattern);
        }
        return new TableTool<Subject>(state, filters, model);
    }

    private TableTool<PathTreeElement> getGroupTable(SiteResource site, String prefix, CoralSession coralSession)
        throws CmsSecurityException, TableException
    {
        TableColumn<PathTreeElement> columns[] = null;
        PathTreeTableModel<PathTreeElement> model = new PathTreeTableModel<PathTreeElement>(columns);
        for (SiteResource s : siteService.getSites(coralSession))
        {
            PathTreeElement siteElm = new PathTreeElement(s.getName(), "site");
            siteElm.set("id", s.getIdString());
            model.bind("/" + siteElm.getName(), siteElm);
            for (RoleResource roleRes : cmsSecurityService.getGroups(coralSession, s))
            {
                PathTreeElement groupElm = new PathTreeElement(cmsSecurityService
                    .getShortGroupName(roleRes), "group");
                groupElm.set("id", roleRes.getRole().getIdString());
                model.bind("/" + siteElm.getName() + "/" + groupElm.getName(), groupElm);
            }
        }

        TableState state = tableStateManager.getState(context, prefix + ":groups");
        if(state.isNew())
        {
            state.setTreeView(true);
            state.setPageSize(0);
        }
        return new TableTool<PathTreeElement>(state, null, model);
    }

    private Set<Subject> getAssignedSubjects(Role role)
    {
        Set<Subject> subjects = new HashSet<Subject>();
        Subject assigned[] = role.getSubjects();
        for (int i = 0; i < assigned.length; i++)
        {
            subjects.add(assigned[i]);
        }
        return subjects;
    }
}
