package net.cyklotron.cms.structure.table;

import java.util.Date;

import net.cyklotron.cms.structure.NavigationNodeResource;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.TimeComparator;

/**
 * This comparator compares validity end times of navigation resources.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ValidityEndComparator.java,v 1.1 2005-01-12 20:44:55 pablo Exp $
 */
public class ValidityEndComparator
    extends TimeComparator
{
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
