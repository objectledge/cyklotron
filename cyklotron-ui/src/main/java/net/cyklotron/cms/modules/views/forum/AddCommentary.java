package net.cyklotron.cms.modules.views.forum;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

/**
 * The empty add commentary screen.
 */
public class AddCommentary
    extends BaseForumScreen
    implements Secure
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {    	
    }
}
