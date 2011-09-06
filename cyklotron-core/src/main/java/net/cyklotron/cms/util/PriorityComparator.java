package net.cyklotron.cms.util;

import java.util.Comparator;

import net.cyklotron.cms.PrioritizedResource;

/**
 * This is a comparator for comparing prioritized resource.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: PriorityComparator.java,v 1.3 2005-04-15 04:34:27 pablo Exp $
 */
public class PriorityComparator<T extends PrioritizedResource>
    implements Comparator<T>
{
    public int compare(PrioritizedResource o1, PrioritizedResource o2)
    {
        try
        {
            int r = o1.getPriority(0) - o2.getPriority(0);
            // same priority value? - strive for stable ordering anyway
            if(r == 0)
            {
                r = (int)(o1.getId() - o2.getId());
            }
            return r;
        }
        catch(Exception e)
        {
            return (int)(o1.getId() - o2.getId());
        }
    }
}
