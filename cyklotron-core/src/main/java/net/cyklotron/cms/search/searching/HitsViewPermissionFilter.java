package net.cyklotron.cms.search.searching;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;

import net.cyklotron.cms.ProtectedResource;

/**
 * This is a filter for filtering search hits upon view permission.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HitsViewPermissionFilter.java,v 1.1 2005-01-12 20:44:40 pablo Exp $
 */
public class HitsViewPermissionFilter extends BaseHitsFilter
{
    private Subject subject;

    public HitsViewPermissionFilter(Subject subject, ResourceService resourceService)
    {
        super(resourceService);
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
