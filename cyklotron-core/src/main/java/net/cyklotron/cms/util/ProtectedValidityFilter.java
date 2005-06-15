package net.cyklotron.cms.util;

import java.util.Date;

import org.objectledge.context.Context;
import org.objectledge.table.TableFilter;

import net.cyklotron.cms.ProtectedResource;

/**
 * This is a filter for filtering resources upon their validity period and a given date.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ProtectedValidityFilter.java,v 1.5 2005-06-15 11:46:50 zwierzem Exp $
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
        
        return protectedRes.isValid(filterDate);
    }
}
