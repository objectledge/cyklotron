package net.cyklotron.cms.modules.actions.related;

import net.cyklotron.cms.related.RelatedConstants;

import net.labeo.services.resource.util.ResourceSelectionState;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Reset resource relationships in memory state.
 * 
 * @author <a href="mailto:zwierzem@caltha.pl">Damian Gajda</a>
 * @version $Id: ResetRelatedState.java,v 1.1 2005-01-24 04:34:41 pablo Exp $
 */
public class ResetRelatedState
extends BaseRelatedAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();

        // clean seletion state
        ResourceSelectionState relatedState =
            ResourceSelectionState.getState(data, RelatedConstants.RELATED_SELECTION_STATE);
        ResourceSelectionState.removeState(data, relatedState);

        templatingContext.put("result","reseted_successfully");
    }
}

