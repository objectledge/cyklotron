package net.cyklotron.cms.modules.views.security;

import static org.objectledge.coral.entity.EntityUtils.entitiesToIds;
import static org.objectledge.coral.entity.EntityUtils.idsToEntitySet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.Entity;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.entity.EntityFactory;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.RoleAssignment;
import org.objectledge.coral.security.RoleImplication;
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
        final CoralSession coralSession)
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
            if(site != null)
            {
                templatingContext.put("security", new SecurityServiceHelper(cmsSecurityService, coralSession));
                templatingContext.put("siteGroups", cmsSecurityService
                    .getGroups(coralSession, site));
            }
            templatingContext.put("groupTable", getGroupTable(site, prefix, coralSession,
                i18nContext));
            long roleId = parameters.getLong("role_id");
            RoleResource role = RoleResourceImpl.getRoleResource(coralSession, roleId);
            templatingContext.put("role", role);
            Role registered = coralSession.getSecurity().getUniqueRole("cms.registered");
            templatingContext.put("registeredRole", registered);
            Role anonymous = coralSession.getSecurity().getUniqueRole("cms.anonymous");
            templatingContext.put("anonymousRole", anonymous);
            templatingContext.put("path_tool", new PathTool(site));

            // supporting for saving changes made on different pages of the table views
            if(parameters.isDefined("all_selected_subject_ids"))
            {
                Set<Subject> assignedSubjects = updateSelectedEntities(parameters,
                    templatingContext, "subject", coralSession.getSecurity().getSubjectFactory());
                templatingContext.put("assignedSubjects", assignedSubjects);
                Set<Role> assignedGroups = updateSelectedEntities(parameters,
                    templatingContext, "group", coralSession.getSecurity().getRoleFactory());
                templatingContext.put("assignedGroups", assignedGroups);
                if(!assignedSubjects.equals(getAssignedSubjects(role.getRole()))
                    || !assignedGroups.equals(getAssignedGroups(role.getRole())))
                {
                    templatingContext.put("unsaved_changes", true);
                }
            }
            else
            {
                // it's the first time the view is displayed
                Set<Subject> assignedSubjects = getAssignedSubjects(role.getRole());
                templatingContext.put("assignedSubjects", assignedSubjects);
                templatingContext.put("all_selected_subject_ids", entitiesToIds(assignedSubjects));
                Set<Role> assignedRoles = getAssignedGroups(role.getRole());
                templatingContext.put("assignedGroups", assignedRoles);
                templatingContext.put("all_selected_group_ids", entitiesToIds(assignedRoles));
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("data access failed", e);
        }
    }

    private <E extends Entity> Set<E> updateSelectedEntities(Parameters parameters,
        TemplatingContext templatingContext, String entity, EntityFactory<E> entityFactory)
        throws EntityDoesNotExistException
    {
        Set<E> assignedEntities = getSelectedEntities(parameters, entity, entityFactory);
        templatingContext.put("all_selected_" + entity + "_ids", entitiesToIds(assignedEntities));
        return assignedEntities;
    }

    private TableTool<Subject> getSubjectTable(SiteResource site, String prefix,
        Parameters parameters, TemplatingContext templatingContext, I18nContext i18nContext,
        CoralSession coralSession)
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

    private TableTool<PathTreeElement> getGroupTable(SiteResource site, String prefix,
        CoralSession coralSession, I18nContext i18nContext)
        throws CmsSecurityException, TableException
    {
        TableColumn<PathTreeElement> columns[] = new TableColumn[1];
        columns[0] = new TableColumn<PathTreeElement>("element", PathTreeElement.getComparator(
            "name", i18nContext.getLocale()));
        PathTreeTableModel<PathTreeElement> model = new PathTreeTableModel<PathTreeElement>(columns);
        model.bind("/", new PathTreeElement("groups", "root"));
        for (SiteResource s : siteService.getSites(coralSession))
        {
            if(!s.equals(site))
            {
                PathTreeElement siteElm = new PathTreeElement(s.getName(), "site");
                model.bind("/" + siteElm.getName(), siteElm);
                PathTreeElement teamMemberElm = new PathTreeElement("@team_member", "team_member");
                teamMemberElm.set("role", s.getTeamMember());
                model.bind("/" + siteElm.getName() + "/" + "@team_member", teamMemberElm);
                for (RoleResource roleRes : cmsSecurityService.getGroups(coralSession, s))
                {
                    PathTreeElement groupElm = new PathTreeElement(cmsSecurityService
                        .getShortGroupName(roleRes), "group");
                    groupElm.set("role", roleRes.getRole());
                    model.bind("/" + siteElm.getName() + "/" + groupElm.getName(), groupElm);
                }
            }
        }

        TableState state = tableStateManager.getState(context, prefix + ":groups");
        if(state.isNew())
        {
            state.setTreeView(true);
            state.setShowRoot(false);
            state.setPageSize(0);
            state.setSortColumnName("element");
        }
        return new TableTool<PathTreeElement>(state, null, model);
    }

    public static <E extends Entity> Set<E> getSelectedEntities(Parameters parameters, String entity,
        EntityFactory<E> entityFactory)
        throws EntityDoesNotExistException
    {
        Set<E> assignedEntities = idsToEntitySet(parameters.get("all_selected_" + entity + "_ids"),
            entityFactory);
        assignedEntities.removeAll(idsToEntitySet(parameters.getStrings(entity + "_id"),
            entityFactory));
        assignedEntities.addAll(idsToEntitySet(parameters.getStrings("selected_" + entity + "_id"),
            entityFactory));
        return assignedEntities;
    }    

    public static Set<Subject> getAssignedSubjects(Role role)
    {
        Set<Subject> subjects = new HashSet<Subject>();
        RoleAssignment assigned[] = role.getRoleAssignments();
        for (RoleAssignment a : assigned)
        {
            subjects.add(a.getSubject());
        }
        return subjects;
    }

    public static Set<Role> getAssignedGroups(Role role)
    {
        Set<Role> roles = new HashSet<Role>();
        for (RoleImplication ri : role.getImplications())
        {
            if(ri.getSubRole().equals(role))
            {
                roles.add(ri.getSuperRole());
            }
        }
        return roles;
    }
}
