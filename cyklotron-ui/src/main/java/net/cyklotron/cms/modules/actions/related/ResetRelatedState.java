package net.cyklotron.cms.modules.actions.related;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.util.CoralEntitySelectionState;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.related.RelatedConstants;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Reset resource relationships in memory state.
 * 
 * @author <a href="mailto:zwierzem@caltha.pl">Damian Gajda</a>
 * @version $Id: ResetRelatedState.java,v 1.4 2005-03-08 10:53:25 pablo Exp $
 */
public class ResetRelatedState
extends BaseRelatedAction
{
    
    public ResetRelatedState(Logger logger, StructureService structureService,
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
        // clean seletion state
        ResourceSelectionState relatedState =
            ResourceSelectionState.getState(context, RelatedConstants.RELATED_SELECTION_STATE);
        CoralEntitySelectionState.removeState(context, relatedState);

        templatingContext.put("result","reseted_successfully");
    }
}

