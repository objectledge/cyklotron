package net.cyklotron.cms.modules.views.aggregation;

import java.util.ArrayList;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.ARLTableModel;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteResourceImpl;
import net.cyklotron.cms.util.CmsPathFilter;
import net.cyklotron.cms.util.CmsResourceClassFilter;
import net.cyklotron.cms.util.ProtectedViewFilter;

/**
 * Common import target screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ImportTarget.java,v 1.1 2005-01-24 04:34:51 pablo Exp $
 */
public class ImportTarget
    extends BaseAggregationScreen
{
    /** table service */
    private TableService tableService = null;
    
    /** integration service */
    private IntegrationService integrationService = null;

    public ImportTarget()
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
        integrationService = (IntegrationService)broker.getService(IntegrationService.SERVICE_NAME);
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        long siteId = parameters.getLong("site_id", -1L);
        long resourceId = parameters.getLong("res_id", -1L);
        try
        {
            SiteResource site = SiteResourceImpl.getSiteResource(coralSession,siteId);
            Resource resource = coralSession.getStore().getResource(resourceId);
            templatingContext.put("resource", resource);
            
            ResourceClassResource resourceClassResource = integrationService.
                getResourceClass(resource.getResourceClass());
            templatingContext.put("copy_action", resourceClassResource.getAggregationCopyAction());
            
            String[] classes = resourceClassResource.getAggregationParentClassesList();
            String[] paths = resourceClassResource.getAggregationTargetPathsList();
            TableState state = tableService.getLocalState(data, "cms:screens:aggregation,ImportTarget");
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_TREE);
                state.setShowRoot(true);
                state.setMultiSelect(false);
                state.setSortColumnName("name");
            }
            String rooId = site.getIdString();
            state.setRootId(rooId);
            state.setExpanded(rooId);

            TableModel model = new ARLTableModel(i18nContext.getLocale()());
            ArrayList filters = new ArrayList();
            filters.add(new ProtectedViewFilter(coralSession.getUserSubject()));
            filters.add(new CmsPathFilter(site, paths));
            TableTool helper = new TableTool(state, model, filters);
            templatingContext.put("table", helper);
            templatingContext.put("resclass_filter",new CmsResourceClassFilter(classes));
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("FilesException ",e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("FilesException ",e);
        }
    }    

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return true;
    }
}
