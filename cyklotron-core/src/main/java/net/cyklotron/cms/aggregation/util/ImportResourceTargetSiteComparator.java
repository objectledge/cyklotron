package net.cyklotron.cms.aggregation.util;

import java.util.Locale;

import net.cyklotron.cms.aggregation.ImportResource;

import org.objectledge.coral.table.comparator.BaseStringComparator;

/**
 * This is a comparator for comparing source site names.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ImportResourceTargetSiteComparator.java,v 1.2 2005-01-12 20:55:53 pablo Exp $
 */
public class ImportResourceTargetSiteComparator
    extends BaseStringComparator
{
    public ImportResourceTargetSiteComparator(Locale locale)
    {
        super(locale);
    }
    
    public int compare(Object o1, Object o2)
    {
        if(!((o1 instanceof ImportResource && o2 instanceof ImportResource )))
        {
            return 0;
        }
        ImportResource r1 = (ImportResource)o1;
        ImportResource r2 = (ImportResource)o2;
        return compareStrings(r1.getDestinationSite().getName(), r2.getDestinationSite().getName());
    }
}
