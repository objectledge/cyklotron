package net.cyklotron.cms.modules.actions.workflow;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.modules.actions.BaseCoralAction;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.workflow.StatefulResource;
import net.cyklotron.cms.workflow.TransitionResource;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: FireTransition.java,v 1.1 2005-03-09 09:59:31 pablo Exp $
 */
public class FireTransition
    extends BaseCoralAction
{
    /** logging facility */
    protected Logger log;

    /** workflow service */
    protected WorkflowService workflowService;

    public FireTransition(Logger logger, WorkflowService workflowService)
    {
        log = logger;
        this.workflowService = workflowService;
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        long resourceId = parameters.getLong("res_id",-1);
        if (resourceId == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        String transitionName = parameters.get("transition","");
        try
        {
            StatefulResource resource = (StatefulResource)coralSession.getStore().getResource(resourceId);
            workflowService.performTransition(coralSession, resource, transitionName, subject);
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("ResourceException: ",e);
            return;
        }
        templatingContext.put("result","changed_successfully");
    }
}


