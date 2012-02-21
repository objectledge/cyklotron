package net.cyklotron.cms.syndication;

import org.objectledge.parameters.Parameters;

/**
 * Provides default parameter values for feed list configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: OutgoingFeedListConfiguration.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class OutgoingFeedListConfiguration
{
    private String sortColumn;
    private boolean ascSortDir;

    public OutgoingFeedListConfiguration(Parameters componentConfig)
    {
        sortColumn = componentConfig.get("feedSortColumn", "name");
        if(componentConfig.isDefined("feedSortDir")
            && componentConfig.get("feedSortDir").length() == 1)
        {
            ascSortDir = componentConfig.getInt("feedSortDir",0) == 0;
        }
        else
        {
            ascSortDir = componentConfig.getBoolean("feedSortDir",true);
        }
    }

    public String getSortColumn()
    {
        return sortColumn;
    }

    public boolean getAscSortDir()
    {
        return ascSortDir;
    }
}
