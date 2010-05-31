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
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;
import net.cyklotron.cms.workflow.StatefulResource;
import net.cyklotron.cms.workflow.TransitionResource;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * Simple fire transition action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FireTransition.java,v 1.5 2006-05-15 08:49:07 pablo Exp $
 */
public class FireTransition
    extends BaseWorkflowAction
{
    public FireTransition(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, styleService, workflowService);
        
    }
    /**
     * Performs the action.
     */
    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        
        long nodeId = parameters.getLong("node_id", -1);
        if (nodeId == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        String transitionName = parameters.get("transition","");
        try
        {
            NavigationNodeResource node = (NavigationNodeResource)coralSession.getStore().getResource(nodeId);
            structureService.fireTransition(coralSession, node, transitionName, subject);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("ResourceException: ",e);
            return;
        }
        catch(StructureException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("ResourceException: ",e);
            return;
        }
        templatingContext.put("result","changed_successfully");
    }

    @Override
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        try
        {
            long nodeId = parameters.getLong("node_id", -1);
            String transitionName = parameters.get("transition","");
            NavigationNodeResource node = NavigationNodeResourceImpl.
                getNavigationNodeResource(coralSession, nodeId);
            Subject subject = coralSession.getUserSubject();
            TransitionResource transition = workflowService.getTransition(coralSession, node
                .getState(), transitionName);            
            return node.canPerform(coralSession, subject, transition);
        }
        catch(Exception e)
        {
            logger.error("Exception during access check",e);
            return false;
        }
    }
}
