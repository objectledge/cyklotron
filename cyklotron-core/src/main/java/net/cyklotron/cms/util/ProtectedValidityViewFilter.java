package net.cyklotron.cms.util;

import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.table.TableFilter;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.ProtectedResource;

/**
 * This is a filter for filtering navigations, it uses permission, workflow state and validity
 * checking.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ProtectedValidityViewFilter.java,v 1.6 2007-11-18 21:23:14 rafal Exp $
 */
public class ProtectedValidityViewFilter<T extends ProtectedResource> implements TableFilter<T>
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

    public boolean accept(ProtectedResource protectedRes)
    {
        return protectedRes.canView(coralSession, data, subject);
    }
}
