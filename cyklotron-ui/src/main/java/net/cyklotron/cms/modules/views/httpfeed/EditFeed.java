package net.cyklotron.cms.modules.views.httpfeed;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.httpfeed.HttpFeedResource;

/**
 * A list of feeds defined fo the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EditFeed.java,v 1.1 2005-01-24 04:34:18 pablo Exp $
 */
public class EditFeed extends BaseHttpFeedScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
    throws ProcessingException
    {
        if(parameters.get("feed_id").isDefined())
        {
            HttpFeedResource feed = getFeed(data);
            templatingContext.put("feed", feed);
        }
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.httpfeed.modify");
    }
}
