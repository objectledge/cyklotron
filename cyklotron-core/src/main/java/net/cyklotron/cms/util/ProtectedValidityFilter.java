package net.cyklotron.cms.util;

import java.util.Date;

import net.cyklotron.cms.ProtectedResource;

import org.objectledge.context.Context;
import org.objectledge.table.TableFilter;

/**
 * This is a filter for filtering resources upon their validity period and a given date.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ProtectedValidityFilter.java,v 1.3 2005-01-19 13:46:39 pablo Exp $
 */
public class ProtectedValidityFilter implements TableFilter
{
    private Date filterDate;

    private Context context;
    
    public ProtectedValidityFilter(Context context, Date filterDate)
    {
        this.context = context;
        this.filterDate = filterDate;
    }

    public boolean accept(Object object)
    {
        if(!(object instanceof ProtectedResource))
        {
            return true;
        }
        ProtectedResource protectedRes = (ProtectedResource)object;
        
        return protectedRes.isValid(context, filterDate);
    }
}
