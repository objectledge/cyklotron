package net.cyklotron.cms.structure.table;

import java.util.Date;

import net.cyklotron.cms.structure.NavigationNodeResource;

import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.filter.TimeFilter;


/**
 * This is a filter for filtering resources upon their validity end time.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ValidityEndFilter.java,v 1.2 2005-01-19 08:23:58 pablo Exp $
 */
public class ValidityEndFilter
    extends TimeFilter
{
    public ValidityEndFilter(Date start, Date end)
    {
        super(start, end);
    }

    protected Date getDate(Resource r)
    {
        return ((NavigationNodeResource)r).getValidityEnd();
    }
}
