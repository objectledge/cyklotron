package net.cyklotron.cms.modules.views.site;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.labeo.services.authentication.AuthenticationService;
import net.labeo.services.authentication.UnknownUserException;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.table.ListTableModel;
import net.labeo.services.table.MapComparator;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableFilter;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;

/**
 *
 */
public class SiteList
    extends BaseSiteScreen
{
    protected TableService tableService;

    protected AuthenticationService authenitcationService;

    protected TableColumn[] columns;

    public SiteList()
        throws ProcessingException
    {
        tableService = (TableService)broker.
            getService(TableService.SERVICE_NAME);
        authenticationService = (AuthenticationService)broker.
            getService(AuthenticationService.SERVICE_NAME);
        try
        {
            columns = new TableColumn[6];
            columns[0] = new TableColumn("id", null);
            columns[1] = new TableColumn("name", new MapComparator("name"));
            columns[2] = new TableColumn("creator", new MapComparator("creator"));
            columns[3] = new TableColumn("owner", new MapComparator("owner"));
            columns[4] = new TableColumn("member", null);
            columns[5] = new TableColumn("administrator", null);
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize table columns", e);
        }
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        SiteResource[] sites = siteService.getSites();
        Subject current = coralSession.getUserSubject();
        ArrayList siteList = new ArrayList();
        for(int i=0; i<sites.length; i++)
        {
            HashMap siteDesc = new HashMap();
            siteDesc.put("id", sites[i].getIdObject());
            siteDesc.put("name", sites[i].getName());
            
            // do not call authentication so often if not needed
            /*
            try
            {
				siteDesc.put("creator", authenticationService.
							 getLogin(sites[i].getCreatedBy().getName()));
            }
            catch(UnknownUserException e)
            {
				siteDesc.put("creator", "unknown");
            }
            try
            {
				siteDesc.put("owner", authenticationService.
							 getLogin(sites[i].getOwner().getName()));
            }
			catch(UnknownUserException e)
			{
				siteDesc.put("owner", "unknown");
			}
			*/
            if(current.hasRole(sites[i].getTeamMember()))
            {
                siteDesc.put("member", Boolean.TRUE);
            }
            if(current.hasRole(sites[i].getAdministrator()))
            {
                siteDesc.put("administrator", Boolean.TRUE);
            }
            siteList.add(siteDesc);
        }
        TableState state = tableService.getLocalState(data, "cms:screens:site,SiteList");
        if(state.isNew())
        {
            state.setViewType(TableConstants.VIEW_AS_LIST);
            state.setPageSize(10);
            state.setSortColumnName("name");
        }
        Role admin = coralSession.getSecurity().getUniqueRole("cms.administrator");
        TableModel model = new ListTableModel(siteList, columns);
        try
        {
            ArrayList filters = new ArrayList();
            if(!coralSession.getUserSubject().hasRole(admin))
            {
                filters.add(new TableFilter()
                    {
                        public boolean accept(Object o)
                        {
                            return ((Map)o).containsKey("member");
                        }
                    }
                );
            }
            TableTool helper = new TableTool(state, model, filters);
            templatingContext.put("table", helper);
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize table", e);
        }
    }

    public boolean checkAccess(RunData data)
    {
        return true;
    }
}
