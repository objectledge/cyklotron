package net.cyklotron.cms.structure.table;

import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.table.TableFilter;

import net.cyklotron.cms.structure.NavigationNodeResource;


/**
 * This is a filter for filtering navigation nodes upon view permission.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ViewPermissionFilter.java,v 1.4 2005-02-09 22:21:01 rafal Exp $
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
