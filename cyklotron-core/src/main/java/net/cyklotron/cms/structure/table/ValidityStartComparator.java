package net.cyklotron.cms.structure.table;

import java.util.Date;

import org.objectledge.coral.table.comparator.TimeComparator;

import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * This comparator compares validity start times of navigation resources.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ValidityStartComparator.java,v 1.3 2005-02-09 22:21:01 rafal Exp $
 */
public class ValidityStartComparator extends TimeComparator<NavigationNodeResource>
{
    public ValidityStartComparator(TimeComparator.SortNulls strategy)
    {
        super(strategy);
    }

    @Override
    protected Date getSortCriterionDate(NavigationNodeResource resource)
    {
        return resource.getValidityStart();
    }    
}
