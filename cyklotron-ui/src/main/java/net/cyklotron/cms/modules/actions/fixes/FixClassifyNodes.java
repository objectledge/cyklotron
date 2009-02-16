package net.cyklotron.cms.modules.actions.fixes;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.relation.RelationModification;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.StateResource;

/**
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id$
 */
public class FixClassifyNodes
    extends BaseCMSAction
{
	CategoryService categoryService;
	
    public FixClassifyNodes(Logger logger, StructureService structureService, 
        CmsDataFactory cmsDataFactory, CategoryService categoryService)
    {
        super(logger, structureService, cmsDataFactory);
		this.categoryService = categoryService;
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext,
        HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        CategoryResource positiveCategory = structureService.getPositiveCategory();
        CategoryResource negativeCategory = structureService.getNegativeCategory();
        Relation refs = categoryService.getResourcesRelation(coralSession);
        QueryResults results;
        try
        {
            results = coralSession.getQuery().
                executeQuery("FIND RESOURCE FROM documents.document_node");
        }
        catch(MalformedQueryException e)
        {
            throw new ProcessingException("cannot get 'documents.document_node' resources", e);
        }
        RelationModification diff = new RelationModification();
        Resource[] resources = results.getArray(1);
        for(Resource res : resources)
        {
            NavigationNodeResource node = (NavigationNodeResource)res; 
            StateResource state = node.getState();
            if(state == null || state.getName().equals("published"))
            {
                diff.add(positiveCategory, node);
            }
            else
            {
                diff.add(negativeCategory, node);
            }
        }
        coralSession.getRelationManager().updateRelation(refs, diff);
    }
}
