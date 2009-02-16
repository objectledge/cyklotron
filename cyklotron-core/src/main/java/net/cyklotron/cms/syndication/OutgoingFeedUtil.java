package net.cyklotron.cms.syndication;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

/**
 * Utility functions for outgoing feeds sub-application.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: OutgoingFeedUtil.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class OutgoingFeedUtil
{
    public static final String FEED_ID_PARAM = "feedId";

    public static OutgoingFeedResource getFeed(CoralSession coralSession, Parameters parameters)
    throws ProcessingException
    {
        long feed_id = parameters.getLong(OutgoingFeedUtil.FEED_ID_PARAM, -1);
        if(feed_id == -1)
        {
            throw new ProcessingException("the parameter feedId is not defined");
        }

        try
        {
            return OutgoingFeedResourceImpl.getOutgoingFeedResource(coralSession, feed_id);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("outgoing feed resource does not exist", e);
        }
    }
}
