package net.cyklotron.cms.modules.actions.forum;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Permission;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.forum.MessageResourceImpl;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteMessage.java,v 1.1 2005-01-24 04:34:01 pablo Exp $
 */
public class DeleteMessage
    extends BaseForumAction
    implements Secure
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        long messageId = parameters.getLong("mid", -1);
        if (messageId == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        try
        {
            MessageResource message = MessageResourceImpl.getMessageResource(coralSession, messageId);
            coralSession.getStore().deleteResource(message);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("ResourceException: ",e);
            return;
        }
        catch(EntityInUseException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("ResourceException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
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
            Permission permission = coralSession.getSecurity().getUniquePermission("cms.forum.delete");
            return coralSession.getUserSubject().hasPermission(message, permission);
        }
        catch(Exception e)
        {
            log.error("Subject has no rights to delete this message" , e);
            return false;
        }    
    }    

}


