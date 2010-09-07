package net.cyklotron.cms.modules.actions.structure.workflow;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.structure.LockedBySessionListener;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;
import net.cyklotron.cms.workflow.TransitionResource;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * Lock the document to the edition action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Lock.java,v 1.4 2005-03-08 10:54:27 pablo Exp $
 */
public class Lock extends BaseWorkflowAction
{

    public Lock(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
        StyleService styleService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, styleService, workflowService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        
        long nodeId = parameters.getLong("node_id", -1);
        if (nodeId == -1)
        {
            templatingContext.put("result", "parameter_not_found");
            return;
        }
        try
        {
            NavigationNodeResource node = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, nodeId);
            if (node.getOwner() == null || !node.getOwner().equals(subject))
            {
                templatingContext.put("result", "subject_not_the_owner");
                return;
            }
            TransitionResource[] transitions = workflowService.getTransitions(coralSession, node.getState());
            int i = 0;
            for (; i < transitions.length; i++)
            {
                if (transitions[i].getName().equals("lock"))
                {
                    break;
                }
            }
            if (i == transitions.length)
            {
                templatingContext.put("result", "illegal_transition_name");
                logger.error("Coudn't find transition 'lock' for state '" + node.getState().getName() + "'");
                return;
            }
            node.setLockedBy(subject);
            node.setState(transitions[i].getTo());
            node.update();
            workflowService.enterState(coralSession, node, transitions[i].getTo());
            httpContext.getRequest().getSession().setAttribute(
                "net.cyklotron.cms.modules.actions.structure.workflow." + node.getIdString(),
                new LockedBySessionListener(node, subject));
        }
        catch (EntityDoesNotExistException e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
            logger.error("ResourceException: ", e);
            return;
        }
        catch (WorkflowException e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
            logger.error("ResourceException: ", e);
            return;
        }
        templatingContext.put("result", "changed_successfully");
    }

    public boolean checkAccessRights(Context context) throws ProcessingException
    {
        return true;
    }

}
