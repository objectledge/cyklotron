package net.cyklotron.cms.modules.views.popup;

import java.util.ArrayList;
import java.util.List;

import net.labeo.services.personaldata.PersonalDataService;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.table.ListComparator;
import net.labeo.services.table.ListTableModel;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Parameter;
import net.labeo.util.configuration.Parameters;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.util.UserUtils;

public class UserList
    extends BaseCMSScreen
{
    protected PersonalDataService personalDataService;

    protected TableService tableService;

    protected TableColumn[] columns;

    protected List letters;

    public UserList()
        throws ProcessingException
    {
        personalDataService = (PersonalDataService)broker.
            getService(PersonalDataService.SERVICE_NAME);
        tableService = (TableService)broker.
            getService(TableService.SERVICE_NAME);
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
            throw new ProcessingException("failed to initialize column data", e);
        }
        // this is way silly
        letters = new ArrayList();
        for(int i=(int)'A'; i<(int)'Z'; i++)
        {
            letters.add(new String(new char[] {(char)i}));
        }
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
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

            List filtered = UserUtils.filteredUserList(data.getBroker(), show, search);
            
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
                Parameters pc = personalDataService.
                    getData(user.getName());
                Parameter[] classes = pc.getArray("objectClass");
                for(int j=0; j<classes.length; j++)
                {
                    if(classes[j].asString().equals("cyklotronPerson"))
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
            TableState state = tableService.getLocalState(data, "cms:screens:popup,UserList");
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_LIST);
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
            templatingContext.put("table", new TableTool(state, model, null));

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
