package net.cyklotron.cms.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.objectledge.context.Context;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.BaseStringComparator;

import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;

/**
 * This is a comparator for comparing resource names.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IndexTitleComparator.java,v 1.3 2005-02-09 22:20:08 rafal Exp $
 */
public class IndexTitleComparator extends BaseStringComparator
{
    private IntegrationService integrationService;
    
    private Context context;
    
    private Map attributeDefCache = new HashMap();
    
    public IndexTitleComparator(Context context, IntegrationService integrationService,Locale locale)
    {
        super(locale);
        this.integrationService = integrationService;
        this.context = context;
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
            CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
            ResourceClassResource rcr = integrationService.getResourceClass(coralSession, rc);
            attribute = rc.getAttribute(rcr.getIndexTitle());
            attributeDefCache.put(rc, attribute);
        }
        return r.get(attribute).toString();
    }
}
