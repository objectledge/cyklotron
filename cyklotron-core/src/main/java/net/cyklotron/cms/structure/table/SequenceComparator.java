package net.cyklotron.cms.structure.table;

import java.util.Comparator;

import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * A copmparator used for sorting navigation nodes according to their sequence
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: SequenceComparator.java,v 1.2 2005-01-12 20:57:15 pablo Exp $
 */
public class SequenceComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        if(!((o1 instanceof NavigationNodeResource && o2 instanceof NavigationNodeResource )))
        {
            return 0;
        }
        NavigationNodeResource n1 = (NavigationNodeResource) o1;
        NavigationNodeResource n2 = (NavigationNodeResource) o2;
        return n1.getSequence(-1) - n2.getSequence(-1);
    }
}
