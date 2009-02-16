package net.cyklotron.cms.modules.views.security;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.Entity;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.RoleImplication;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.ListTableModel;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.RoleResourceImpl;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;

public class GroupRoleAssignments
    extends BaseRoleScreen
{
    
    
    public GroupRoleAssignments(org.objectledge.context.Context context, Logger logger,
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
            if(site != null)
            {
                templatingContext.put("subjects", site.getTeamMember().getSubjects());
            }
            else
            {
                templatingContext.put("subjects", coralSession.getSecurity().getSubject());
            }
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
            Comparator comp = new EntityNameComparator(i18nContext.getLocale());
            TableColumn[] cols = new TableColumn[1];
            cols[0] = new TableColumn("name", comp);
            TableModel model = new ListTableModel(workgroups, cols);
            String suffix = "";
            if(site != null)
            {
                suffix = site.getName();
            }
            TableState state = tableStateManager.
                getState(context, "screens:cms:security,GroupRoleAssignments:"+suffix);
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
                state.setSortColumnName("name");
            }
            TableTool table = new TableTool(state, null, model);
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
