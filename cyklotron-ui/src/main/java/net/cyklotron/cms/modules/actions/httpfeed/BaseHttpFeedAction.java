package net.cyklotron.cms.modules.actions.httpfeed;

import net.cyklotron.cms.httpfeed.HttpFeedResource;
import net.cyklotron.cms.httpfeed.HttpFeedService;
import net.cyklotron.cms.httpfeed.HttpFeedUtil;
import net.cyklotron.cms.modules.actions.BaseCMSAction;

import org.objectledge.pipeline.ProcessingException;

/**
 * Http feed application base action.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseHttpFeedAction.java,v 1.1 2005-01-24 04:35:09 pablo Exp $
 */
public abstract class BaseHttpFeedAction extends BaseCMSAction implements Secure
{
    /** logging facility */
    protected Logger log;

    /** http feed service */
    protected HttpFeedService httpFeedService;

    public BaseHttpFeedAction()
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


