package net.cyklotron.cms.httpfeed;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;

/**
 * Utility functions for http feed application.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HttpFeedUtil.java,v 1.3 2005-01-18 17:38:25 pablo Exp $
 */
public class HttpFeedUtil
{
    public static HttpFeedResource getFeed(CoralSession resourceService, RunData data)
    throws ProcessingException
    {
        long feed_id = data.getParameters().get("feed_id").asLong(-1);
        if(feed_id == -1)
        {
            throw new ProcessingException("the parameter feed_id is not defined");
        }

        try
        {
            return HttpFeedResourceImpl.getHttpFeedResource(resourceService, feed_id);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("http feed resource does not exist", e);
        }
    }
}
