package net.cyklotron.cms.util;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.ProtectedResource;

import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.table.TableFilter;

/**
 * This is a filter for filtering navigations, it uses permission, workflow state and validity
 * checking.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ProtectedValidityViewFilter.java,v 1.3 2005-01-19 13:46:39 pablo Exp $
 */
public class ProtectedValidityViewFilter implements TableFilter
{
    private CmsData data;
    
    private Subject subject;

    private Context context;
    
    public ProtectedValidityViewFilter(Context context, CmsData data, Subject subject)
    {
        this.context = context;
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
        return protectedRes.canView(context, data, subject);
    }
}
