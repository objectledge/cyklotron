package net.cyklotron.cms.modules.actions.category;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.relation.RelationModification;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.util.CoralEntitySelectionState;
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
 * @version $Id: Categorize.java,v 1.6 2007-11-18 21:26:00 rafal Exp $
 */
public class Categorize extends BaseCategorizationAction
{
    
    
    public Categorize(Logger logger, StructureService structureService,
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
        
        // get resource categories
        Map temp = categorizationState.getEntities(coralSession, "selected");
        Set<Resource> categories = (Set<Resource>)temp.keySet();

        // perform categorization
        Relation refs = categoryService.getResourcesRelation(coralSession);
        
        HashSet<Resource> previousSet = new HashSet<Resource>();
        Resource[] previousCategories = refs.getInverted().get(resource);
        for(Resource res: previousCategories)
        {
            previousSet.add(res);
        }
        HashSet<Resource> changed = new HashSet<Resource>(categories);
        changed.removeAll(previousSet);
        previousSet.removeAll(categories);
        changed.addAll(previousSet);
        
        Permission classifyPermission = coralSession.getSecurity().getUniquePermission("cms.category.classify");
        for(Iterator<Resource> i=changed.iterator(); i.hasNext();)
        {
            Resource category = i.next();
            if(!subject.hasPermission(category, classifyPermission))
            {
                templatingContext.put("result","categories_not_granted");
                mvcContext.setView("category.Categorize");
                return;
            }
        }
        //      remove it from session
        CoralEntitySelectionState.removeState(context, categorizationState);

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
