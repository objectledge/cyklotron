package net.cyklotron.cms.modules.actions.related;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.util.CoralEntitySelectionState;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.ProtectedResource;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Update resource relationships.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UpdateRelations.java,v 1.9 2007-12-20 16:57:05 rafal Exp $
 */
public class RemoveRelation
    extends BaseRelatedAction
{
    
    public RemoveRelation(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, RelatedService relatedService)
    {
        super(logger, structureService, cmsDataFactory, relatedService);        
    }
    
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        
        long resId = parameters.getLong("res_id", -1L);
        long resClassResId = parameters.getLong("res_class_id", -1L);
        long itemId = parameters.getLong("item_id", -1);
        
        try
        {
            List<Resource> resources;
            Resource resource = coralSession.getStore().getResource(resId);
            Resource resourceItem = coralSession.getStore().getResource(itemId);
            
            resources = new ArrayList<Resource>(Arrays.asList(relatedService.getRelatedTo(coralSession, resource, null, null)));
            resources.remove(resourceItem);
            relatedService.setRelatedTo(coralSession, resource ,resources.toArray(new Resource[resources.size()]));
            
            ResourceSelectionState relatedState = ResourceSelectionState.getState(context,
                RELATED_SELECTION_STATE + ":" + resource.getIdString());
            relatedState.update(parameters);
            relatedState.remove(resourceItem);

        }
        catch(EntityDoesNotExistException e)
        {
            logger.error("ARLException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        templatingContext.put("result","removed_successfully");
    }

}

