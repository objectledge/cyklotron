package net.cyklotron.cms.syndication;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

/**
 * Utility functions for incoming feeds sub-application.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IncomingFeedUtil.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class IncomingFeedUtil
{
    public static final String FEED_ID_PARAM = "feedId";
    
    public static IncomingFeedResource getFeed(CoralSession coralSession, Parameters parameters)
    throws ProcessingException
    {
        long feed_id = parameters.getLong(FEED_ID_PARAM, -1);
        if(feed_id == -1)
        {
            throw new ProcessingException("the parameter feedId is not defined");
        }

        try
        {
            return IncomingFeedResourceImpl.getIncomingFeedResource(coralSession, feed_id);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("incoming feed resource does not exist", e);
        }
    }
}
