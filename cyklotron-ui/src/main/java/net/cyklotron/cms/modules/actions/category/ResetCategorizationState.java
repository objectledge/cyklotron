package net.cyklotron.cms.modules.actions.category;

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
import net.cyklotron.cms.category.CategoryConstants;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailto:zwierzem@caltha.pl">Damian Gajda</a>
 * @version $Id: ResetCategorizationState.java,v 1.4 2005-03-08 10:51:31 pablo Exp $
 */
public class ResetCategorizationState extends BaseCategorizationAction
{
    
    
    public ResetCategorizationState(Logger logger, StructureService structureService,
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
        // clean category seletion state
        ResourceSelectionState categorizationState =
            ResourceSelectionState.getState(context, CategoryConstants.CATEGORY_SELECTION_STATE);
        if(categorizationState.isNew())
        {
            categorizationState.setPrefix("category");
        }

        CoralEntitySelectionState.removeState(context, categorizationState);

        templatingContext.put("result","reseted_successfully");
    }
}
