package net.cyklotron.cms.structure.table;

import java.util.Date;
import net.cyklotron.cms.structure.NavigationNodeResource;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.TimeFilter;


/**
 * This is a filter for filtering resources upon their validity start time.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ValidityStartFilter.java,v 1.1 2005-01-12 20:44:55 pablo Exp $
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
