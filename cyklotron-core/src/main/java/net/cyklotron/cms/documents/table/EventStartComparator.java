package net.cyklotron.cms.documents.table;

import java.util.Date;

import net.cyklotron.cms.documents.DocumentNodeResource;

import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.TimeComparator;

/**
 * This comparator compares event start times of document resources.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EventStartComparator.java,v 1.2 2005-01-19 08:23:11 pablo Exp $
 */
public class EventStartComparator extends TimeComparator
{
    protected Date getDate(Resource r)
    {
        return ((DocumentNodeResource)r).getEventStart();
    }

    public int compare(Object o1, Object o2)
    {
        if (!((o1 instanceof DocumentNodeResource && o2 instanceof DocumentNodeResource)))
        {
            return 0;
        }
        DocumentNodeResource r1 = (DocumentNodeResource)o1;
        DocumentNodeResource r2 = (DocumentNodeResource)o2;
        return compareDates(r1.getEventStart(), r2.getEventStart());
    }
}
