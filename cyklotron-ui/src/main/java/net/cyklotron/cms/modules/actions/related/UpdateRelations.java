package net.cyklotron.cms.modules.actions.related;

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

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.ProtectedResource;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Update resource relationships.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UpdateRelations.java,v 1.4 2005-02-09 22:22:37 rafal Exp $
 */
public class UpdateRelations
    extends BaseRelatedAction
{
    
    public UpdateRelations(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, RelatedService relatedService)
    {
        super(logger, structureService, cmsDataFactory, relatedService);
        // TODO Auto-generated constructor stub
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        long resId = parameters.getLong("res_id", -1L);
        long resClassResId = parameters.getLong("res_class_id", -1L);
        try
        {
            Resource resource = coralSession.getStore().getResource(resId);
            ResourceSelectionState relatedState =
                ResourceSelectionState.getState(context, RELATED_SELECTION_STATE);
            relatedState.update(parameters);
            Map selected = relatedState.getEntities(coralSession,"selected");
            Resource[] resources = new Resource[selected.size()];
            selected.keySet().toArray(resources);
            relatedService.setRelatedTo(coralSession, resource, resources);
            CoralEntitySelectionState.removeState(context, relatedState);
        }
        catch(EntityDoesNotExistException e)
        {
            logger.error("ARLException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccessRights(Context context)
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        long resId = parameters.getLong("res_id", -1L);
        try
        {
            Resource resource = coralSession.getStore().getResource(resId);
            if(resource instanceof ProtectedResource)
            {
                return ((ProtectedResource)resource).canModify(context, coralSession.getUserSubject());
            }
        }
        catch(EntityDoesNotExistException e)
        {
        }
        return true;
    }

}

