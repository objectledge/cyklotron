package net.cyklotron.cms.search.searching;

import java.util.Date;

import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.ProtectedResource;

/**
 * This is a filter for filtering search results upon their visibility based on branches permission
 * assignments.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HitsProtectedFilter.java,v 1.6 2005-06-15 12:37:24 zwierzem Exp $
 */
public class HitsProtectedFilter<T extends SearchHit> extends BaseHitsFilter<T>
{
    private Subject subject;
    private Date date;

    public HitsProtectedFilter(Subject subject, Date date, CoralSession coralSession)
    {
        super(coralSession);
        this.subject = subject;
        this.date = date;
    }

    public boolean checkAccess(Resource branch)
    {
        if(branch instanceof ProtectedResource)
        {
            return ((ProtectedResource)branch).canView(coralSession, subject, date);
        }
        else
        {
            return true;
        }
    }
}
