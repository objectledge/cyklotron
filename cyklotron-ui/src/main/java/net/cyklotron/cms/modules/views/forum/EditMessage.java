package net.cyklotron.cms.modules.views.forum;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.forum.MessageResourceImpl;
import net.cyklotron.services.workflow.WorkflowException;

/**
 * The message screen class.
 */
public class EditMessage
    extends BaseForumScreen
    implements Secure
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        long mid = parameters.getLong("mid", -1);
        if(mid == -1)
        {
            throw new ProcessingException("Message id not found");
        }
        try
        {
            MessageResource message = MessageResourceImpl.getMessageResource(coralSession,mid);
            templatingContext.put("message",message);
            templatingContext.put("transitions",workflowService.getTransitions(message.getState()));
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("Resource not found",e);
        }
        catch(WorkflowException e)
        {
            throw new ProcessingException("WorkflowException",e);
        }
    }    
}
