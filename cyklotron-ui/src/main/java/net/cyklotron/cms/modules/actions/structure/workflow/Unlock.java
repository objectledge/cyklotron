package net.cyklotron.cms.modules.actions.structure.workflow;

import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.services.workflow.TransitionResource;
import net.cyklotron.services.workflow.WorkflowException;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Unlock the document action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Unlock.java,v 1.1 2005-01-24 04:33:54 pablo Exp $
 */
public class Unlock extends BaseWorkflowAction
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
            TransitionResource[] transitions = workflowService.getTransitions(node.getState());
            int i = 0;
            for (; i < transitions.length; i++)
            {
                if (transitions[i].getName().equals("unlock"))
                {
                    break;
                }
            }
            if (i == transitions.length)
            {
                templatingContext.put("result", "illegal_transition_name");
                log.error("Coudn't find transition 'unlock' for state '" + node.getState().getName() + "'");
                return;
            }
            node.setLockedBy(null);
            node.setState(transitions[i].getTo());
            node.update(subject);
            workflowService.enterState(node, transitions[i].getTo());
            data.getRequest().getSession().removeAttribute("net.cyklotron.cms.modules.actions.structure.workflow." + node.getIdString());
        }
        catch (EntityDoesNotExistException e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", net.labeo.util.StringUtils.stackTrace(e));
            log.error("ResourceException: ", e);
            return;
        }
        catch (WorkflowException e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", net.labeo.util.StringUtils.stackTrace(e));
            log.error("ResourceException: ", e);
            return;
        }
        templatingContext.put("result", "changed_successfully");
    }

    public boolean checkAccess(RunData data) throws ProcessingException
    {
        long nodeId = parameters.getLong("node_id", -1);
        try
        {
            NavigationNodeResource node = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, nodeId);
            if (node.getState() == null || !node.getState().equals("locked"))
            {
                // that would be checked in prepare method
                return true;
            }
            Subject subject = coralSession.getUserSubject();
            Permission permission = coralSession.getSecurity().getUniquePermission("cms.structure.delete");
            if (subject.equals(node.getOwner()) || subject.hasPermission(node, permission))
            {
                return true;
            }
        }
        catch (Exception e)
        {
            log.error("Exception occured during permission check ", e);
        }
        return false;
    }
}
