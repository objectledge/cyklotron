package net.cyklotron.cms.search.searching;

import net.cyklotron.cms.ProtectedResource;

import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.store.Resource;

/**
 * This is a filter for filtering search hits upon view permission.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HitsViewPermissionFilter.java,v 1.3 2005-01-19 08:22:56 pablo Exp $
 */
public class HitsViewPermissionFilter extends BaseHitsFilter
{
    private Subject subject;

    public HitsViewPermissionFilter(Subject subject, Context context)
    {
        super(context);
        this.subject = subject;
    }

    public boolean checkAccess(Resource branch)
    {
        if(branch instanceof ProtectedResource)
        {
            return ((ProtectedResource)branch).canView(subject);
        }
        else
        {
            return true;
        }
    }
}
