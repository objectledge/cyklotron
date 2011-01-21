package net.cyklotron.cms.modules.views.site;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.comparator.MapComparator;
import org.objectledge.table.generic.ListTableModel;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 *
 */
public class SiteList
    extends BaseSiteScreen
{
    protected UserManager userManager;

    protected TableColumn[] columns;

    
    public SiteList(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SiteService siteService,
        UserManager userManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, siteService);
        this.userManager = userManager;
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
            throw new ComponentInitializationError("failed to initialize table columns", e);
        }
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        SiteResource[] sites = siteService.getSites(coralSession);
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
        TableState state = tableStateManager.getState(context, "cms:screens:site,SiteList");
        if(state.isNew())
        {
            state.setTreeView(false);
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
            TableTool helper = new TableTool(state, filters, model);
            templatingContext.put("table", helper);
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize table", e);
        }
    }

    public boolean checkAccessRights(Context context)
    {
        return true;
    }
}
