package net.cyklotron.cms.modules.views.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.table.ExtendedTableModel;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;

/**
 * The default void screen assember for security sub-application.
 */
public abstract class BaseRoleScreen
    extends BaseSecurityScreen
{
    
    
    public BaseRoleScreen(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SecurityService securityService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        securityService);
        // TODO Auto-generated constructor stub
    }
    
    public TableTool getRoleTable(CoralSession coralSession, SiteResource site, 
        I18nContext i18nContext)
        throws TableException
    {
        Resource rolesRoot = cmsSecurityService.getRoleInformationRoot(coralSession, site);
        TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
        TableState state = tableStateManager.getState(context, getTableName()+":"+site.getName());
        if(state.isNew())
        {
            state.setTreeView(true);
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
        
        TableTool helper = new TableTool(state, filters, model);
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
