package net.cyklotron.cms.modules.actions.structure.workflow;

import java.util.Calendar;
import java.util.Date;

import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.services.workflow.StateResource;

/**
 * Assign to transition action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ForcePublication.java,v 1.2 2005-01-24 10:26:57 pablo Exp $
 */
public class ForcePublication extends BaseWorkflowAction
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
            Date today = Calendar.getInstance().getTime();
            Date start = node.getValidityStart();
            Date end = node.getValidityEnd();
            String targetState = null;
            if(end != null && end.before(today))
            {
                targetState = "expired";
            }
            else
            {
                if(start == null || start.before(today))
                {
                    targetState = "published";
                }
                if(start != null && start.after(today))
                {
                    targetState = "accepted";
                }
            }
            if(targetState != null)
            {
                Resource state = coralSession.getStore(). 
                    getUniqueResourceByPath("/cms/workflow/automata/structure.navigation_node/states/"+targetState);
                node.setState((StateResource)state);
                node.setLastEditor(subject);
                node.update(subject);
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
