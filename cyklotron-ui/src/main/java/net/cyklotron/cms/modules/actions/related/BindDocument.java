package net.cyklotron.cms.modules.actions.related;

import static net.cyklotron.cms.modules.views.BaseChooseResource.STATE_NAME;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentNodeResourceImpl;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

/**
 * Update resource relationships.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UpdateRelations.java,v 1.9 2007-12-20 16:57:05 rafal Exp $
 */
public class BindDocument
    extends BaseRelatedAction
{

    private final TableStateManager tableStateManager;

    public BindDocument(Logger logger, StructureService structureService,
        TableStateManager tableStateManager, CmsDataFactory cmsDataFactory,
        RelatedService relatedService)
    {
        super(logger, structureService, cmsDataFactory, relatedService);
        this.tableStateManager = tableStateManager;
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {

        try
        {
            long resId = parameters.getLong("res_id", -1L);
            long docId = parameters.getLong("doc_res_id", -1L);

            if(docId != -1L)
            {
                templatingContext.put("doc_res_id", docId);
                Resource resource = coralSession.getStore().getResource(resId);
                DocumentNodeResource documentResource = DocumentNodeResourceImpl
                    .getDocumentNodeResource(coralSession, docId);
                NavigationNodeResource currentNode = DocumentNodeResourceImpl
                    .getNavigationNodeResource(coralSession, docId);

                ResourceSelectionState relatedState = ResourceSelectionState.getState(context,
                    RELATED_SELECTION_STATE + ":" + resource.getIdString());

                relatedState.update(parameters);
                relatedState.setValue(documentResource, "selected");

                TableState state = tableStateManager.getState(context, STATE_NAME);
                state.setExpanded(currentNode.getIdString());

                while(currentNode.getParent() instanceof NavigationNodeResource)
                {
                    currentNode = (NavigationNodeResource)currentNode.getParent();
                    state.setExpanded(currentNode.getIdString());
                }
                state.setExpanded((currentNode.getParent()).getIdString());
            }
        }
        catch(IllegalArgumentException e)
        {

            templatingContext.put("result", "resource is not documents.document_node");
            return;

        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result", "resource does not exist");
            return;

        }
        catch(Exception e)
        {

            logger.error("exception: ", e);
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
            return;
        }

    }

}
