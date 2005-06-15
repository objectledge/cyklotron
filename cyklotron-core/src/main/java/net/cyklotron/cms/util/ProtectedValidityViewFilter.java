package net.cyklotron.cms.util;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.ProtectedResource;

import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.table.TableFilter;

/**
 * This is a filter for filtering navigations, it uses permission, workflow state and validity
 * checking.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ProtectedValidityViewFilter.java,v 1.5 2005-06-15 12:37:30 zwierzem Exp $
 */
public class ProtectedValidityViewFilter implements TableFilter
{
    private CmsData data;
    
    private Subject subject;

    private CoralSession coralSession;
    
    public ProtectedValidityViewFilter(CoralSession coralSession, CmsData data, Subject subject)
    {
        this.coralSession = coralSession;
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
        return protectedRes.canView(coralSession, data, subject);
    }
}
