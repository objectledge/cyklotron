package net.cyklotron.cms.modules.views.forum;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.forum.MessageResourceImpl;


/**
 * The message screen class.
 */
public class Message
    extends BaseForumScreen
    implements Secure
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        long mid = parameters.getLong("mid", -1);
        if(mid == -1)
        {
            throw new ProcessingException("Message id not found");
        }
        try
        {
            MessageResource message = MessageResourceImpl.getMessageResource(coralSession,mid);
            templatingContext.put("message",message);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("Resource not found",e);
        }
    }    
}
