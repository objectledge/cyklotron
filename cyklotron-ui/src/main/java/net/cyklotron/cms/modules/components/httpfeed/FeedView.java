package net.cyklotron.cms.modules.components.httpfeed;

import net.labeo.Labeo;
import net.labeo.services.ServiceBroker;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.httpfeed.HttpFeedException;
import net.cyklotron.cms.httpfeed.HttpFeedResource;
import net.cyklotron.cms.httpfeed.HttpFeedService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;

/**
 * FeedView component displays http feed contents.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: FeedView.java,v 1.1 2005-01-24 04:35:31 pablo Exp $
 */

public class FeedView extends SkinableCMSComponent
{
    /** The httpfeed service. */
    private HttpFeedService httpFeedService;

    public FeedView()
    {
        ServiceBroker broker = Labeo.getBroker();
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(HttpFeedService.LOGGING_FACILITY);
        httpFeedService = (HttpFeedService)broker.getService(HttpFeedService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        if(getSite(context) == null)
        {
            componentError(context, "No site selected");
            return;
        }

        try
        {
            Parameters componentConfig = getConfiguration();
            Resource parent = httpFeedService.getFeedsParent(getSite(context));

            String name = componentConfig.get("feedName",null);
            if(name == null)
            {
                componentError(context, "Http feed name is not configured");
                return;
            }

            Resource[] res = coralSession.getStore().getResource(parent, name);
            if(res.length == 1)
            {
                HttpFeedResource feed = (HttpFeedResource)res[0];
                templatingContext.put("feed",feed);
            }
            else if(res.length == 0)
            {
                componentError(context, "Cannot find a http feed with name='"+name+"'");
                return;
            }
            else
            {
                componentError(context, "Multiple http feeds with name='"+name+"'");
                return;
            }
        }
        catch(HttpFeedException e)
        {
            componentError(context, "Cannot get http feed root", e);
            return;
        }
    }
}
