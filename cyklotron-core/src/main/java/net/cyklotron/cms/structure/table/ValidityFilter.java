package net.cyklotron.cms.structure.table;

import java.util.Date;

import net.cyklotron.cms.structure.NavigationNodeResource;

import org.objectledge.table.TableFilter;


/**
 * This is a filter for filtering resources upon their validity period and a given date.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ValidityFilter.java,v 1.3 2005-01-19 08:23:58 pablo Exp $
 */
public class ValidityFilter
    implements TableFilter
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
