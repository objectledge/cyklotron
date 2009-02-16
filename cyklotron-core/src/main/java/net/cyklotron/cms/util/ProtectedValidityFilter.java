package net.cyklotron.cms.util;

import java.util.Date;

import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.table.TableFilter;

import net.cyklotron.cms.ProtectedResource;

/**
 * This is a filter for filtering resources upon their validity period and a given date.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ProtectedValidityFilter.java,v 1.6 2006-02-28 15:29:28 pablo Exp $
 */
public class ProtectedValidityFilter implements TableFilter
{
    private Date filterDate;
    
    private CoralSession coralSession;
    
    private Subject subject;

    public ProtectedValidityFilter(CoralSession coralSession, Subject subject, Date filterDate)
    {
        this.filterDate = filterDate;
        this.coralSession = coralSession;
        this.subject = subject;
    }

    public boolean accept(Object object)
    {
        if(!(object instanceof ProtectedResource))
        {
            return true;
        }
        ProtectedResource protectedRes = (ProtectedResource)object;
        return protectedRes.canView(coralSession, subject, filterDate);
    }
}
