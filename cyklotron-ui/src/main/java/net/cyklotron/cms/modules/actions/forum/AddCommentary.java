package net.cyklotron.cms.modules.actions.forum;

import net.cyklotron.cms.forum.CommentaryResource;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.forum.ForumResourceImpl;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: AddCommentary.java,v 1.1 2005-01-24 04:34:01 pablo Exp $
 */
public class AddCommentary
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
        String path = parameters.get("path","");
        long forumId = parameters.getLong("fid", -1);
        if (forumId == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        String description = parameters.get("description","");
        String adminName = parameters.get("admin",subject.getName());
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
        	path = forum.getSite().getPath()+"/structure"+path;
			NavigationNodeResource node = (NavigationNodeResource)coralSession.
				getStore().getUniqueResourceByPath(path);
            String name = "comments/"+node.getIdString();
            Resource[] resources = coralSession.getStore().getResourceByPath(forum.getPath()+"/"+name);
            if(resources.length > 0)
            {
				templatingContext.put("result","commentary_already_exists");
				return;
            }
            CommentaryResource commentary = forumService.
            	createCommentary(forum, name, node, admin, subject);
            commentary.setDescription(description);
           	commentary.update(subject);
            if(state.equals("moderated"))
            {
				workflowService.performTransition(commentary, "show.moderated", subject);            	            	
            }
			if(state.equals("open"))
			{
				workflowService.performTransition(commentary, "show.open", subject);
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


