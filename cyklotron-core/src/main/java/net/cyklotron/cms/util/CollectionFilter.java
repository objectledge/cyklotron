package net.cyklotron.cms.util;

import java.util.Collection;
import java.util.Iterator;

import org.objectledge.table.TableFilter;

/**
 * Utility class for filtering java.util collections.
 * 
 * @author RafaĹ Krzewski
 * @version $Id: CollectionFilter.java,v 1.1 2005-05-30 00:42:27 pablo Exp $
 */
public class CollectionFilter
{
    /**
     * Private ctor to ensure non-instantability.
     */
    private CollectionFilter()
    {
        // non instantiantable
    }

    /**
     * Applies a filter to a collection.
     * 
     * All items that are not accepted by the filter will be removed from the 
     * collection.
     * 
     * @param collection the collection to be filtered.
     * @param filter the filter to apply.
     */
    public static void apply(Collection collection, TableFilter filter)
    {
        Iterator i = collection.iterator();
        while(i.hasNext())
        {
            Object o = i.next();
            if(!filter.accept(o))
            {
                i.remove();
            }
        }
    }
}
