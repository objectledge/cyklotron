package net.cyklotron.cms.structure.table;

import java.util.Date;

import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.filter.TimeFilter;

import net.cyklotron.cms.structure.NavigationNodeResource;


/**
 * This is a filter for filtering resources upon their validity start time.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ValidityStartFilter.java,v 1.3 2005-02-09 22:21:01 rafal Exp $
 */
public class ValidityStartFilter
    extends TimeFilter
{
    public ValidityStartFilter(Date start, Date end)
    {
        super(start, end);
    }

    protected Date getDate(Resource r)
    {
        return ((NavigationNodeResource)r).getValidityStart();
    }
}
