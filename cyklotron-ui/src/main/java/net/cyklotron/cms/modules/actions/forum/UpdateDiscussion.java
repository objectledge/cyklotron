package net.cyklotron.cms.modules.actions.forum;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.CommentaryResource;
import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.DiscussionResourceImpl;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.ProtectedTransitionResource;
import net.cyklotron.cms.workflow.ProtectedTransitionResourceImpl;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateDiscussion.java,v 1.5 2005-06-13 11:08:38 rafal Exp $
 */
public class UpdateDiscussion
    extends BaseForumAction
{
    
    public UpdateDiscussion(Logger logger, StructureService structureService,
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
            discussion.update();
            long transitionId = parameters.getLong("transition", 0);
            if(transitionId != 0)
            {
                ProtectedTransitionResource transition = ProtectedTransitionResourceImpl.
                    getProtectedTransitionResource(coralSession,transitionId);
                workflowService.performTransition(coralSession, discussion, transition);
            }
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("ForumException: ",e);
            return;
        }
        catch(WorkflowException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("WorkflowException: ",e);
            return;
        }
        catch(InvalidResourceNameException e)
        {
            templatingContext.put("result","name_invalid");
            return;
        }
        templatingContext.put("result","updated_successfully");
    }


    public boolean checkAccessRights(Context context)
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
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
            logger.error("Subject has no rights to modify this discussion" , e);
            return false;
        }    
    }    

}


