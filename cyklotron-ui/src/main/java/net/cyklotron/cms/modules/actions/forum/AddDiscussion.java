package net.cyklotron.cms.modules.actions.forum;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.forum.ForumResourceImpl;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: AddDiscussion.java,v 1.1 2005-01-24 04:34:01 pablo Exp $
 */
public class AddDiscussion
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
        long forumId = parameters.getLong("fid", -1);
        if (forumId == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        String description = parameters.get("description","");
        String adminName = parameters.get("admin",subject.getName());
        String replyTo = parameters.get("reply_to","");
        String state = parameters.get("state","");
        Subject admin;
        try
        {
            admin = coralSession.getSecurity().getSubject(adminName);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","admin_subject_not_found");
            return;
        }
        try
        {
            ForumResource forum = ForumResourceImpl.getForumResource(coralSession,forumId);
            name = "discussions/"+name;
            DiscussionResource discussion = forumService.createDiscussion(
                forum, name, admin, subject);
            discussion.setDescription(description);
            discussion.setReplyTo(replyTo);
            discussion.update(subject);
            if(state.equals("moderated"))
            {
				workflowService.performTransition(discussion, "show.moderated", subject);            	            	
            }
			if(state.equals("open"))
			{
				workflowService.performTransition(discussion, "show.open", subject);
			}
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("failed to create discussion",e);
            return;
        }
        templatingContext.put("result","added_successfully");
    }

    public boolean checkAccess(RunData data)
    {
        long forumId = parameters.getLong("fid", -1);
        if(forumId == -1)
        {
            log.error("Couldn't find forum id");
            return false;
        }
        try
        {
            ForumResource forum = ForumResourceImpl.getForumResource(coralSession,forumId);
            Permission forumAdd = coralSession.getSecurity().getUniquePermission("cms.forum.add");
            return coralSession.getUserSubject().hasPermission(forum, forumAdd);
        }
        catch(Exception e)
        {
            log.error("Subject has no rights to add discussion in this forum" , e);
            return false;
        }    
    }
    
}


