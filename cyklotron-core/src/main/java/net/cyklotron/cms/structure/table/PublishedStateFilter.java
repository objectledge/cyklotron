package net.cyklotron.cms.structure.table;

import org.objectledge.table.TableFilter;

import net.cyklotron.cms.documents.DocumentNodeResource;

/**
 * This is a filter for filtering navigation nodes upon view permission.
 * 
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ViewPermissionFilter.java,v 1.6 2007-11-18 21:23:03 rafal Exp $
 */
public class PublishedStateFilter
    implements TableFilter
{
    private final static String PUBLISHED_STATE_NAME = "published";

    public boolean accept(Object object)
    {
        if(!(object instanceof DocumentNodeResource))
        {
            return false;
        }

        DocumentNodeResource node = (DocumentNodeResource)object;

        return PUBLISHED_STATE_NAME.equals(node.getState().getName());
    }
}
