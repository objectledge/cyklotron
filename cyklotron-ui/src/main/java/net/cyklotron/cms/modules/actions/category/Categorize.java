package net.cyklotron.cms.modules.actions.category;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.cyklotron.cms.category.CategoryConstants;
import net.cyklotron.cms.category.CategoryMapResource;
import net.cyklotron.cms.category.CategoryResource;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.generic.CrossReference;
import net.labeo.services.resource.util.ResourceSelectionState;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: Categorize.java,v 1.1 2005-01-24 04:33:58 pablo Exp $
 */
public class Categorize extends BaseCategorizationAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        // prepare categorized resource
        Resource resource = getResource(data);

        // get and modify category ids state
        ResourceSelectionState categorizationState =
            ResourceSelectionState.getState(data, CategoryConstants.CATEGORY_SELECTION_STATE);
        if(categorizationState.isNew())
        {
            categorizationState.setPrefix("category");
        }

        categorizationState.update(data);
        // remove it from session
        ResourceSelectionState.removeState(data, categorizationState);

        // get resource categories
        Map temp = categorizationState.getResources(coralSession, "selected");
        Set categories = temp.keySet();

        // perform categorization
        try
        {
            CategoryMapResource categoryMap = categoryService.getCategoryMap();
            CrossReference refs = categoryMap.getReferences();

            refs.removeInv(resource);
            for(Iterator i=categories.iterator(); i.hasNext();)
            {
                refs.put((CategoryResource)(i.next()), resource);
            }

            categoryMap.setReferences(refs);
            categoryMap.update(subject);
        }
        catch (ValueRequiredException e)
        {
            templatingContext.put("result","exception");
            log.error("Problem updating category references: ",e);
            return;
        }

        templatingContext.put("result","updated_successfully");
    }
}
