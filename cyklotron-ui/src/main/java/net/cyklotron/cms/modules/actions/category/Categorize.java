package net.cyklotron.cms.modules.actions.category;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.relation.RelationModification;
import org.objectledge.coral.security.Subject;
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
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: Categorize.java,v 1.2 2005-01-24 10:27:04 pablo Exp $
 */
public class Categorize extends BaseCategorizationAction
{
    
    
    public Categorize(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CategoryService categoryService,
        IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, categoryService, integrationService);
        // TODO Auto-generated constructor stub
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();

        // prepare categorized resource
        Resource resource = getResource(coralSession, parameters);

        // get and modify category ids state
        ResourceSelectionState categorizationState =
            ResourceSelectionState.getState(context, CategoryConstants.CATEGORY_SELECTION_STATE);
        if(categorizationState.isNew())
        {
            categorizationState.setPrefix("category");
        }

        categorizationState.update(parameters);
        // remove it from session
        ResourceSelectionState.removeState(context, categorizationState);

        // get resource categories
        Map temp = categorizationState.getEntities(coralSession, "selected");
        Set categories = temp.keySet();

        // perform categorization
        Relation refs = categoryService.getResourcesRelation(coralSession);
        RelationModification diff = new RelationModification();
        diff.removeInv(resource);
        for(Iterator i=categories.iterator(); i.hasNext();)
        {
            diff.add((CategoryResource)(i.next()), resource);
        }
        coralSession.getRelationManager().updateRelation(refs, diff);
        templatingContext.put("result","updated_successfully");
    }
}
