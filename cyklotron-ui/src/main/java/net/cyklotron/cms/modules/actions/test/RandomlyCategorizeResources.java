package net.cyklotron.cms.modules.actions.test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.cyklotron.cms.category.CategoryConstants;
import net.cyklotron.cms.category.CategoryMapResource;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.modules.actions.category.BaseCategorizationAction;
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
 * @version $Id: RandomlyCategorizeResources.java,v 1.1 2005-01-24 04:34:32 pablo Exp $
 */
public class RandomlyCategorizeResources extends BaseCategorizationAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

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

        // get activated categories
        Map temp = categorizationState.getResources(coralSession, "selected");
        CategoryResource[] categories =
            (CategoryResource[]) temp.keySet().toArray(new CategoryResource[temp.keySet().size()]); 

        // -----------------------------------
        // get and modify resources ids state
        ResourceSelectionState resSelState =
            ResourceSelectionState.getState(data,
            net.cyklotron.cms.modules.screens.test.RandomlyCategorizeResources.RES_SELECTION_STATE); 
        resSelState .update(data);
        // remove it from session
        ResourceSelectionState.removeState(data, resSelState);

        // get resources for categorization
        temp = resSelState.getResources(coralSession, "selected");
        Set resources = temp.keySet();

        // ----------------------------------------
        // get parameters
        boolean replaceOld = parameters.get("addcategories")
            .asString("replace").equals("replace");
        int minCategories =  parameters.getInt("mincats", 0);
        int maxCategories =  parameters.getInt("maxcats", 7);
        int randRange = maxCategories - minCategories;  

        // perform categorization
        try
        {
            CategoryMapResource categoryMap = categoryService.getCategoryMap();
            CrossReference refs = categoryMap.getReferences();

            Set selectedCategories = new HashSet();
            Random rand = new Random(); 
            for(Iterator i=resources.iterator(); i.hasNext();)
            {
                Resource resource = (Resource)(i.next());
                if(replaceOld)
                {
                    refs.removeInv(resource);
                }
                selectedCategories.clear();
                int numCategories = rand.nextInt(randRange) + minCategories;
                while(selectedCategories.size() < numCategories)
                {
                    selectedCategories.add(categories[rand.nextInt(categories.length)]);
                }
                for (Iterator iter = selectedCategories.iterator(); iter.hasNext();)
                {
                    CategoryResource category = (CategoryResource) iter.next();
                    refs.put(category, resource);
                }
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
    
    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkAdministrator(context, coralSession);
    }
}
