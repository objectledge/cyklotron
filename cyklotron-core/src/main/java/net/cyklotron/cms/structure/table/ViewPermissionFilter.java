package net.cyklotron.cms.structure.table;

import net.cyklotron.cms.structure.NavigationNodeResource;

import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.table.TableFilter;


/**
 * This is a filter for filtering navigation nodes upon view permission.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ViewPermissionFilter.java,v 1.5 2005-06-15 12:37:26 zwierzem Exp $
 */
public class ViewPermissionFilter implements TableFilter
{
    private Subject subject;
    
    private CoralSession coralSession;

    public ViewPermissionFilter(CoralSession coralSession, Subject subject)
    {
        this.coralSession = coralSession;
        this.subject = subject;
    }

    public boolean accept(Object object)
    {
        if(!(object instanceof NavigationNodeResource))
        {
            return false;
        }

        NavigationNodeResource node = (NavigationNodeResource)object;

        return node.canView(coralSession, subject);
    }
}
