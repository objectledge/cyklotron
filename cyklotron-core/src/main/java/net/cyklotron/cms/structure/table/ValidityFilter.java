package net.cyklotron.cms.structure.table;

import java.util.Date;

import org.objectledge.context.Context;
import org.objectledge.table.TableFilter;

import net.cyklotron.cms.structure.NavigationNodeResource;


/**
 * This is a filter for filtering resources upon their validity period and a given date.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ValidityFilter.java,v 1.5 2005-02-09 22:21:01 rafal Exp $
 */
public class ValidityFilter
    implements TableFilter
{
    private Date filterDate;
    
    private Context context;

    public ValidityFilter(Context context, Date filterDate)
    {
        this.context = context;
        this.filterDate = filterDate;
    }

    public boolean accept(Object object)
    {
        if(!(object instanceof NavigationNodeResource))
        {
            return false;
        }

        NavigationNodeResource node = (NavigationNodeResource)object;
        return node.isValid(context, filterDate);
    }
}
