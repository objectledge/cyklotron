package net.cyklotron.cms.search.searching;

import java.util.Date;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;

import net.cyklotron.cms.ProtectedResource;

/**
 * This is a filter for filtering search results upon their visibility based on branches permission
 * assignments.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HitsProtectedFilter.java,v 1.1 2005-01-12 20:44:40 pablo Exp $
 */
public class HitsProtectedFilter extends BaseHitsFilter
{
    private Subject subject;
    private Date date;

    public HitsProtectedFilter(Subject subject, Date date, ResourceService resourceService)
    {
        super(resourceService);
        this.subject = subject;
        this.date = date;
    }

    public boolean checkAccess(Resource branch)
    {
        if(branch instanceof ProtectedResource)
        {
            return ((ProtectedResource)branch).canView(subject, date);
        }
        else
        {
            return true;
        }
    }
}
