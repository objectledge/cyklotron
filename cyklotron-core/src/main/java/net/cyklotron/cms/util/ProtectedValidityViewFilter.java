package net.cyklotron.cms.util;

import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.table.TableFilter;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.ProtectedResource;

/**
 * This is a filter for filtering navigations, it uses permission, workflow state and validity
 * checking.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ProtectedValidityViewFilter.java,v 1.4 2005-02-09 22:20:08 rafal Exp $
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
