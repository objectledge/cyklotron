package net.cyklotron.cms.modules.components;

import java.util.Iterator;
import java.util.List;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.structure.NavigationNodeResource;

public class EmbeddedScreen extends SkinableCMSComponent
{
    public static String SCREEN_ERROR_MESSAGES_KEY = "screen_error_messages";
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        // TODO: Think of a better way of keeping the screen error messages
        List errors = (List)(context.get(SCREEN_ERROR_MESSAGES_KEY));
        
        if(errors != null && errors.size() > 0)
        {
            NavigationNodeResource currentNode = getNode();
            
            for(Iterator i=errors.iterator(); i.hasNext();)
            {
                componentError(context, (String)(i.next()));
            }
        }
    }
}
