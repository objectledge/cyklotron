package net.cyklotron.cms.search.searching;

import java.util.Date;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.resource.Subject;

import net.cyklotron.cms.ProtectedResource;

/**
 * This is a filter for filtering search results upon their visibility based on branches permission
 * assignments.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HitsProtectedFilter.java,v 1.2 2005-01-18 17:38:19 pablo Exp $
 */
public class HitsProtectedFilter extends BaseHitsFilter
{
    private Subject subject;
    private Date date;

    public HitsProtectedFilter(Subject subject, Date date, CoralSession resourceService)
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
