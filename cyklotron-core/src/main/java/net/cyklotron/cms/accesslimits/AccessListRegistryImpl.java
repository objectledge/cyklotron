package net.cyklotron.cms.accesslimits;

import java.net.InetAddress;
import java.util.Map;
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
import org.objectledge.web.ratelimit.impl.AccessListRegistry;

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
                    lists.put(listResource.getName(), new AccessList(listResource, null, log));
                    oldNames.put(listResource, listResource.getName());
                }
            }
            ResourceClass<?> listRc = coralSession.getSchema().getResourceClass(
                AccessListResource.CLASS_NAME);
            coralSession.getEvent().addResourceChangeListener(this, listRc);
            coralSession.getEvent().addResourceCreationListener(this, listRc);
            coralSession.getEvent().addResourceDeletionListener(this, listRc);
            ResourceClass<?> listItemRc = coralSession.getSchema().getResourceClass(
                AccessListItemResource.CLASS_NAME);
            coralSession.getEvent().addResourceChangeListener(this, listItemRc);
            coralSession.getEvent().addResourceCreationListener(this, listItemRc);
            coralSession.getEvent().addResourceDeletionListener(this, listItemRc);
        }
    }

    private AccessListResource rebuildList(Resource resource, Resource skip)
    {
        AccessListResource listResource = (AccessListResource)resource;
        lists.put(listResource.getName(), new AccessList(listResource, skip, log));
        return listResource;
    }

    @Override
    public void resourceCreated(Resource resource)
    {
        if(resource instanceof AccessListResource)
        {
            AccessListResource listResource = rebuildList(resource, null);
            oldNames.put(listResource, listResource.getName());
        }
        if(resource instanceof AccessListItemResource)
        {
            rebuildList(resource.getParent(), null);
        }
    }

    @Override
    public void resourceChanged(Resource resource, Subject subject)
    {
        if(resource instanceof AccessListResource)
        {
            AccessListResource listResource = rebuildList(resource, null);
            final String oldName = oldNames.get(resource);
            if(!oldName.equals(resource.getName()))
            {
                lists.remove(oldName);
                oldNames.put(listResource, listResource.getName());
            }
        }
        if(resource instanceof AccessListItemResource)
        {
            rebuildList(resource.getParent(), null);
        }
    }

    @Override
    public void resourceDeleted(Resource resource)
        throws Exception
    {
        if(resource instanceof AccessListResource)
        {
            lists.remove(resource.getName());
            oldNames.remove(resource);            
        }
        if(resource instanceof AccessListItemResource)
        {
            rebuildList(resource.getParent(), resource);
        }
    }

    @Override
    public boolean contains(String listName, InetAddress address)
    {
        AccessList list = lists.get(listName);
        if(list != null)
        {
            return list.contains(address);
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean anyContains(InetAddress address)
    {
        for(AccessList list : lists.values())
        {
            if(list.contains(address))
            {
                return true;
            }               
        }
        return false;
    }
}
