package net.cyklotron.cms.modules.views.category;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.util.ResourceSelectionState;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.CategoryConstants;
import net.cyklotron.cms.category.CategoryInfoTool;
import net.cyklotron.cms.category.CategoryResource;

/**
 * Screen showing available categories, presented as tree, allowing to choose them for a resource.
 * This screen is not protected because everyone should be able to see which categories are
 * assigned to a resource.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Categorize.java,v 1.2 2005-01-25 11:23:54 pablo Exp $
 */
public class Categorize extends CategoryList
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // prepare categorized resource
        Resource resource = getResource(data);
        templatingContext.put("resource", resource);

        // prepare category tool
        CategoryInfoTool categoryTool = new CategoryInfoTool(data);
        templatingContext.put("category_tool", categoryTool);

        // get category selection state
        ResourceSelectionState categorizationState =
            ResourceSelectionState.getState(data, CategoryConstants.CATEGORY_SELECTION_STATE);
        if(parameters.get("reset-state").asBoolean(false))
        {
            ResourceSelectionState.removeState(data, categorizationState);
            categorizationState =
                ResourceSelectionState.getState(data, CategoryConstants.CATEGORY_SELECTION_STATE);
        }

        Set expandedCategoriesIds = new HashSet();
        if(categorizationState.isNew())
        {
            categorizationState.setPrefix("category");
    
            CategoryResource[] categories = categoryService.getCategories(resource, false);
            Map initialState = new HashMap();
            for(int i=0; i<categories.length; i++)
            {
                initialState.put(categories[i], "selected");
            }
            categorizationState.init(initialState);

            // prepare expanded categories - includes inherited ones
            categories = categoryService.getCategories(resource, true);
            for(int i=0; i<categories.length; i++)
            {
                expandedCategoriesIds.add(categories[i].getIdObject());
            }
        }
        // modify category ids state
        categorizationState.update(data);
        //
        templatingContext.put("category_selection_state", categorizationState);

        // prepare category tree or trees
        prepareTableTools(data, expandedCategoriesIds);
    }

    protected String getTableStateBaseName()
    {
        return "cms:screens:category,Categorize";
    }

    protected Resource getResource(RunData data)
        throws ProcessingException
    {
        long res_id = parameters.getLong("res_id", -1);
        Resource resource;
        if(res_id == -1)
        {
            throw new ProcessingException("Parameter res_id is not defined");
        }
        else
        {
            try
            {
                resource = coralSession.getStore().getResource(res_id);
                if(resource instanceof CategoryResource)
                {
                    throw new ProcessingException("Cannot categorize categories");
                }
            }
            catch(EntityDoesNotExistException e)
            {
                throw new ProcessingException("resource does not exist",e);
            }
        }
        return resource;
    }
}
