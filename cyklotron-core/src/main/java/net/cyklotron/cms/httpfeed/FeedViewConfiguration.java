package net.cyklotron.cms.httpfeed;

import net.labeo.util.configuration.Configuration;

/**
 * Provides default parameter values for feed view configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: FeedViewConfiguration.java,v 1.1 2005-01-12 20:45:02 pablo Exp $
 */
public class FeedViewConfiguration
{
    private String feedName;

    public FeedViewConfiguration(Configuration componentConfig)
    {
        feedName = componentConfig.get("feedName").asString("");
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
