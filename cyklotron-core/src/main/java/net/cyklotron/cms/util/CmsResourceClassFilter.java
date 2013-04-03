package net.cyklotron.cms.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.filter.ResourceClassFilter;

import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;

/**
 * This is a filter for filtering resources upon their resource class.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CmsResourceClassFilter.java,v 1.3 2005-02-09 22:20:08 rafal Exp $
 */
public class CmsResourceClassFilter<R extends Resource>
    extends ResourceClassFilter<R>
{
    public CmsResourceClassFilter(CoralSession coralSession, IntegrationService integrationService,
        String[] resourceClassResourceNames)
    {
        super(makeClassSet(coralSession, integrationService, resourceClassResourceNames), true);
    }
    
    private static Set<ResourceClass<?>> makeClassSet(CoralSession coralSession,
        IntegrationService integrationService, String[] resourceClassResourceNames)
    {
        Set<String> resClassResNames = resolveClassNames(resourceClassResourceNames, coralSession);
        ResourceClassResource[] resClasses = integrationService.getResourceClasses(coralSession);
        Set<ResourceClass<?>> acceptedResClasses = new HashSet<>();
        for(int i=0; i<resClasses.length; i++)
        {
            ResourceClassResource resClassRes = resClasses[i];
            if(resClassResNames.contains(resClassRes.getName()))
            {
                acceptedResClasses.add(integrationService.getResourceClass(coralSession, resClassRes));
            }
        }
        return acceptedResClasses;
    }

    private static Set<String> resolveClassNames(String[] resourceClassResourceNames,
        CoralSession coralSession)
    {
        final Set<String> names = new HashSet<>(Arrays.asList(resourceClassResourceNames));
        for(String name : resourceClassResourceNames)
        {
            if(name.matches("\\d+"))
            {
                try
                {
                    Resource r = coralSession.getStore().getResource(Long.parseLong(name));
                    names.add(r.getName());
                }
                catch(NumberFormatException | EntityDoesNotExistException e)
                {
                    // ignore
                }
            }
            else
            {
                names.add(name);
            }
        }
        return names;
    }
}
