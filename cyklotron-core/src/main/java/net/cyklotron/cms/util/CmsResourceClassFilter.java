package net.cyklotron.cms.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;

import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.filter.ResourceClassFilter;

/**
 * This is a filter for filtering resources upon their resource class.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CmsResourceClassFilter.java,v 1.2 2005-01-18 17:38:32 pablo Exp $
 */
public class CmsResourceClassFilter
    extends ResourceClassFilter
{
    public CmsResourceClassFilter(ResourceClass acceptedResourceClass)
    {
        super(acceptedResourceClass);
    }

    public CmsResourceClassFilter(ResourceClass acceptedResourceClass, boolean allowInheritance)
    {
        super(acceptedResourceClass, allowInheritance);
    }
    
    public CmsResourceClassFilter(ResourceClass[] acceptedResourceClasses)
    {
        super(acceptedResourceClasses);
    }

    public CmsResourceClassFilter(CoralSession coralSession, IntegrationService integrationService, String[] resourceClassResourceNames)
    {
        super(new ResourceClass[0]);
        Set resClassResNames = new HashSet(Arrays.asList(resourceClassResourceNames));
        ResourceClassResource[] resClasses = integrationService.getResourceClasses(coralSession);
        List acceptedResClasses = new LinkedList();
        for(int i=0; i<resClasses.length; i++)
        {
            ResourceClassResource resClassRes = resClasses[i];
            if(resClassResNames.contains(resClassRes.getName()))
            {
                acceptedResClasses.add(integrationService.getResourceClass(coralSession, resClassRes));
            }
        }
        ResourceClass[] accptdResClasses = new ResourceClass[acceptedResClasses.size()];
        accptdResClasses = (ResourceClass[])(acceptedResClasses.toArray(accptdResClasses));
        
        init(accptdResClasses, true);
    }
}
