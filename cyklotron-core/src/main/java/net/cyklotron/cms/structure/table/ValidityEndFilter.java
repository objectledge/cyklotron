package net.cyklotron.cms.structure.table;

import java.util.Date;
import net.cyklotron.cms.structure.NavigationNodeResource;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.TimeFilter;


/**
 * This is a filter for filtering resources upon their validity end time.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ValidityEndFilter.java,v 1.1 2005-01-12 20:44:55 pablo Exp $
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
