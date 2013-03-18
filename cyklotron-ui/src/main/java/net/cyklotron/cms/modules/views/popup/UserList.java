package net.cyklotron.cms.modules.views.popup;

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.authentication.DefaultPrincipal;
import org.objectledge.authentication.UserManager;
import org.objectledge.authentication.UserUnknownException;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.directory.DirectoryParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.comparator.ListComparator;
import org.objectledge.table.comparator.NumericStringComparator;
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

    @SuppressWarnings("unchecked")
    protected TableColumn<List<String>>[] columns = new TableColumn[5];

    protected List<String> letters = new ArrayList<>();

    public UserList(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager, UserManager userManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.userManager = userManager;
        try
        {
            columns[0] = new TableColumn<List<String>>("subjectId", new ListComparator<String>(1,
                new NumericStringComparator()));
            columns[1] = new TableColumn<List<String>>("uid", new ListComparator<String>(2));
            columns[2] = new TableColumn<List<String>>("dn", new ListComparator<String>(3));
            columns[3] = new TableColumn<List<String>>("cn", new ListComparator<String>(4));
            columns[4] = new TableColumn<List<String>>("mail", new ListComparator<String>(5));
        }
        catch(TableException e)
        {
            throw new ComponentInitializationError("failed to initialize column data", e);
        }
        // this is way silly
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

            List<Subject> filtered = UserUtils.filteredUserList(coralSession, userManager, show,
                search);
            
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

            List<List<String>> processed = new ArrayList<>();
            int pos = 0;
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
                try(DirectoryParameters pc = new DirectoryParameters(
                    userManager.getPersonalData(new DefaultPrincipal(user.getName()))))
                {
                    String[] classes = pc.getStrings("objectClass");
                    for(int j=0; j<classes.length; j++)
                    {
                        if(classes[j].equals("inetOrgPerson"))
                        {
                            List<String> userData = new ArrayList<>();
                            userData.add(Integer.toString(pos++));
                            userData.add(user.getIdString());
                            userData.add(pc.get("uid"));
                            userData.add(user.getName());
                            userData.add(pc.get("cn", ""));
                            userData.add(firstOrBlank(pc.getStrings("mail")));
                            processed.add(userData);
                        }
                    }
                }
                catch(UserUnknownException e)
                {
                    logger.error(user.getName()
                        + " is present in Coral but missing from user directory");
                    continue;
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
                state.setSortColumnName("uid");
            }
            TableModel<List<String>> model = new ListTableModel<List<String>>(processed, columns)
                {
                    @Override
                    public String getId(String parent, List<String> child)
                    {
                        return child.get(0);
                    }
                };
            templatingContext.put("table", new TableTool<List<String>>(state, null, model));

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

    private static String firstOrBlank(String[] vals)
    {
        if(vals != null && vals.length >= 1)
        {
            return vals[0];
        }
        else
        {
            return "";
        }
    }
}
