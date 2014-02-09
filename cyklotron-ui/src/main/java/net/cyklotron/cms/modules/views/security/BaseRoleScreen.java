package net.cyklotron.cms.modules.views.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
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
        
    }
    
    public TableTool<Resource> getRoleTable(CoralSession coralSession, SiteResource site, 
        I18nContext i18nContext)
        throws TableException
    {
        Resource rolesRoot = cmsSecurityService.getRoleInformationRoot(coralSession, site);
        TableModel<Resource> model = new CoralTableModel(coralSession, i18nContext.getLocale());
        String suffix = "";
        if(site != null)
        {
            suffix = site.getName();
        }
        TableState state = tableStateManager.getState(context, getTableName()+":"+suffix);
        if(state.isNew())
        {
            state.setTreeView(true);
            state.setCurrentPage(0);
            state.setShowRoot(false);
            Object[] firstLevel = ((ExtendedTableModel<Resource>)model).getChildren(rolesRoot);
            for(int i=0; i<firstLevel.length; i++)
            {
                state.setExpanded(((Resource)firstLevel[i]).getIdString());
            }
        }
        String rootId = rolesRoot.getIdString();
        state.setSortColumnName("name");
        state.setRootId(rootId);
        state.setExpanded(rootId);
        List<TableFilter<? super Resource>> filters = new ArrayList<>();
        if(site != null)
        {
            filters.add(new TableFilter<Resource>()
                        {
                            public boolean accept(Resource o)
                            {
                                if(o instanceof RoleResource)
                                {
                                    return !cmsSecurityService.isGroupResource((RoleResource)o);
                                }
                                else
                                {
                                    return true;
                                }
                            }
                        }
                    );
        }
        TableTool<Resource> helper = new TableTool<Resource>(state, filters, model);
        return helper; 
    }

    protected abstract String getTableName();

    public static class PathTool
    {
        List<String> base = new ArrayList<String>();

        public PathTool(SiteResource site)
        {
            String prefix = "/cms";
            if(site != null)
            {
                prefix = site.getPath();
            }
            base.add(prefix+"/structure");
            base.add(prefix+"/applications");
            base.add(prefix+"/files");
            base.add(prefix+"/categories");
        }

        public String process(String in)
        {
            Iterator<String> i = base.iterator();
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
