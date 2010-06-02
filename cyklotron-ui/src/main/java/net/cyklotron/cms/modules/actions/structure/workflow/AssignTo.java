package net.cyklotron.cms.modules.actions.structure.workflow;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
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
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * Assign to transition action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: AssignTo.java,v 1.3 2005-01-25 08:24:44 pablo Exp $
 */
public class AssignTo extends BaseWorkflowAction
{
    protected UserManager userManager;
    
    public AssignTo(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, WorkflowService workflowService
        ,UserManager userManager)
    {
        super(logger, structureService, cmsDataFactory, styleService, workflowService);
        this.userManager = userManager;
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
                dn = userManager.getUserByLogin(login).getName();
            }
            catch(Exception e)
            {
                templatingContext.put("result", "subject_not_found");
                return;
            }
            Subject owner = coralSession.getSecurity().getSubject(dn);
            NavigationNodeResource node = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, nodeId);
            Permission permission = coralSession.getSecurity().getUniquePermission("cms.structure.modify_group");
            if (!owner.hasPermission(node, permission))
            {
                templatingContext.put("result", "subject_is_not_the_redactor");
                return;
            }
            coralSession.getStore().setOwner(node, owner);
            if (node.getState() != null && node.getState().getName().equals("new"))
            {
                structureService.fireTransition(coralSession, node, "assign", subject);
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
			return node.canModify(coralSession, coralSession.getUserSubject());
		}
		catch(Exception e)
		{
			throw new ProcessingException("Exception occured during access rights checking",e);
		}
	}
}
