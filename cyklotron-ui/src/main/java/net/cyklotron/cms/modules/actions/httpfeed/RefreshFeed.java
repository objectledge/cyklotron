package net.cyklotron.cms.modules.actions.httpfeed;

import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.httpfeed.HttpFeedResource;

/**
 * Action for explicit refreshing of http feeds in the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: RefreshFeed.java,v 1.1 2005-01-24 04:35:09 pablo Exp $
 */
public class RefreshFeed extends BaseHttpFeedAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
   
        HttpFeedResource feed = getFeed(data);
        String content = httpFeedService.getContent(feed.getUrl());
        httpFeedService.refreshFeed(feed, content, subject);
        if(content != null)
        {
            templatingContext.put("result","refreshed_successfully");
        }
        else
        {
            templatingContext.put("result","refresh_failed");
        }
    }
}
