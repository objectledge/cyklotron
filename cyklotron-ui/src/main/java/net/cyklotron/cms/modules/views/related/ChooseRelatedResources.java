package net.cyklotron.cms.modules.views.related;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.modules.views.BaseChooseResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedConstants;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.util.CmsResourceClassFilter;
import net.cyklotron.cms.util.IndexTitleComparator;

public class ChooseRelatedResources
    extends BaseChooseResource
{
    protected RelatedService relatedService;
    
    public static String SELECTION_STATE = RelatedConstants.RELATED_SELECTION_STATE;
    
    public static String STATE_NAME = "cms:screens:related,ChooseRelatedResources";

    public ChooseRelatedResources(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, RelatedService relatedService,
        IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        integrationService);
        this.relatedService = relatedService;
    }

    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        super.process(parameters, mvcContext, templatingContext, httpContext, i18nContext,
            coralSession);
        
        long resId = parameters.getLong("res_id", -1L);
        
        try
        {
            Resource resource = coralSession.getStore().getResource(resId);
            SiteResource site = getCmsData().getSite();
            
            if(!resourceClassResource.getRelatedSupported())
            {
                throw new ProcessingException(
                    "Selected resource class does not support relationships: "
                        + resourceClassResource.getName());
            }
            if(!integrationService.isApplicationEnabled(coralSession, site,
                (ApplicationResource)resourceClassResource.getParent().getParent()))
            {
                throw new ProcessingException(
                    "Selected resource class belongs to disabled application: "
                        + resourceClassResource.getName());
            }
            templatingContext.put("res_class_res", resourceClassResource);
            templatingContext.put("res_class_filter", new CmsResourceClassFilter(coralSession,
                integrationService, new String[] { resourceClassResource.getName() }));
    
            String stateId = RelatedConstants.RELATED_SELECTION_STATE + ":"
            + resource.getIdString();
            ResourceSelectionState relatedState = ResourceSelectionState.getState(context, stateId);
            boolean resetState = parameters.getBoolean("reset", false);
            if(resetState)
            {
                ResourceSelectionState.removeState(context, relatedState);
                relatedState = ResourceSelectionState.getState(context, stateId);
            }
            if(relatedState.isNew())
            {
                // get related resources
                Map initialState = new HashMap();
                Resource[] related = relatedService.getRelatedTo(coralSession, resource, null, null);
                for (int i = 0; i < related.length; i++)
                {
                    initialState.put(related[i], "selected");
                }
                // initialise state
                relatedState.init(initialState);
            }
            else
            {
                // modify state for changes
                relatedState.update(parameters);
            }
            
            ResourceList<Resource> sequence = null;
            if(resource instanceof DocumentNodeResource)
            {
                sequence = ((DocumentNodeResource)resource).getRelatedResourcesSequence();
            }
            Resource[] relatedTo = relatedService.getRelatedTo(coralSession, resource, sequence,
                new IndexTitleComparator(context, integrationService, i18nContext.getLocale()));
            templatingContext.put("related_to", Arrays.asList(relatedTo));
            templatingContext.put("related_selection_state", relatedState);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("Could not find selected resource or resource class", e);
        }
        
    }
    
    protected boolean isResourceClassSupported(ResourceClassResource rClass)
    {
        return rClass.getRelatedSupported(false);
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("related"))
        {
            logger.debug("Application 'related' not enabled in site");
            return false;
        }
        return true;
    }

}
