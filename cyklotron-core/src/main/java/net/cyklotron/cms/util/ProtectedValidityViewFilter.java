package net.cyklotron.cms.util;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.ProtectedResource;

import org.objectledge.coral.security.Subject;
import org.objectledge.table.TableFilter;

/**
 * This is a filter for filtering navigations, it uses permission, workflow state and validity
 * checking.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ProtectedValidityViewFilter.java,v 1.2 2005-01-19 08:24:15 pablo Exp $
 */
public class ProtectedValidityViewFilter implements TableFilter
{
    private CmsData data;
    
    private Subject subject;
    
    public ProtectedValidityViewFilter(CmsData data, Subject subject)
    {
        this.data = data;
        this.subject = subject;
    }

    public boolean accept(Object object)
    {
        if(!(object instanceof ProtectedResource))
        {
            return true;
        }
        ProtectedResource protectedRes = (ProtectedResource)object;
        return protectedRes.canView(data, subject);
    }
}
