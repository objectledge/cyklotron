package net.cyklotron.cms.modules.actions.structure.workflow;

import java.util.Calendar;
import java.util.Date;

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

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;
import net.cyklotron.cms.workflow.StateResource;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * Assign to transition action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ForcePublication.java,v 1.3 2005-01-25 08:24:44 pablo Exp $
 */
public class ForcePublication extends BaseWorkflowAction
{
    public ForcePublication(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, WorkflowService workflowService)
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
                node.update();
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
