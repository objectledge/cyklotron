package net.cyklotron.cms.aggregation.util;

import java.util.Locale;

import org.objectledge.table.comparator.BaseStringComparator;

import net.cyklotron.cms.aggregation.ImportResource;

/**
 * This is a comparator for comparing source site names.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ImportResourceTargetSiteComparator.java,v 1.4 2005-02-15 17:31:48 rafal Exp $
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
