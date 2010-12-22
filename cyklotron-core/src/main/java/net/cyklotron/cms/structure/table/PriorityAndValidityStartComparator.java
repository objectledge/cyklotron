package net.cyklotron.cms.structure.table;

import java.util.Date;

import org.objectledge.coral.table.comparator.TimeComparator;

import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * This comparator compares priority and validity start times of navigation resources.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: PriorityAndValidityStartComparator.java,v 1.4 2005-04-15 04:34:26 pablo Exp $
 */
public class PriorityAndValidityStartComparator extends TimeComparator<NavigationNodeResource>
{
    public PriorityAndValidityStartComparator(TimeComparator.SortNulls strategy)
    {
        super(strategy);
    }
    
    @Override
    protected Date getSortCriterionDate(NavigationNodeResource resource)
    {
        return resource.getValidityStart();
    }    

    public int compare(NavigationNodeResource r1, NavigationNodeResource r2)
    {
		int rel = r1.getPriority(0) - r2.getPriority(0);
        if(rel != 0)
        {
        	return rel;
        }
        else
        {
            return super.compare(r1, r2);
        }
    }
}
