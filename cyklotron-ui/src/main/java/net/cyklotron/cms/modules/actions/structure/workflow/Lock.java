package net.cyklotron.cms.modules.actions.structure.workflow;

import net.cyklotron.cms.structure.LockedBySessionListener;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.services.workflow.TransitionResource;
import net.cyklotron.services.workflow.WorkflowException;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Lock the document to the edition action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Lock.java,v 1.2 2005-01-24 10:26:57 pablo Exp $
 */
public class Lock extends BaseWorkflowAction
{

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        Context context = data.getContext();
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
            TransitionResource[] transitions = workflowService.getTransitions(node.getState());
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
                log.error("Coudn't find transition 'lock' for state '" + node.getState().getName() + "'");
                return;
            }
            node.setLockedBy(subject);
            node.setState(transitions[i].getTo());
            node.update(subject);
            workflowService.enterState(node, transitions[i].getTo());
            data.getRequest().getSession().setAttribute(
                "net.cyklotron.cms.modules.actions.structure.workflow." + node.getIdString(),
                new LockedBySessionListener(node, subject));
        }
        catch (EntityDoesNotExistException e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
            log.error("ResourceException: ", e);
            return;
        }
        catch (WorkflowException e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
            log.error("ResourceException: ", e);
            return;
        }
        templatingContext.put("result", "changed_successfully");
    }

    public boolean checkAccessRights(Context context) throws ProcessingException
    {
        return true;
    }

}
