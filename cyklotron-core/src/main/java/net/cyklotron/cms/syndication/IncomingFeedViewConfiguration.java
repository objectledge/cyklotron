package net.cyklotron.cms.syndication;

import org.objectledge.parameters.Parameters;

/**
 * Provides default parameter values for feed view configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IncomingFeedViewConfiguration.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class IncomingFeedViewConfiguration
{
    private String feedName;

    public IncomingFeedViewConfiguration(Parameters componentConfig)
    {
        feedName = componentConfig.get("feedName", "");
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
