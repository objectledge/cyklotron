package net.cyklotron.cms.structure.table;

import net.cyklotron.cms.structure.NavigationNodeResource;
import net.labeo.services.resource.Subject;
import net.labeo.services.table.TableFilter;


/**
 * This is a filter for filtering navigation nodes upon view permission.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ViewPermissionFilter.java,v 1.1 2005-01-12 20:44:55 pablo Exp $
 */
public class ViewPermissionFilter implements TableFilter
{
    private Subject subject;

    public ViewPermissionFilter(Subject subject)
    {
        this.subject = subject;
    }

    public boolean accept(Object object)
    {
        if(!(object instanceof NavigationNodeResource))
        {
            return false;
        }

        NavigationNodeResource node = (NavigationNodeResource)object;

        return node.canView(subject);
    }
}
