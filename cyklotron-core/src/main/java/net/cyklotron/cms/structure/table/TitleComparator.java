package net.cyklotron.cms.structure.table;

import java.util.Locale;
import net.cyklotron.cms.structure.NavigationNodeResource;

import net.labeo.services.resource.table.BaseStringComparator;

/**
 * This is a comparator for comparing navigation resource titles.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: TitleComparator.java,v 1.1 2005-01-12 20:44:55 pablo Exp $
 */
public class TitleComparator
    extends BaseStringComparator
{
    public TitleComparator(Locale locale)
    {
        super(locale);
    }

    public int compare(Object o1, Object o2)
    {
        if(!((o1 instanceof NavigationNodeResource && o2 instanceof NavigationNodeResource )))
        {
            return 0;
        }

        NavigationNodeResource r1 = (NavigationNodeResource)o1;
        NavigationNodeResource r2 = (NavigationNodeResource)o2;

        return compareStrings(r1.getTitle(), r2.getTitle());
    }
}
