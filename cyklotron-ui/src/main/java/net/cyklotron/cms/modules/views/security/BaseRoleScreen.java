package net.cyklotron.cms.modules.views.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.table.ARLTableModel;
import net.labeo.services.table.ExtendedTableModel;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableFilter;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.site.SiteResource;

/**
 * The default void screen assember for security sub-application.
 */
public abstract class BaseRoleScreen
    extends BaseSecurityScreen
{
    public TableTool getRoleTable(RunData data, SiteResource site)
        throws TableException
    {
        Resource rolesRoot = cmsSecurityService.getRoleInformationRoot(site);
        TableModel model = new ARLTableModel(i18nContext.getLocale()());
        TableState state = tableService.getLocalState(data, getTableName()+":"+site.getName());
        if(state.isNew())
        {
            state.setViewType(TableConstants.VIEW_AS_TREE);
            state.setCurrentPage(0);
            state.setShowRoot(false);
            Object[] firstLevel = ((ExtendedTableModel)model).getChildren(rolesRoot);
            for(int i=0; i<firstLevel.length; i++)
            {
                state.setExpanded(((Resource)firstLevel[i]).getIdString());
            }
        }
        String rootId = rolesRoot.getIdString();
        state.setSortColumnName("name");
        state.setRootId(rootId);
        state.setExpanded(rootId);
        final Role teamMember = site.getTeamMember();
        ArrayList filters = new ArrayList();
        filters.add(new TableFilter()
                    {
                        public boolean accept(Object o)
                        {
                            if(o instanceof RoleResource)
                            {
                                return ! ((RoleResource)o).getRole().equals(teamMember);
                            }
                            else
                            {
                                return true;
                            }
                        }
                    }
                );
        
        TableTool helper = new TableTool(state, model, filters);
        return helper; 
    }

    protected abstract String getTableName();

    public static class PathTool
    {
        List base = new ArrayList();

        public PathTool(SiteResource site)
        {
            base.add(site.getPath()+"/structure");
            base.add(site.getPath()+"/applications");
        }

        public String process(String in)
        {
            Iterator i = base.iterator();
            while(i.hasNext())
            {
                String basePath = (String)i.next();
                if(in.startsWith(basePath))
                {
                    if(in.length() == basePath.length())
                    {
                        return "/";
                    }
                    else
                    {
                        return in.substring(basePath.length());
                    }
                }
            }
            return in;
        }
    }
}
