package net.cyklotron.cms.modules.views.related;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.integration.ResourceClassResourceImpl;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedConstants;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.util.CmsPathFilter;
import net.cyklotron.cms.util.CmsResourceClassFilter;
import net.cyklotron.cms.util.ProtectedViewFilter;

public class ChooseRelatedResources
    extends BaseRelatedScreen
{
    /** integration service */
    private IntegrationService integrationService;

    
    public ChooseRelatedResources(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, RelatedService relatedService,
        IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        relatedService);
        this.integrationService = integrationService;
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
                new CmsResourceClassFilter(coralSession, integrationService, new String[] { resourceClassResource.getName() }));
            String[] classes = resourceClassResource.getAggregationParentClassesList();
            String[] paths = resourceClassResource.getAggregationTargetPathsList();
            
            // TODO: check if we should name the state using resource id
            ResourceSelectionState relatedState =
                ResourceSelectionState.getState(context, RelatedConstants.RELATED_SELECTION_STATE);
            
            String[] expandedResourcesIds = null;
            if(relatedState.isNew())
            {
                // get related resources
                Map initialState = new HashMap();
                Resource[] related = relatedService.getRelatedTo(coralSession, resource);
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
                relatedState.update(parameters);
            }
            templatingContext.put("related_selection_state", relatedState);
            
            TableState state = tableStateManager.getState(context, "cms:screens:related,ChooseRelatedResources");
            if(state.isNew())
            {
                state.setTreeView(true);
                state.setShowRoot(true);
                state.setSortColumnName("name");
                state.setExpanded(expandedResourcesIds);
            }
            String rooId = Long.toString(site.getId());
            state.setRootId(rooId);
            state.setExpanded(rooId);
            
            TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
            ArrayList filters = new ArrayList();
            filters.add(new ProtectedViewFilter(coralSession, coralSession.getUserSubject()));
            filters.add(new CmsPathFilter(site, paths));
            TableTool helper = new TableTool(state, filters, model);
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
