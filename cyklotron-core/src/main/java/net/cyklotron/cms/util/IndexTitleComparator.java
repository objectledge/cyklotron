package net.cyklotron.cms.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.labeo.Labeo;
import net.labeo.services.resource.AttributeDefinition;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.resource.table.BaseStringComparator;

import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;

/**
 * This is a comparator for comparing resource names.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IndexTitleComparator.java,v 1.1 2005-01-12 20:44:32 pablo Exp $
 */
public class IndexTitleComparator extends BaseStringComparator
{
    private IntegrationService integrationService;
    
    private Map attributeDefCache = new HashMap();
    
    public IndexTitleComparator(Locale locale)
    {
        super(locale);
        integrationService = (IntegrationService)
            (Labeo.getBroker().getService(IntegrationService.SERVICE_NAME));
    }
    
    public int compare(Object o1, Object o2)
    {
        if(!((o1 instanceof Resource && o2 instanceof Resource )))
        {
            return 0;
        }

        Resource r1 = (Resource)o1;
        Resource r2 = (Resource)o2;

        return compareStrings(getIndexTitle(r1), getIndexTitle(r2));
    }
    
    private String getIndexTitle(Resource r)
    {
        ResourceClass rc = r.getResourceClass();
        AttributeDefinition attribute = (AttributeDefinition)(attributeDefCache.get(rc));
        if(attribute == null)
        {
            ResourceClassResource rcr = integrationService.getResourceClass(rc);
            attribute = rc.getAttribute(rcr.getIndexTitle());
            attributeDefCache.put(rc, attribute);
        }
        return r.get(attribute).toString();
    }
}
