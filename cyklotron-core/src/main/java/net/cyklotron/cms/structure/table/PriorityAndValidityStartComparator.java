package net.cyklotron.cms.structure.table;

import java.util.Date;

import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.TimeComparator;

import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * This comparator compares priority and validity start times of navigation resources.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: PriorityAndValidityStartComparator.java,v 1.3 2005-02-09 22:21:01 rafal Exp $
 */
public class PriorityAndValidityStartComparator extends TimeComparator
{
    protected Date getDate(Resource r)
    {
        return ((NavigationNodeResource)r).getValidityStart();
    }

    public int compare(Object o1, Object o2)
    {
    	if (!((o1 instanceof NavigationNodeResource && o2 instanceof NavigationNodeResource)))
        {
            return 0;
        }
	    NavigationNodeResource r1 = (NavigationNodeResource)o1;
        NavigationNodeResource r2 = (NavigationNodeResource)o2;
		int diff = r1.getPriority(0) - r2.getPriority(0);
        if(diff != 0)
        {
        	return diff;
        }
        return compareDates(r1.getValidityStart(), r2.getValidityStart());
    }
}
