package net.cyklotron.cms.modules.actions.structure.workflow;

import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.services.workflow.TransitionResource;
import net.labeo.services.authentication.AuthenticationService;
import net.labeo.services.authentication.UnknownUserException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Assign to transition action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: AssignTo.java,v 1.2 2005-01-24 10:26:57 pablo Exp $
 */
public class AssignTo extends BaseWorkflowAction
{
    protected AuthenticationService authenticationService;

    public AssignTo()
    {
        authenticationService = (AuthenticationService)broker.getService(AuthenticationService.SERVICE_NAME);
    }

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
        String login = parameters.get("subject_name","");
        if (login.length() == 0)
        {
            templatingContext.put("result", "subject_not_chosen");
            return;
        }
        try
        {
            String dn;
            try
            {
                dn = authenticationService.getUserByLogin(login).getName();
            }
            catch(UnknownUserException e)
            {
                templatingContext.put("result", "subject_not_found");
                return;
            }
            Subject owner = coralSession.getSecurity().getSubject(dn);
            NavigationNodeResource node = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, nodeId);
            Permission permission = coralSession.getSecurity().getUniquePermission("cms.structure.modify_own");
            if (!owner.hasPermission(node, permission))
            {
                templatingContext.put("result", "subject_is_not_the_redactor");
                return;
            }
            coralSession.getStore().setOwner(node, owner);
            if (node.getState() != null && node.getState().getName().equals("new"))
            {
                TransitionResource[] transitions = workflowService.getTransitions(node.getState());
                int i = 0;
                for (; i < transitions.length; i++)
                {
                    if (transitions[i].getName().equals("assign"))
                    {
                        break;
                    }
                }
                if (i == transitions.length)
                {
                    templatingContext.put("result", "illegal_transition_name");
                    log.error("Coudn't find transition 'assign' for state '" + node.getState().getName() + "'");
                    return;
                }
                node.setState(transitions[i].getTo());
                node.update(subject);
                workflowService.enterState(node, transitions[i].getTo());
            }
        }
        catch (Exception e)
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
		try
		{
			long nodeId = parameters.getLong("node_id", -1);
			NavigationNodeResource node = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, nodeId);
			Permission permission = coralSession.getSecurity().getUniquePermission("cms.structure.modify");
			return coralSession.getUserSubject().hasPermission(node, permission);
		}
		catch(Exception e)
		{
			throw new ProcessingException("Exception occured during access rights checking",e);
		}
	}
}
