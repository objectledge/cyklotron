package net.cyklotron.cms.util;

import net.cyklotron.cms.ProtectedResource;

import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.table.TableFilter;


/**
 * This is a filter for filtering cms protected resources upon view permission.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@ngo.pl">Pawe� Potempski</a>
 * @version $Id: ProtectedViewFilter.java,v 1.3 2005-01-19 13:46:39 pablo Exp $
 */
public class ProtectedViewFilter implements TableFilter
{
    private Subject subject;

    private Context context;
    
    public ProtectedViewFilter(Context context, Subject subject)
    {
        this.context = context;
        this.subject = subject;
    }

    public boolean accept(Object object)
    {
        if(!(object instanceof ProtectedResource))
        {
            return true;
        }
        ProtectedResource protectedRes = (ProtectedResource)object;

        return protectedRes.canView(context, subject);
    }
}
