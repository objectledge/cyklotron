package net.cyklotron.cms.modules.actions.forum;

import net.cyklotron.cms.forum.ForumConstants;
import net.cyklotron.cms.forum.ForumException;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.workflow.WorkflowService;

import org.objectledge.pipeline.ProcessingException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BaseForumAction.java,v 1.1 2005-01-24 04:34:01 pablo Exp $
 */
public abstract class BaseForumAction
    extends BaseCMSAction
    implements ForumConstants
{
    protected Logger log;

    protected ForumService forumService;
    
    protected WorkflowService workflowService;
    
    public BaseForumAction()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(ForumService.LOGGING_FACILITY);
        forumService = (ForumService)broker.getService(ForumService.SERVICE_NAME);
        workflowService = (WorkflowService)broker.getService(WorkflowService.SERVICE_NAME);
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        try
        {
            return coralSession.getUserSubject().hasRole(forumService.getForum(getSite(context)).getAdministrator());
        }
        catch(ProcessingException e)
        {
            log.error("Subject has no rights to view this screen");
            return false;
        }
        catch(ForumException e)
        {
            log.error("Subject has no rights to view this screen");
            return false;
        }
    }
}


