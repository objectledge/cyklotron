package net.cyklotron.cms.modules.actions.structure.workflow;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.services.workflow.StatefulResource;
import net.cyklotron.services.workflow.TransitionResource;
import net.cyklotron.services.workflow.WorkflowException;

/**
 * Simple fire transition action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FireTransition.java,v 1.1 2005-01-24 04:33:54 pablo Exp $
 */
public class FireTransition
    extends BaseWorkflowAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        Context context = data.getContext();
        long nodeId = parameters.getLong("node_id", -1);
        if (nodeId == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        String transitionName = parameters.get("transition","");
        try
        {
            StatefulResource resource = (StatefulResource)coralSession.getStore().getResource(nodeId);
            TransitionResource[] transitions = workflowService.getTransitions(resource.getState());
            int i = 0;
            for(; i<transitions.length; i++)
            {
                if(transitions[i].getName().equals(transitionName))
                {
                    break;
                }
            }
            if(i == transitions.length)
            {
                templatingContext.put("result","illegal_transition_name");
                log.error("illegal transition name '"+transitionName+"' for state '"+resource.getState().getName()+"'");
                return;
            }
            resource.setState(transitions[i].getTo());
            workflowService.enterState(resource, transitions[i].getTo());
            if(!transitionName.equals("take_assigned") &&
               !transitionName.equals("take_rejected") &&
               !transitionName.equals("finish"))
            {
                ((NavigationNodeResource)resource).setLastEditor(subject);    
            }
            resource.update(subject);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("ResourceException: ",e);
            return;
        }
        catch(WorkflowException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("ResourceException: ",e);
            return;
        }
        templatingContext.put("result","changed_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        try
        {
            long nodeId = parameters.getLong("node_id", -1);
            String transitionName = parameters.get("transition","");
            NavigationNodeResource node = NavigationNodeResourceImpl.
                getNavigationNodeResource(coralSession, nodeId);
            Subject subject = coralSession.getUserSubject();
            Permission permission = null;
            if(transitionName.equals("take_assigned") ||
               transitionName.equals("take_rejected") ||
               transitionName.equals("finish"))
            {
                permission = coralSession.getSecurity().getUniquePermission("cms.structure.modify_own");
                return subject.hasPermission(node, permission);
            }
            if(transitionName.equals("reject_prepared") ||
               transitionName.equals("reject_accepted") ||
               transitionName.equals("reject_published") ||
               transitionName.equals("reject_expired") ||
               transitionName.equals("accept"))
            {
                permission = coralSession.getSecurity().getUniquePermission("cms.structure.modify");
                return subject.hasPermission(node, permission);
            }
            log.error("Invalid transition name");
            return false;
        }
        catch(Exception e)
        {
            log.error("Exception during access check",e);
            return false;
        }
    }
}
