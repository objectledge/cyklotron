package net.cyklotron.cms.util;

import java.util.Date;

import net.labeo.services.table.TableFilter;

import net.cyklotron.cms.ProtectedResource;

/**
 * This is a filter for filtering resources upon their validity period and a given date.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ProtectedValidityFilter.java,v 1.1 2005-01-12 20:44:32 pablo Exp $
 */
public class ProtectedValidityFilter implements TableFilter
{
    private Date filterDate;

    public ProtectedValidityFilter(Date filterDate)
    {
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
