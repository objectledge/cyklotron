package net.cyklotron.cms.structure.table;

import java.util.Date;

import net.cyklotron.cms.structure.NavigationNodeResource;


/**
 * This is a filter for filtering resources upon their validity period and a given date.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ValidityFilter.java,v 1.2 2005-01-13 11:46:27 pablo Exp $
 */
public class ValidityFilter
    implements net.labeo.services.table.TableFilter
{
    private Date filterDate;

    public ValidityFilter(Date filterDate)
    {
        this.filterDate = filterDate;
    }

    public boolean accept(Object object)
    {
        if(!(object instanceof NavigationNodeResource))
        {
            return false;
        }

        NavigationNodeResource node = (NavigationNodeResource)object;

        return node.isValid(filterDate);
    }
}
