package net.cyklotron.cms.modules.actions.category;

import java.util.Map;
import java.util.Set;

import net.labeo.services.resource.util.ResourceSelectionState;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.CategoryConstants;

/**
 *
 * @author <a href="mailto:zwierzem@caltha.pl">Damian Gajda</a>
 * @version $Id: OptimiseCategorization.java,v 1.1 2005-01-24 04:33:58 pablo Exp $
 */
public class OptimiseCategorization extends BaseCategorizationAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();

        // get and modify category ids state
        ResourceSelectionState categorizationState =
            ResourceSelectionState.getState(data, CategoryConstants.CATEGORY_SELECTION_STATE);
        if(categorizationState.isNew())
        {
            categorizationState.setPrefix("category");
        }

        categorizationState.update(data);

        // collect categories
        Map temp = categorizationState.getResources(coralSession, "selected");
        Set categories = temp.keySet();

        // perform optimisation
        Set removedCategories = categoryService.optimiseCategorizationSet(categories);
        templatingContext.put("removed_categories", removedCategories);

        // recreate categorization state -  temp is backed by categories in optimisation
        categorizationState.init(temp);

        templatingContext.put("result","optimised_successfully");
    }
}
