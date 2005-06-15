package net.cyklotron.cms.util;

import net.cyklotron.cms.ProtectedResource;

import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.table.TableFilter;


/**
 * This is a filter for filtering cms protected resources upon view permission.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@ngo.pl">Pawe� Potempski</a>
 * @version $Id: ProtectedViewFilter.java,v 1.5 2005-06-15 12:37:30 zwierzem Exp $
 */
public class ProtectedViewFilter implements TableFilter
{
    private Subject subject;

    private CoralSession coralSession;
    
    public ProtectedViewFilter(CoralSession coralSession, Subject subject)
    {
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

        return protectedRes.canView(coralSession, subject);
    }
}
