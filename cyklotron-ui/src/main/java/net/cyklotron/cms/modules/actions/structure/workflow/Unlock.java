package net.cyklotron.cms.modules.actions.structure.workflow;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;
import net.cyklotron.cms.workflow.TransitionResource;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * Unlock the document action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Unlock.java,v 1.3 2005-01-25 08:24:44 pablo Exp $
 */
public class Unlock extends BaseWorkflowAction
{

    public Unlock(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
        StyleService styleService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, styleService, workflowService);
        // TODO Auto-generated constructor stub
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
            TransitionResource[] transitions = workflowService.getTransitions(coralSession, node.getState());
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
                logger.error("Coudn't find transition 'unlock' for state '" + node.getState().getName() + "'");
                return;
            }
            node.setLockedBy(null);
            node.setState(transitions[i].getTo());
            node.update();
            workflowService.enterState(coralSession, node, transitions[i].getTo());
            httpContext.getRequest().getSession().removeAttribute("net.cyklotron.cms.modules.actions.structure.workflow." + node.getIdString());
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
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
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
            logger.error("Exception occured during permission check ", e);
        }
        return false;
    }
}
