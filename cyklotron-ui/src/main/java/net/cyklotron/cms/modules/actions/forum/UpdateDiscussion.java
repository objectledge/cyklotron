package net.cyklotron.cms.modules.actions.forum;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.forum.CommentaryResource;
import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.DiscussionResourceImpl;
import net.cyklotron.services.workflow.ProtectedTransitionResource;
import net.cyklotron.services.workflow.ProtectedTransitionResourceImpl;
import net.cyklotron.services.workflow.WorkflowException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateDiscussion.java,v 1.1 2005-01-24 04:34:01 pablo Exp $
 */
public class UpdateDiscussion
    extends BaseForumAction
    implements Secure
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        Context context = data.getContext();
        String name = parameters.get("name","");
        if(name.equals(""))
        {
            templatingContext.put("result","illegal_discussion_name");
            return;
        }
        long discussionId = parameters.getLong("did", -1);
        if (discussionId == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        String description = parameters.get("description","");
		String replyTo = parameters.get("reply_to","");

        try
        {
            DiscussionResource discussion = DiscussionResourceImpl.getDiscussionResource(coralSession,discussionId);
            if(discussion instanceof CommentaryResource)
            {
                ((CommentaryResource)discussion).setDocumentTitle(name);   
            }
            else
            {
                if(!discussion.getName().equals(name))
                {
                    coralSession.getStore().setName(discussion,name);
                }
            }
            discussion.setDescription(description);
			discussion.setReplyTo(replyTo);
            discussion.update(subject);
            long transitionId = parameters.getLong("transition", 0);
            if(transitionId != 0)
            {
                ProtectedTransitionResource transition = ProtectedTransitionResourceImpl.
                    getProtectedTransitionResource(coralSession,transitionId);
                workflowService.performTransition(discussion, transition, subject);
            }
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("ForumException: ",e);
            return;
        }
        catch(WorkflowException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("WorkflowException: ",e);
            return;
        }
        templatingContext.put("result","updated_successfully");
    }


    public boolean checkAccess(RunData data)
    {
        long discussionId = parameters.getLong("did", -1);
        if (discussionId == -1)
        {
            return true;
        }
        try
        {
            DiscussionResource discussion = DiscussionResourceImpl.getDiscussionResource(coralSession, discussionId);
            Permission permission = coralSession.getSecurity().getUniquePermission("cms.forum.modify");
            return coralSession.getUserSubject().hasPermission(discussion, permission);
        }
        catch(Exception e)
        {
            log.error("Subject has no rights to modify this discussion" , e);
            return false;
        }    
    }    

}


