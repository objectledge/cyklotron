package net.cyklotron.cms.modules.actions.forum;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.forum.MessageResourceImpl;
import net.cyklotron.services.workflow.ProtectedTransitionResource;
import net.cyklotron.services.workflow.ProtectedTransitionResourceImpl;
import net.cyklotron.services.workflow.WorkflowException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateMessage.java,v 1.1 2005-01-24 04:34:01 pablo Exp $
 */
public class UpdateMessage
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
                workflowService.performTransition(message, transition, subject);
            }
        }
        catch(ValueRequiredException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("ForumException: ",e);
            return;
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
            log.error("Subject has no rights to modify this message" , e);
            return false;
        }    
    }    

}


