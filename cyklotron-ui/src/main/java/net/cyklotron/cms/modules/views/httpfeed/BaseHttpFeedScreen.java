package net.cyklotron.cms.modules.views.httpfeed;

import net.cyklotron.cms.httpfeed.HttpFeedResource;
import net.cyklotron.cms.httpfeed.HttpFeedService;
import net.cyklotron.cms.httpfeed.HttpFeedUtil;
import net.cyklotron.cms.modules.views.BaseCMSScreen;

import org.objectledge.pipeline.ProcessingException;

/**
 * Http feed application base screen.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseHttpFeedScreen.java,v 1.1 2005-01-24 04:34:18 pablo Exp $
 */
public class BaseHttpFeedScreen extends BaseCMSScreen implements Secure
{
    /** logging facility */
    protected Logger log;

    /** http feed service */
    protected HttpFeedService httpFeedService;

    public BaseHttpFeedScreen()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME))
            .getFacility(HttpFeedService.LOGGING_FACILITY);
        httpFeedService = (HttpFeedService)broker.getService(HttpFeedService.SERVICE_NAME);
    }

    public HttpFeedResource getFeed(RunData data)
    throws ProcessingException
    {
        return HttpFeedUtil.getFeed(coralSession, data);
    }
}
