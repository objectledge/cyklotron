package net.cyklotron.cms.util;

import net.labeo.services.resource.Subject;
import net.labeo.services.table.TableFilter;

import net.cyklotron.cms.ProtectedResource;


/**
 * This is a filter for filtering cms protected resources upon view permission.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@ngo.pl">Pawe³ Potempski</a>
 * @version $Id: ProtectedViewFilter.java,v 1.1 2005-01-12 20:44:32 pablo Exp $
 */
public class ProtectedViewFilter implements TableFilter
{
    private Subject subject;

    public ProtectedViewFilter(Subject subject)
    {
        this.subject = subject;
    }

    public boolean accept(Object object)
    {
        if(!(object instanceof ProtectedResource))
        {
            return true;
        }
        ProtectedResource protectedRes = (ProtectedResource)object;

        return protectedRes.canView(subject);
    }
}
