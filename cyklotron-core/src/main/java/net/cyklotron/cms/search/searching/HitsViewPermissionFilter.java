package net.cyklotron.cms.search.searching;

import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.ProtectedResource;

/**
 * This is a filter for filtering search hits upon view permission.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HitsViewPermissionFilter.java,v 1.5 2005-02-09 22:20:47 rafal Exp $
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
            return ((ProtectedResource)branch).canView(context, subject);
        }
        else
        {
            return true;
        }
    }
}
