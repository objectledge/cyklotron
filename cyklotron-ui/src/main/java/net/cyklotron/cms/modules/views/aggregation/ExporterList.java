package net.cyklotron.cms.modules.views.aggregation;

import java.util.ArrayList;

import net.labeo.services.authentication.AuthenticationService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.table.ResourceListTableModel;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.util.ProtectedValidityViewFilter;
import net.cyklotron.cms.util.SiteRejectFilter;

/**
 * This screen shows a list of sites that are visible by anonymous subject, it is used as a starting
 * point for importing data from this sites.
 *
 * @author <a href="mailo:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ExporterList.java,v 1.3 2005-01-25 11:23:53 pablo Exp $
 */
public class ExporterList extends BaseAggregationScreen
{
    protected TableService tableService;
    protected StructureService structureService;
    protected Subject anonymous;

    public ExporterList() throws ProcessingException
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
        structureService = (StructureService)broker.getService(StructureService.SERVICE_NAME);
        
        try
        {
            AuthenticationService authenticationService =
                (AuthenticationService)broker.getService(AuthenticationService.SERVICE_NAME);
            anonymous = coralSession.getSecurity().getSubject(
                authenticationService.getAnonymousUser().getName());
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("canno get anonymous subject", e);
        }
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            CmsData cmsData =  getCmsData();
            SiteResource[] sites = siteService.getSites();
            NavigationNodeResource[] homePages = new NavigationNodeResource[sites.length];
            
            for(int i=0; i< sites.length; i++)
            {
                homePages[i] = structureService.getRootNode(sites[i]);
            }
            
            TableState state = tableService.getGlobalState(data,
                "cms:screens:aggregation:ExporterList");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model = new ResourceListTableModel(homePages, i18nContext.getLocale()());
            ArrayList filters = new ArrayList();
            filters.add(new ProtectedValidityViewFilter(cmsData, anonymous));
            filters.add(new SiteRejectFilter(new SiteResource[] { cmsData.getSite() } ));
            TableTool helper = new TableTool(state, model, filters);
            templatingContext.put("table", helper);
        }
        catch(StructureException e)
        {
            throw new ProcessingException("failed to get home pages", e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize table toolkit", e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        Role role = coralSession.getSecurity().getUniqueRole("cms.aggregation.importer");
        return coralSession.getUserSubject().hasRole(getCmsData().getSite().getTeamMember()) &&
               coralSession.getUserSubject().hasRole(role);
    }
}
