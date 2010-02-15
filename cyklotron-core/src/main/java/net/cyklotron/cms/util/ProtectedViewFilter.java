package net.cyklotron.cms.util;

import java.util.Date;

import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.table.TableFilter;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.ProtectedResource;

/**
 * This is a filter for filtering cms protected resources upon view permission.
 * 
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@ngo.pl">Pawe� Potempski</a>
 * @version $Id: ProtectedViewFilter.java,v 1.7 2008-06-05 17:07:49 rafal Exp $
 */
public class ProtectedViewFilter
    implements TableFilter<Resource>
{
    private Subject subject;

    private CoralSession coralSession;

    private CmsData cmsData;

    private Date date;

    public ProtectedViewFilter(CoralSession coralSession, Subject subject)
    {
        this.coralSession = coralSession;
        this.subject = subject;
    }
    
    public ProtectedViewFilter(CoralSession coralSession, Subject subject, Date date)
    {
        this.coralSession = coralSession;
        this.subject = subject;
        this.date = date;
    }
    
    public ProtectedViewFilter(CoralSession coralSession, Subject subject, CmsData cmsData)
    {
        this.coralSession = coralSession;
        this.subject = subject;
        this.cmsData = cmsData;
    }

    public boolean accept(Resource object)
    {
        if(!(object instanceof ProtectedResource))
        {
            return true;
        }
        ProtectedResource protectedRes = (ProtectedResource)object;

        if(cmsData != null)
        {
            return protectedRes.canView(coralSession, cmsData, subject);
        }
        else if(date != null)
        {
            return protectedRes.canView(coralSession, subject, date);
        }
        else
        {
            return protectedRes.canView(coralSession, subject);
        }
    }
}
