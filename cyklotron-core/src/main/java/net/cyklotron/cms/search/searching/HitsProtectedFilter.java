package net.cyklotron.cms.search.searching;

import java.util.Date;

import net.cyklotron.cms.ProtectedResource;

import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.store.Resource;

/**
 * This is a filter for filtering search results upon their visibility based on branches permission
 * assignments.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HitsProtectedFilter.java,v 1.4 2005-01-19 13:47:16 pablo Exp $
 */
public class HitsProtectedFilter extends BaseHitsFilter
{
    private Subject subject;
    private Date date;

    public HitsProtectedFilter(Subject subject, Date date, Context context)
    {
        super(context);
        this.subject = subject;
        this.date = date;
    }

    public boolean checkAccess(Resource branch)
    {
        if(branch instanceof ProtectedResource)
        {
            return ((ProtectedResource)branch).canView(context, subject, date);
        }
        else
        {
            return true;
        }
    }
}
