package net.cyklotron.cms.search.searching;

import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.ProtectedResource;

/**
 * This is a filter for filtering search hits upon view permission.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HitsViewPermissionFilter.java,v 1.6 2005-06-15 12:37:25 zwierzem Exp $
 */
public class HitsViewPermissionFilter<T extends SearchHit> extends BaseHitsFilter<T>
{
    private Subject subject;

    public HitsViewPermissionFilter(Subject subject, CoralSession coralSession)
    {
        super(coralSession);
        this.subject = subject;
    }

    public boolean checkAccess(Resource branch)
    {
        if(branch instanceof ProtectedResource)
        {
            return ((ProtectedResource)branch).canView(coralSession, subject);
        }
        else
        {
            return true;
        }
    }
}
