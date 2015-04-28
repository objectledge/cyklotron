package net.cyklotron.cms.accesslimits;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.event.ResourceChangeListener;
import org.objectledge.coral.event.ResourceCreationListener;
import org.objectledge.coral.event.ResourceDeletionListener;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;

public class AccessListRegistryImpl
    implements ResourceChangeListener, ResourceCreationListener, ResourceDeletionListener,
    AccessListRegistry
{
    private static final String LISTS_ROOT = "/cms/accesslimits/lists";

    private final Map<String, AccessList> lists = new ConcurrentHashMap<>();

    // used to track renames
    private final Map<Resource, String> oldNames = new ConcurrentHashMap<>();

    private Logger log;

    public AccessListRegistryImpl(CoralSessionFactory coralSessionFactory, Logger log)
        throws EntityDoesNotExistException
    {
        this.log = log;
        try(CoralSession coralSession = coralSessionFactory.getRootSession())
        {
            Resource[] children = coralSession.getStore().getResourceByPath(LISTS_ROOT + "/*");
            for(Resource child : children)
            {
                if(child instanceof AccessListResource)
                {
                    AccessListResource listResource = (AccessListResource)child;
                    lists.put(listResource.getName(), new AccessList(listResource, log));
                    oldNames.put(listResource, listResource.getName());
                }
            }
            ResourceClass<?> actionRc = coralSession.getSchema().getResourceClass(
                AccessListResource.CLASS_NAME);
            coralSession.getEvent().addResourceChangeListener(this, actionRc);
            coralSession.getEvent().addResourceCreationListener(this, actionRc);
            coralSession.getEvent().addResourceDeletionListener(this, actionRc);
        }
    }

    @Override
    public void resourceCreated(Resource resource)
    {
        if(resource instanceof AccessListResource)
        {
            AccessListResource listResource = (AccessListResource)resource;
            lists.put(listResource.getName(), new AccessList(listResource, log));
            oldNames.put(listResource, listResource.getName());
        }
    }

    @Override
    public void resourceChanged(Resource resource, Subject subject)
    {
        if(resource instanceof AccessListResource)
        {
            AccessListResource listResource = (AccessListResource)resource;
            lists.put(listResource.getName(), new AccessList(listResource, log));
            final String oldName = oldNames.get(resource);
            if(!oldName.equals(resource.getName()))
            {
                lists.remove(oldName);
                oldNames.put(listResource, listResource.getName());
            }
        }
    }

    @Override
    public void resourceDeleted(Resource resource)
        throws Exception
    {
        lists.remove(resource.getName());
        oldNames.remove(resource);
    }

    @Override
    public Set<String> getMatchingLists(InetAddress address)
    {
        Set<String> listNames = new HashSet<>();
        for(Map.Entry<String, AccessList> entries : lists.entrySet())
        {
            if(entries.getValue().matches(address))
            {
                listNames.add(entries.getKey());
            }
        }
        return listNames;
    }
}
