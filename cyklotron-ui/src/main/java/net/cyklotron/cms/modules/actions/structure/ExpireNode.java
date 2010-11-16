package net.cyklotron.cms.modules.actions.structure;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.modules.actions.structure.workflow.BaseWorkflowAction;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateNode.java,v 1.13 2008-03-15 13:28:11 pablo Exp $
 */
public class ExpireNode
    extends BaseWorkflowAction
{
    public ExpireNode(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, styleService, workflowService);
    }

    /**
     * Performs the action.
     */
    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
    {

        long nodeId = parameters.getLong("node_id", -1L);

        if(nodeId == -1L)
        {
            templatingContext.put("result", "parameter_not_found");
            return;
        }

        try
        {
            Subject subject = coralSession.getUserSubject();
            NavigationNodeResource node = NavigationNodeResourceImpl.getNavigationNodeResource(
                coralSession, nodeId);

            // set ValidityEnd as expired.
            Calendar calendar = Calendar.getInstance();
            calendar.set(3000, 12, 31);
            node.setValidityStart(calendar.getTime());
            // save node description if added at ReviewProposedChanges view.
            if(parameters.isDefined("admin_description")
                && !parameters.get("admin_description", "").equals(node.getDescription()))
            {
                node.setDescription(parameters.get("admin_description", ""));
            }
            String state = node.getState().getName();
            String transition = "expire";
            List<String> transitions = Arrays.asList("new", "taken", "assigned", "prepared",
                "published");

            if(transitions.contains(state))
            {
                if(!"published".equals(state))
                {
                    transition += "_" + state;
                }
                structureService.fireTransition(coralSession, node, transition, subject);
                ((DocumentNodeResource)node).setProposedContent(null);
            }
        }
        catch(EntityDoesNotExistException e)
        {
            logger.error("Exception: ", e);
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        catch(Exception e)
        {
            logger.error("Exception: ", e);
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
            return;
        }

    }

    @Override
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);

        try
        {
            long nodeId = parameters.getLong("node_id", -1L);
            NavigationNodeResource node = NavigationNodeResourceImpl.getNavigationNodeResource(
                coralSession, nodeId);
            Permission permission = coralSession.getSecurity().getUniquePermission(
                "cms.structure.modify");
            return getCmsData(context).getNode().canModify(coralSession,
                coralSession.getUserSubject());
        }
        catch(Exception e)
        {
            logger.error("Exception during access check", e);
            return false;
        }
    }
}
