package net.cyklotron.cms.modules.views.related;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.ARLTableModel;
import net.labeo.services.resource.util.ResourceSelectionState;
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
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.integration.ResourceClassResourceImpl;
import net.cyklotron.cms.related.RelatedConstants;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.util.CmsPathFilter;
import net.cyklotron.cms.util.CmsResourceClassFilter;
import net.cyklotron.cms.util.ProtectedViewFilter;

public class ChooseRelatedResources
    extends BaseRelatedScreen
{
    /** table service */
    private TableService tableService;
    
    /** integration service */
    private IntegrationService integrationService;

    public ChooseRelatedResources()
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
        integrationService = (IntegrationService)broker.getService(IntegrationService.SERVICE_NAME);
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        
        long resId = parameters.getLong("res_id", -1L);
        long resClassResId = parameters.getLong("res_class_id", -1L);
        try
        {
            SiteResource site = cmsData.getSite();
            Resource resource = coralSession.getStore().getResource(resId);
            templatingContext.put("resource", resource);

            ResourceClassResource resourceClassResource =
                ResourceClassResourceImpl.getResourceClassResource(coralSession, resClassResId);
            if(!resourceClassResource.getRelatedSupported())
            {
                throw new ProcessingException(
                    "Selected resource class does not support relationships: "
                        +resourceClassResource.getName());
            }
            templatingContext.put("res_class_res", resourceClassResource);
            templatingContext.put("res_class_filter",
                new CmsResourceClassFilter(new String[] { resourceClassResource.getName() }));
            String[] classes = resourceClassResource.getAggregationParentClassesList();
            String[] paths = resourceClassResource.getAggregationTargetPathsList();
            
            // TODO: check if we should name the state using resource id
            ResourceSelectionState relatedState =
                ResourceSelectionState.getState(data, RelatedConstants.RELATED_SELECTION_STATE);
            
            String[] expandedResourcesIds = null;
            if(relatedState.isNew())
            {
                // get related resources
                Map initialState = new HashMap();
                Resource[] related = relatedService.getRelatedTo(resource);
                for(int i=0; i < related.length; i++)
                {
                    initialState.put(related[i], "selected");
                }
                // initialise state
                relatedState.init(initialState);
                // prepare expanded resources - includes ancestors
                expandedResourcesIds = relatedState.getExpandedIds(coralSession, site.getId());
            }
            else
            {
                // modify state for changes
                relatedState.update(data);
            }
            templatingContext.put("related_selection_state", relatedState);
            
            TableState state = tableService.getLocalState(data, "cms:screens:related,ChooseRelatedResources");
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_TREE);
                state.setShowRoot(true);
                state.setMultiSelect(false);
                state.setSortColumnName("name");
                state.setExpanded(expandedResourcesIds);
            }
            String rooId = Long.toString(site.getId());
            state.setRootId(rooId);
            state.setExpanded(rooId);
            
            TableModel model = new ARLTableModel(i18nContext.getLocale()());
            ArrayList filters = new ArrayList();
            filters.add(new ProtectedViewFilter(coralSession.getUserSubject()));
            filters.add(new CmsPathFilter(site, paths));
            TableTool helper = new TableTool(state, model, filters);
            templatingContext.put("table", helper);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("Could not find selected resource or resource class",e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("Could not prepare table state",e);
        }
    }
}
