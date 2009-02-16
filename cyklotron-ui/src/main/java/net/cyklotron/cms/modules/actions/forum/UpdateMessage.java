package net.cyklotron.cms.modules.actions.forum;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.forum.MessageResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.ProtectedTransitionResource;
import net.cyklotron.cms.workflow.ProtectedTransitionResourceImpl;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateMessage.java,v 1.6 2007-02-25 14:14:11 pablo Exp $
 */
public class UpdateMessage
    extends BaseForumAction
{
    public UpdateMessage(Logger logger, StructureService structureService,
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
            templatingContext.put("result","illegal_message_name");
            return;
        }
        long messageId = parameters.getLong("mid", -1);
        if (messageId == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        String content = parameters.get("content","");
        int priority = parameters.getInt("priority", 0);
        try
        {
            MessageResource message = MessageResourceImpl.getMessageResource(coralSession,messageId);
            if(!message.getName().equals(name))
            {
                coralSession.getStore().setName(message,name);
            }
            message.setContent(content);
            message.setPriority(priority);
            long transitionId = parameters.getLong("transition", 0);
            if(transitionId != 0)
            {
                ProtectedTransitionResource transition = ProtectedTransitionResourceImpl.
                    getProtectedTransitionResource(coralSession,transitionId);
                workflowService.performTransition(coralSession, message, transition);
            }
        }
        catch(ValueRequiredException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("ForumException: ",e);
            return;
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
    throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("forum"))
        {
            logger.debug("Application 'forum' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);

        long messageId = parameters.getLong("mid", -1);
        if (messageId == -1)
        {
            return true;
        }
        try
        {
            MessageResource message = MessageResourceImpl.getMessageResource(coralSession, messageId);
            Permission permission = coralSession.getSecurity().getUniquePermission("cms.forum.modify");
            return coralSession.getUserSubject().hasPermission(message, permission);
        }
        catch(Exception e)
        {
            logger.error("Subject has no rights to modify this message" , e);
            return false;
        }    
    }    

}


