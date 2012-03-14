package net.cyklotron.cms.structure.table;

import java.util.Comparator;

import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * A copmparator used for sorting navigation nodes according to their sequence
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: SequenceComparator.java,v 1.3 2005-04-11 10:34:23 rafal Exp $
 */
public class SequenceComparator<T extends NavigationNodeResource> implements Comparator<T>
{
    public int compare(T n1, T n2)
    {
        int r = n1.getSequence(-1) - n2.getSequence(-1);
        // same sequence value? - strive for stable ordering anyway
        if(r == 0)
        {
            r = (int)(n1.getId() - n2.getId());
        }
        return r;
    }
}
