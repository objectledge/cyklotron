package net.cyklotron.cms.modules.actions.forum;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.forum.MessageResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddMessage.java,v 1.7 2007-02-25 14:14:11 pablo Exp $
 */
public class MoveMessage
    extends BaseForumAction
{
    
    public MoveMessage(Logger logger, StructureService structureService,
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
        
        long parentId = parameters.getLong("parent_id", -1);
        if(parentId == -1)
        {
            throw new ProcessingException("message id not found");
        }
        
        long mid = parameters.getLong("mid", -1);
        if(mid == -1)
        {
            throw new ProcessingException("message id not found");
        }
                
        try
        {
            MessageResource parent = MessageResourceImpl.getMessageResource(coralSession, parentId);
            MessageResource message = MessageResourceImpl.getMessageResource(coralSession, mid);
            
            if(parent != null && parent.getDiscussion().equals(message.getDiscussion()));
            {
                coralSession.getStore().setParent(message, parent);
            }
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("resource not fount ", e);
        }
        catch(Exception e)
        {
            throw new ProcessingException("Component exception: ", e);
        }
    }


    public boolean checkAccessRights(Context context) 
    throws ProcessingException
    {
        
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        
        long parentId = parameters.getLong("parent_id", -1);
        if(parentId == -1)
        {
            throw new ProcessingException("resource not fount.");
        }
        
        try
        {
            MessageResource parent = MessageResourceImpl.getMessageResource(coralSession, parentId);
            if(parent == null)
            {
                throw new ProcessingException("source not fount.");
            }
            
            Permission moderatePermission = coralSession.getSecurity().getUniquePermission(
                "cms.forum.moderate");
            return coralSession.getUserSubject().hasPermission(parent.getDiscussion(), moderatePermission);
        }
        catch(EntityDoesNotExistException e)
        {
            return false;
        }
    }
}


