package net.cyklotron.cms.modules.views.forum;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.DiscussionResourceImpl;
import net.cyklotron.services.workflow.WorkflowException;


/**
 * The edit discussion screen class.
 */
public class EditDiscussion
    extends BaseForumScreen
    implements Secure
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        long did = parameters.getLong("did", -1);
        if(did == -1)
        {
            throw new ProcessingException("Discussion id not found");
        }
        try
        {
            DiscussionResource discussion = DiscussionResourceImpl.getDiscussionResource(coralSession,did);
            templatingContext.put("discussion",discussion);
            templatingContext.put("transitions",workflowService.getTransitions(discussion.getState()));
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
