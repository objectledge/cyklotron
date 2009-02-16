package net.cyklotron.cms.poll.util;

import org.objectledge.table.comparator.TimeComparator;

import net.cyklotron.cms.poll.PollResource;

/**
 * @author pablo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PublicationTimePollComparator extends TimeComparator 
{
    public int compare(Object o1, Object o2)
    {
        if (!((o1 instanceof PollResource && o2 instanceof PollResource)))
        {
            return 0;
        }
        PollResource r1 = (PollResource)o1;
        PollResource r2 = (PollResource)o2;
        return compareDates(r1.getStartDate(), r2.getStartDate());
    }
}
