package net.cyklotron.cms.modules.actions.structure.workflow;

import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.modules.actions.structure.BaseStructureAction;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * Base workflow action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: BaseWorkflowAction.java,v 1.1 2005-01-24 04:33:54 pablo Exp $
 */
public abstract class BaseWorkflowAction extends BaseStructureAction
{
    /** style service */
    protected WorkflowService workflowService;

    public BaseWorkflowAction()
    {
        workflowService = (WorkflowService)broker.getService(WorkflowService.SERVICE_NAME);
    }

    public boolean checkAccess(RunData data) throws ProcessingException
    {
        return checkAdministrator(context, coralSession);
    }

}
