package net.cyklotron.cms.modules.actions.structure.workflow;

import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;

/**
 * Assign to transition action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: MoveToWaitingRoom.java,v 1.2 2005-01-24 10:26:57 pablo Exp $
 */
public class MoveToWaitingRoom extends BaseWorkflowAction
{
    public static final String WAITING_ROOM_NAME = "waiting_room";
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
            Resource homePage = getHomePage(context);
            // hack!!! but who cares if whole the action is one big hack!
            Resource[] parents = coralSession.getStore().getResource(homePage,WAITING_ROOM_NAME);
            Resource parent = null;
            if(parents.length == 0)
            {
                parent = structureService.addDocumentNode(
                    WAITING_ROOM_NAME, WAITING_ROOM_NAME, homePage, subject);
            }
            else
            {
                parent = parents[0];
            }
            coralSession.getStore().setParent(node, parent);
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
