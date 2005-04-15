package net.cyklotron.cms.util;

import java.util.Comparator;

import net.cyklotron.cms.PrioritizedResource;

/**
 * This is a comparator for comparing prioritized resource.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: PriorityComparator.java,v 1.3 2005-04-15 04:34:27 pablo Exp $
 */
public class PriorityComparator
    implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        if(!((o1 instanceof PrioritizedResource && o2 instanceof PrioritizedResource )))
        {
            return 0;
        }
        PrioritizedResource r1 = (PrioritizedResource)o1;
        PrioritizedResource r2 = (PrioritizedResource)o2;
        try
        {
            int r = r1.getPriority(0) - r2.getPriority(0);
            // same priority value? - strive for stable ordering anyway
            if(r == 0)
            {
                r = (int)(r1.getId() - r2.getId());
            }
            return r;
        }
        catch(Exception e)
        {
            return (int)(r1.getId() - r2.getId());
        }
    }
}
