package net.cyklotron.cms.util;

import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.table.TableFilter;

import net.cyklotron.cms.ProtectedResource;


/**
 * This is a filter for filtering cms protected resources upon view permission.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@ngo.pl">Pawe� Potempski</a>
 * @version $Id: ProtectedViewFilter.java,v 1.4 2005-02-09 22:20:08 rafal Exp $
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
