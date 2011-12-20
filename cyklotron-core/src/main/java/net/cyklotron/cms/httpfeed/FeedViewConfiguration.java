package net.cyklotron.cms.httpfeed;

import org.objectledge.parameters.Parameters;

/**
 * Provides default parameter values for feed view configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: FeedViewConfiguration.java,v 1.2 2005-01-20 05:45:23 pablo Exp $
 */
public class FeedViewConfiguration
{
    private String feedName;

    public FeedViewConfiguration(Parameters componentConfig)
    {
        feedName = componentConfig.get("feedName","");
    }

    /** Getter for property feedName.
     * @return Value of property feedName.
     *
     */
    public String getFeedName()
    {
        return feedName;
    }    
}
