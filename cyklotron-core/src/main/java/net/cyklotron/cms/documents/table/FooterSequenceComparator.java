package net.cyklotron.cms.documents.table;

import java.util.Comparator;

import net.cyklotron.cms.documents.FooterResource;

/**
 * A copmparator used for sorting footers according to their sequence
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: FooterSequenceComparator.java,v 1.1 2006-05-08 12:29:07 pablo Exp $
 */
public class FooterSequenceComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        if(!((o1 instanceof FooterResource && o2 instanceof FooterResource )))
        {
            return 0;
        }
        FooterResource n1 = (FooterResource) o1;
        FooterResource n2 = (FooterResource) o2;
        int r = n1.getSequence(-1) - n2.getSequence(-1);
        if(r == 0)
        {
            r = (int)(n1.getId() - n2.getId());
        }
        return r;
    }
}
