package net.cyklotron.cms.modules.actions.forum;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.forum.ForumNodeResource;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.services.workflow.StateResource;
import net.cyklotron.services.workflow.StateResourceImpl;

/**
 * Updates forum application configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateConfiguration.java,v 1.1 2005-01-24 04:34:01 pablo Exp $
 */
public class UpdateConfiguration
    extends BaseForumAction
{
    /** structure service */
    protected StructureService structureService;

    public UpdateConfiguration()
    {
        structureService = (StructureService)broker.getService(StructureService.SERVICE_NAME);
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
    throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        
        SiteResource site = getSite(context);
        try 
        {
            String path = parameters.get("forumNodePath","");
            NavigationNodeResource forumNode = null;
            if(!path.equals(""))
            {
                Resource parent = structureService.getRootNode(site).getParent();
                Resource[] ress = coralSession.getStore().getResourceByPath(parent.getPath()+path);
                if(ress.length == 1)
                {
                    forumNode = (NavigationNodeResource)ress[0];
                }
                else if(ress.length > 1)
                {
                    throw new ProcessingException("multiple nodes with path="+path);
                }
                else // length == 0
                {
                    templatingContext.put("result", "no_node_with_given_path");
                    return;
                }
            }
            String replyTo = parameters.get("reply_to","");
  
            ForumResource forumRoot = forumService.getForum(site);
            forumRoot.setForumNode(forumNode);
            forumRoot.setReplyTo(replyTo);
            forumRoot.setLastlyAddedSize(parameters.get("forum_last_added_size").asInt(5));
            forumRoot.update(subject);
			long stateId = parameters.getLong("default_state", -1);
			if(stateId != -1)
			{
				StateResource state = StateResourceImpl.getStateResource(coralSession, stateId);
				forumService.setInitialCommentaryState(forumRoot, state, subject);
			}
			Resource[] resources = coralSession.getStore().getResource(forumRoot,"discussions");
			ForumNodeResource comments = ((ForumNodeResource)resources[0]);
			comments.setLastlyAddedSize(parameters.get("discussions_last_added_size").asInt(5));
			comments.update(subject);
			resources = coralSession.getStore().getResource(forumRoot,"comments");
			ForumNodeResource discussions = ((ForumNodeResource)resources[0]);
			discussions.setLastlyAddedSize(parameters.get("comments_last_added_size").asInt(5));
			forumService.addLastAdded(forumRoot, null, subject);
			forumService.addLastAdded(discussions, null, subject);
			forumService.addLastAdded(comments, null, subject);
			discussions.update(subject);
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured", e);
        }
        templatingContext.put("result","updated_successfully");
    }
}
