package net.cyklotron.cms.modules.actions.forum;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.ForumNodeResource;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.StateResource;
import net.cyklotron.cms.workflow.StateResourceImpl;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * Updates forum application configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateConfiguration.java,v 1.3 2005-03-08 10:52:12 pablo Exp $
 */
public class UpdateConfiguration
    extends BaseForumAction
{
    
    public UpdateConfiguration(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, ForumService forumService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, forumService, workflowService);
        
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
    throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        
        SiteResource site = getSite(context);
        try 
        {
            String path = parameters.get("forumNodePath","");
            NavigationNodeResource forumNode = null;
            if(!path.equals(""))
            {
                Resource parent = structureService.getRootNode(coralSession, site).getParent();
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
  
            ForumResource forumRoot = forumService.getForum(coralSession, site);
            forumRoot.setForumNode(forumNode);
            forumRoot.setReplyTo(replyTo);
            forumRoot.setLastlyAddedSize(parameters.getInt("forum_last_added_size",5));
            forumRoot.update();
			long stateId = parameters.getLong("default_state", -1);
			if(stateId != -1)
			{
				StateResource state = StateResourceImpl.getStateResource(coralSession, stateId);
				forumService.setInitialCommentaryState(coralSession, forumRoot, state);
			}
			Resource[] resources = coralSession.getStore().getResource(forumRoot,"discussions");
			ForumNodeResource comments = ((ForumNodeResource)resources[0]);
			comments.setLastlyAddedSize(parameters.getInt("discussions_last_added_size",5));
			comments.update();
			resources = coralSession.getStore().getResource(forumRoot,"comments");
			ForumNodeResource discussions = ((ForumNodeResource)resources[0]);
			discussions.setLastlyAddedSize(parameters.getInt("comments_last_added_size",5));
			forumService.addLastAdded(coralSession, forumRoot, null);
			forumService.addLastAdded(coralSession, discussions, null);
			forumService.addLastAdded(coralSession, comments, null);
			discussions.update();
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured", e);
        }
        templatingContext.put("result","updated_successfully");
    }
}
