package net.cyklotron.cms.modules.views.security;

import java.util.ArrayList;
import java.util.HashMap;

import net.labeo.services.authentication.AuthenticationService;
import net.labeo.services.personaldata.PersonalDataService;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.RoleAssignment;
import net.labeo.services.resource.Subject;
import net.labeo.services.table.ListTableModel;
import net.labeo.services.table.MapComparator;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Parameters;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;

/**
 *
 */
public class MemberList
    extends BaseSecurityScreen
{
    protected TableService tableService;

    protected AuthenticationService authenitcationService;

    protected PersonalDataService personalDataService;

    protected TableColumn[] columns;

    public MemberList()
        throws ProcessingException
    {
        tableService = (TableService)broker.
            getService(TableService.SERVICE_NAME);
        authenticationService = (AuthenticationService)broker.
            getService(AuthenticationService.SERVICE_NAME);
        personalDataService = (PersonalDataService)broker.
            getService(PersonalDataService.SERVICE_NAME);
        try
        {
            columns = new TableColumn[5];
            columns[0] = new TableColumn("id", null);
            columns[1] = new TableColumn("login", new MapComparator("login"));
            columns[2] = new TableColumn("name", new MapComparator("name"));
            columns[3] = new TableColumn("administrator", null);
            columns[4] = new TableColumn("member_since", new MapComparator("member_since"));
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize table columns", e);
        }
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            Role teamMember = site.getTeamMember();
            Subject[] members = teamMember.getSubjects();
            ArrayList memberList = new ArrayList();
            for(int i=0; i<members.length; i++)
            {
                HashMap memberDesc = new HashMap();
                memberDesc.put("id",members[i].getIdObject());
                memberDesc.put("login", authenticationService.getLogin(members[i].getName()));
                Parameters pc = personalDataService.getData(members[i].getName());
                memberDesc.put("name", pc.get("cn"));
                if(members[i].hasRole(site.getAdministrator()))
                {
                    memberDesc.put("administrator", Boolean.TRUE);
                }
                RoleAssignment[] assignments = members[i].getRoleAssignments();
                for(int j=0; j<assignments.length; j++)
                {
                    if(assignments[j].getRole().equals(site.getTeamMember()))
                    {
                        memberDesc.put("member_since", assignments[j].getGrantTime());
                    }
                }
                memberList.add(memberDesc);
            }
            TableState state = tableService.getLocalState(data, getTableName());
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(memberList, columns);
            templatingContext.put("table", new TableTool(state, model, null));
        }
        catch(Exception e)
        {
            if(e instanceof ProcessingException)
            {
                throw (ProcessingException)e;
            }
            throw new ProcessingException("failed to retreive data", e);
        }
    }

    public String getTableName()
    {
        return "screens:cms:security,MemberList";
    }
}
