package net.cyklotron.cms.modules.views.security;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

import net.labeo.services.resource.Entity;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.RoleImplication;
import net.labeo.services.table.ListTableModel;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.RoleResourceImpl;
import net.cyklotron.cms.site.SiteResource;

public class GroupRoleAssignments
    extends BaseRoleScreen
{
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
            templatingContext.put("path_tool", new PathTool(site));
            Role workgroup = coralSession.getSecurity().getUniqueRole("cms.workgroup");
            RoleImplication[] implications = workgroup.getImplications();
            ArrayList workgroups = new ArrayList(implications.length);
            for(int i=0; i<implications.length; i++)
            {
                if(implications[i].getSuperRole().equals(workgroup))
                {
                    workgroups.add(implications[i].getSubRole());
                }
            }
            Comparator comp = new EntityNameComparator(i18nContext.getLocale()());
            TableColumn[] cols = new TableColumn[1];
            cols[0] = new TableColumn("name", comp);
            TableModel model = new ListTableModel(workgroups, cols);
            TableState state = tableService.
                getLocalState(data, "screens:cms:security,GroupRoleAssignments:"+site.getIdString());
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
                state.setSortColumnName("name");
            }
            TableTool table = new TableTool(state, model, null);
            templatingContext.put("table", table);

            Role registered = coralSession.getSecurity().
                getUniqueRole("cms.registered");
            if(role.getRole().isSuperRole(registered))
            {
                templatingContext.put("registered", Boolean.TRUE);
            }
            Role anonymous = coralSession.getSecurity().
                getUniqueRole("cms.anonymous");
            if(role.getRole().isSuperRole(anonymous))
            {
                templatingContext.put("anonymous", Boolean.TRUE);
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("data access failed", e);
        }
    }

    protected String getTableName()
    {
        throw new UnsupportedOperationException("tables handled differently here");
    }

    public static class EntityNameComparator
        implements Comparator
    {
        Collator collator;

        public EntityNameComparator(Locale locale)
        {
            collator = Collator.getInstance(locale);
        }

        public int compare(Object o1, Object o2)
        {
            Entity e1 = (Entity)o1;
            Entity e2 = (Entity)o2;
            return collator.compare(e1.getName(), e2.getName());
        }
    }
}
