package net.cyklotron.cms.structure.table;

import java.util.Date;

import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.TimeComparator;

import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * This comparator compares validity end times of navigation resources.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ValidityEndComparator.java,v 1.3 2005-02-09 22:21:01 rafal Exp $
 */
public class ValidityEndComparator
    extends TimeComparator
{
    public ValidityEndComparator(TimeComparator.SortNulls strategy)
    {
        super(strategy);
    }
    
    protected Date getDate(Resource r)
    {
        return ((NavigationNodeResource)r).getValidityEnd();
    }
    
	public int compare(Object o1, Object o2)
    {
		if(!((o1 instanceof NavigationNodeResource && o2 instanceof NavigationNodeResource )))
		{
			return 0;
		}
		NavigationNodeResource r1 = (NavigationNodeResource)o1;
		NavigationNodeResource r2 = (NavigationNodeResource)o2;
		return compareDates(r1.getValidityEnd(), r2.getValidityEnd());
    }
}
