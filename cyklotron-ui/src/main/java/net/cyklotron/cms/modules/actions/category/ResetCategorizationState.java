package net.cyklotron.cms.modules.actions.category;

import net.labeo.services.resource.util.ResourceSelectionState;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.CategoryConstants;

/**
 *
 * @author <a href="mailto:zwierzem@caltha.pl">Damian Gajda</a>
 * @version $Id: ResetCategorizationState.java,v 1.1 2005-01-24 04:33:58 pablo Exp $
 */
public class ResetCategorizationState extends BaseCategorizationAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();

        // clean category seletion state
        ResourceSelectionState categorizationState =
            ResourceSelectionState.getState(data, CategoryConstants.CATEGORY_SELECTION_STATE);
        if(categorizationState.isNew())
        {
            categorizationState.setPrefix("category");
        }

        ResourceSelectionState.removeState(data, categorizationState);

        templatingContext.put("result","reseted_successfully");
    }
}
