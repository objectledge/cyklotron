package net.cyklotron.cms.documents.table;

import java.util.Date;

import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.TimeComparator;

import net.cyklotron.cms.documents.DocumentNodeResource;

/**
 * This comparator compares event end times of document resources.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EventEndComparator.java,v 1.3 2005-02-09 22:20:35 rafal Exp $
 */
public class EventEndComparator extends TimeComparator<DocumentNodeResource>
{
    public EventEndComparator(TimeComparator.Nulls strategy)
    {
        super(strategy);
    }
    
    protected Date getSortCriterionDate(DocumentNodeResource r)
    {
        return r.getEventEnd();
    }
}
