package net.cyklotron.cms.modules.actions.forum;

import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.DiscussionResourceImpl;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.forum.MessageResourceImpl;
import net.cyklotron.cms.site.SiteResource;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddMessage.java,v 1.1 2005-01-24 04:34:01 pablo Exp $
 */
public class AddMessage
    extends BaseForumAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        try
        {
            Subject subject = coralSession.getUserSubject();
            String instanceName = parameters.get("ci","");
            templatingContext.put("result_scope", "forum_"+instanceName);
            String name = parameters.get("name","");
            if(name.equals(""))
            {
                templatingContext.put("result","illegal_message_name");
                return;
            }
            long parentId = parameters.getLong("parent", -1);
            long discussionId = parameters.getLong("did", -1);
            long resourceId = parameters.getLong("resid", -1);
            if((parentId == -1 || discussionId == -1) && resourceId == -1)
            {
                templatingContext.put("result","parameter_not_found");
                return;
            }

            DiscussionResource discussion = null;
            Resource parent = null;
            if(parentId != -1)
            {
                discussion = DiscussionResourceImpl.getDiscussionResource(coralSession,discussionId);
                parent = coralSession.getStore().getResource(parentId);
            }
            else
            {
                Resource res = coralSession.getStore().getResource(resourceId);
                SiteResource site = CmsTool.getSite(res);
                ForumResource forum = forumService.getForum(site);
                discussion = forumService.createCommentary(forum, "comments/"+Long.toString(resourceId), res, 
                    site.getOwner(), site.getOwner());
                parent = discussion;
            }
            
            if(discussion.getState().getName().equals("hidden"))
            {
				templatingContext.put("result","hidden_discussion");
				return;
            }
            
            String content = parameters.get("content","");
            content = StringUtils.wrap(content, 78);
            int priority = parameters.getInt("priority", 0);

            MessageResource message = MessageResourceImpl.
                createMessageResource(coralSession, name, parent,
                                      name, content, priority, data.getEncoding(), discussion,
                                      subject);
            String author = parameters.get("author","");
            String email = parameters.get("email","");
            message.setAuthor(author);
            message.setEmail(email);
            message.update(subject);
            // workflow
            if(discussion.getForum().getSite() != null)
            {
                workflowService.assignState(discussion.getForum().getSite().getParent().getParent(), message, subject);
            }
            else
            {
                workflowService.assignState(null, message, subject);
            }
			if(discussion.getState().getName().equals("open"))
			{
				workflowService.performTransition(message, "accept", subject);		
                templatingContext.put("result","added_successfully");
			}
            else
            {
                templatingContext.put("result","added_for_moderation_successfully");
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("ForumException: ",e);
            return;
        }
    }


    public boolean checkAccess(RunData data) 
        throws ProcessingException
    {
        Permission forumAdd = coralSession.getSecurity().getUniquePermission("cms.forum.add");
        long parentId = parameters.getLong("parent", -1);
        if(parentId != -1)
        {
            try
            {
                Resource parent = coralSession.getStore().getResource(parentId);
                return coralSession.getUserSubject().hasPermission(parent, forumAdd);
            }   
            catch(Exception e)
            {
                throw new ProcessingException("failed to check access rights", e);
            }    
        }
        else
        {
            long resId = parameters.getLong("resid", -1);
            if(resId == -1)
            {
                log.error("forum,AddMessage action: parent nor resid undefined");
                return false; 
            }
            else
            {
                try
                {
                    Resource res = coralSession.getStore().getResource(resId);
                    SiteResource site = CmsTool.getSite(res);
                    ForumResource forum = forumService.getForum(site);
                    Resource[] r = coralSession.getStore().getResource(forum, "discussions");
                    if(r.length == 0)
                    {
                        return coralSession.getUserSubject().hasPermission(forum, forumAdd);
                    }
                    else
                    {
                        return coralSession.getUserSubject().hasPermission(r[0], forumAdd);
                    }
                }
                catch (Exception e)
                {
                    throw new ProcessingException("failed to check access rights", e);
                }
            }
        }
    }
}


