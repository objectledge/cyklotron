package net.cyklotron.cms.modules.actions.structure.workflow;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import java.util.Date;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * Assign to transition action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: MoveToWaitingRoom.java,v 1.6 2008-03-15 13:28:12 pablo Exp $
 */
public class MoveToWaitingRoom extends BaseWorkflowAction
{
    public MoveToWaitingRoom(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, styleService, workflowService);
        
    }
    public static final String WAITING_ROOM_NAME = "waiting_room";
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        
        long[] nodeIds = parameters.getLongs("op_node_id");
        if (nodeIds.length == 0)
        {
            templatingContext.put("result", "no_nodes_selected");
            return;
        }
        try
        {
            Resource homePage = getHomePage(context);
            // hack!!! but who cares if whole the action is one big hack!
            Resource[] parents = coralSession.getStore().getResource(homePage,WAITING_ROOM_NAME);
            Resource parent = null;
            if(parents.length == 0)
            {
                parent = structureService.addDocumentNode(coralSession,
                    WAITING_ROOM_NAME, WAITING_ROOM_NAME, homePage, subject);
            }
            else
            {
                parent = parents[0];
            }
            for(long nodeId : nodeIds)
            {
                NavigationNodeResource node = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, nodeId);
                Resource newParent = parent;
                if(node.getValidityStart() != null)
                {
                    newParent = structureService.getParent(coralSession, parent, 
                            node.getValidityStart(),
                            StructureService.MONTHLY_CALENDAR_TREE_STRUCTURE,subject);
                }
                coralSession.getStore().setParent(node, newParent);
            }
        }
        catch (Exception e)
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
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
		try
		{
		    long[] nodeIds = parameters.getLongs("op_node_id");
		    for(long nodeId : nodeIds)
		    {
		        NavigationNodeResource node = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, nodeId);
		        Permission permission = coralSession.getSecurity().getUniquePermission("cms.structure.modify");
		        if(!coralSession.getUserSubject().hasPermission(node, permission))
		        {
		            return false;
		        }
		    }
		    return true;
		}
		catch(Exception e)
		{
			throw new ProcessingException("Exception occured during access rights checking",e);
		}
	}
}
