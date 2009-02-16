package net.cyklotron.cms.modules.actions.structure.workflow;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.structure.BaseStructureAction;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * Base workflow action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: BaseWorkflowAction.java,v 1.2 2005-01-24 10:26:57 pablo Exp $
 */
public abstract class BaseWorkflowAction extends BaseStructureAction
{
    /** style service */
    protected WorkflowService workflowService;

    
    public BaseWorkflowAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, 
        WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        this.workflowService = workflowService;
    }

    public boolean checkAccessRights(Context context) throws ProcessingException
    {
        return checkAdministrator(context);
    }

}
