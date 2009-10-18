package net.cyklotron.cms.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.objectledge.context.Context;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.table.comparator.BaseStringComparator;

import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;

/**
 * This is a comparator for comparing resource names.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IndexTitleComparator.java,v 1.4 2005-02-15 17:31:51 rafal Exp $
 */
public class IndexTitleComparator<R extends Resource> extends BaseStringComparator<R>
{
    private IntegrationService integrationService;
    
    private Context context;
    
    private Map<ResourceClass, AttributeDefinition> attributeDefCache = new HashMap<ResourceClass, AttributeDefinition>();
    
    public IndexTitleComparator(Context context, IntegrationService integrationService,Locale locale)
    {
        super(locale);
        this.integrationService = integrationService;
        this.context = context;
    }
    
    public int compare(Resource r1, Resource r2)
    {
        return compareStrings(getIndexTitle(r1), getIndexTitle(r2));
    }
    
    private String getIndexTitle(Resource r)
    {
        ResourceClass rc = r.getResourceClass();
        AttributeDefinition attribute = attributeDefCache.get(rc);
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
