package net.cyklotron.cms.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.labeo.Labeo;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.resource.table.ResourceClassFilter;

import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;

/**
 * This is a filter for filtering resources upon their resource class.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CmsResourceClassFilter.java,v 1.1 2005-01-12 20:44:32 pablo Exp $
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

    public CmsResourceClassFilter(String[] resourceClassResourceNames)
    {
        super(new ResourceClass[0]);
        IntegrationService integrationService = (IntegrationService)(Labeo.getBroker()
            .getService(IntegrationService.SERVICE_NAME));

        Set resClassResNames = new HashSet(Arrays.asList(resourceClassResourceNames));

        ResourceClassResource[] resClasses = integrationService.getResourceClasses();
        List acceptedResClasses = new LinkedList();
        for(int i=0; i<resClasses.length; i++)
        {
            ResourceClassResource resClassRes = resClasses[i];
            if(resClassResNames.contains(resClassRes.getName()))
            {
                acceptedResClasses.add(integrationService.getResourceClass(resClassRes));
            }
        }
        ResourceClass[] accptdResClasses = new ResourceClass[acceptedResClasses.size()];
        accptdResClasses = (ResourceClass[])(acceptedResClasses.toArray(accptdResClasses));
        
        init(accptdResClasses, true);
    }
}
