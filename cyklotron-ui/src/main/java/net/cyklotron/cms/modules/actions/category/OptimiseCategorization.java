package net.cyklotron.cms.modules.actions.category;

import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryConstants;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailto:zwierzem@caltha.pl">Damian Gajda</a>
 * @version $Id: OptimiseCategorization.java,v 1.3 2005-03-08 10:51:31 pablo Exp $
 */
public class OptimiseCategorization extends BaseCategorizationAction
{
    
    public OptimiseCategorization(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CategoryService categoryService,
        IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, categoryService, integrationService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        // prepare categorized resource
        Resource resource = getResource(coralSession, parameters);
        
        // get and modify category ids state
        ResourceSelectionState categorizationState = ResourceSelectionState.getState(context,
            CategoryConstants.CATEGORY_SELECTION_STATE + ":" + resource.getIdString());
        if(categorizationState.isNew())
        {
            categorizationState.setPrefix("category");
        }

        categorizationState.update(parameters);

        // collect categories
        Map temp = categorizationState.getEntities(coralSession, "selected");
        Set categories = temp.keySet();

        // perform optimisation
        Set removedCategories = categoryService.optimiseCategorizationSet(categories);
        templatingContext.put("removed_categories", removedCategories);

        // recreate categorization state -  temp is backed by categories in optimisation
        categorizationState.init(temp);

        templatingContext.put("result","optimised_successfully");
    }
}
