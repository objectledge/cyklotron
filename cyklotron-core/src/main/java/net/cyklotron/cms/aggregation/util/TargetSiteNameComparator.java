package net.cyklotron.cms.aggregation.util;

import java.util.Locale;
import net.labeo.services.resource.table.BaseStringComparator;
import net.cyklotron.cms.aggregation.RecommendationResource;

/**
 * This is a comparator for comparing target site names.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: TargetSiteNameComparator.java,v 1.1 2005-01-12 20:45:12 pablo Exp $
 */
public class TargetSiteNameComparator
    extends BaseStringComparator
{
    public TargetSiteNameComparator(Locale locale)
    {
        super(locale);
    }
    
    public int compare(Object o1, Object o2)
    {
        if(!((o1 instanceof RecommendationResource && o2 instanceof RecommendationResource )))
        {
            return 0;
        }
        RecommendationResource r1 = (RecommendationResource)o1;
        RecommendationResource r2 = (RecommendationResource)o2;
        return compareStrings(r1.getTargetSite().getName(), r2.getTargetSite().getName());
    }
}
