package net.cyklotron.cms.structure.table;

import net.cyklotron.cms.structure.NavigationNodeResource;

import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.table.TableFilter;


/**
 * This is a filter for filtering navigation nodes upon view permission.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ViewPermissionFilter.java,v 1.3 2005-01-19 13:46:42 pablo Exp $
 */
public class ViewPermissionFilter implements TableFilter
{
    private Subject subject;
    
    private Context context;

    public ViewPermissionFilter(Context context, Subject subject)
    {
        this.context = context;
        this.subject = subject;
    }

    public boolean accept(Object object)
    {
        if(!(object instanceof NavigationNodeResource))
        {
            return false;
        }

        NavigationNodeResource node = (NavigationNodeResource)object;

        return node.canView(context, subject);
    }
}
