package net.cyklotron.cms.modules.views.test;

import java.util.HashSet;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.CategoryConstants;
import net.cyklotron.cms.category.CategoryInfoTool;
import net.cyklotron.cms.modules.views.category.CategoryList;
import net.labeo.services.resource.util.ResourceSelectionState;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Screen showing available categories, presented as tree, allowing to choose them for a 
 * random resource categorization.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: RandomlyCategorizeResources.java,v 1.1 2005-01-24 04:35:26 pablo Exp $
 */
public class RandomlyCategorizeResources extends CategoryList
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
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

        if(categorizationState.isNew())
        {
            categorizationState.setPrefix("category");
        }
        // modify category ids state
        categorizationState.update(data);
        //
        templatingContext.put("category_selection_state", categorizationState);

        // prepare category tree or trees
        prepareTableTools(data, new HashSet());

        // get resource selection state
        ResourceSelectionState resSelState =
            ResourceSelectionState.getState(data, RES_SELECTION_STATE);
        if(parameters.get("reset-state").asBoolean(false))
        {
            ResourceSelectionState.removeState(data, resSelState);
            resSelState = ResourceSelectionState.getState(data, RES_SELECTION_STATE);
        }

        // modify res ids state
        resSelState.update(data);
        //
        templatingContext.put("resource_selection_state", resSelState);
        
        prepareTableTool(data, cmsDataFactory.getCmsData(context).getSite().getIdString(), "res_table");
    }

    protected String getTableStateBaseName()
    {
        return "cms:screens:test,RandomlyCategorizeResources";
    }
    
    public static final String RES_SELECTION_STATE = "test res sel state ugaxdasd";
}
