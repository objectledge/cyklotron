package net.cyklotron.cms.modules.views.popup;

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.authentication.DefaultPrincipal;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.ListComparator;
import org.objectledge.table.generic.ListTableModel;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.util.UserUtils;

public class UserList
    extends BaseCMSScreen
{
    protected UserManager userManager;

    protected TableColumn[] columns;

    protected List letters;

    public UserList(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager, UserManager userManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.userManager = userManager;
        try
        {
            columns = new TableColumn[5];
            columns[0] = new TableColumn("uid", new ListComparator(1));
            columns[1] = new TableColumn("name", new ListComparator(3));
            columns[2] = new TableColumn("locality", new ListComparator(4));
            columns[3] = new TableColumn("country", new ListComparator(5));
            columns[4] = new TableColumn("filler", null);
        }
        catch(TableException e)
        {
            throw new ComponentInitializationError("failed to initialize column data", e);
        }
        // this is way silly
        letters = new ArrayList();
        for(int i=(int)'A'; i<(int)'Z'; i++)
        {
            letters.add(new String(new char[] {(char)i}));
        }
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            String show = parameters.get("show",null);
            String search = parameters.get("search",null);
            String prevShow = parameters.get("prev_show",null);
            String prevSearch = parameters.get("prev_search",null);
            templatingContext.put("show", show);
            templatingContext.put("search", search);

            List filtered = UserUtils.filteredUserList(coralSession, userManager, show, search);
            
            // TODO: furure enhansement idea for more than one permission and
            // role filter.
            // set one or more parameter i.e. permission_filter = permissionName + nodeId
            // in prepare method parse it and filter subjects - that's all.

            boolean permissionFilter = parameters.getBoolean("permission_filter", false);
            templatingContext.put("permission_filter", new Boolean(permissionFilter));
            Resource resource = null;
            Permission permission = null;
            if(permissionFilter)
            {
                long resId = parameters.getLong("res_id", -1);
                resource = coralSession.getStore().getResource(resId);
                String permissionName = parameters.get("perm","");
                permission = coralSession.getSecurity().getUniquePermission(permissionName);
            }
            
            boolean roleFilter = parameters.getBoolean("role_filter", false);
            templatingContext.put("role_filter", new Boolean(roleFilter));
            Role role = null;
            if(roleFilter)
            {
                String roleName = parameters.get("role","");
                role = coralSession.getSecurity().getUniqueRole(roleName);
            }
            


            ArrayList processed = new ArrayList();
            for(int i=0; i<filtered.size(); i++)
            {
                Subject user = (Subject)filtered.get(i);
                // filter user that has no rights for resource or specified role.
                if(permissionFilter && !user.hasPermission(resource, permission))
                {
                    continue;
                }
                if(roleFilter && !user.hasRole(role))
                {
                    continue;
                }
                Parameters pc = userManager.getPersonalData(new DefaultPrincipal(user.getName()));
                String[] classes = pc.getStrings("objectClass");
                for(int j=0; j<classes.length; j++)
                {
                    if(classes[j].equals("cyklotronPerson"))
                    {
                        ArrayList userData = new ArrayList();
                        userData.add(user.getIdObject());
                        userData.add(pc.get("uid"));
                        userData.add(user.getName());
                        userData.add(pc.get("cn",""));
                        userData.add(pc.get("l",""));
                        userData.add(pc.get("c",""));
                        processed.add(userData);
                    }
                }
            }
            templatingContext.put("users", processed);
            TableState state = tableStateManager.getState(context, "cms:screens:popup,UserList");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            if((show == null && prevShow != null) ||
               (show != null && prevShow == null) ||
               (show != null && !show.equals(prevShow)) ||
               (search == null && prevSearch != null) ||
               (search != null && prevSearch == null) ||
               (search != null && !search.equals(prevSearch)))
            {
                state.setCurrentPage(1);
            }
            TableModel model = new ListTableModel(processed, columns);
            templatingContext.put("table", new TableTool(state, null, model));

            templatingContext.put("letters", letters);
        }
        catch(Exception e)
        {
            if(e instanceof ProcessingException)
            {
                throw (ProcessingException)e;
            }
            else
            {
                throw new ProcessingException("failed to load user list", e);
            }
        }
    }
}
