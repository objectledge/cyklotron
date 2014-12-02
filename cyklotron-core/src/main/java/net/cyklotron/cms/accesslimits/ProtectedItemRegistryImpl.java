package net.cyklotron.cms.accesslimits;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.event.ResourceCreationListener;
import org.objectledge.coral.event.ResourceDeletionListener;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.IdComparator;
import org.objectledge.web.ratelimit.impl.RequestInfo;

import com.google.common.base.Optional;

public class ProtectedItemRegistryImpl
    implements ResourceCreationListener, ResourceDeletionListener, ProtectedItemRegistry
{
    private static final String RULES_ROOT = "/cms/accesslimits/rules";

    private final Map<ProtectedItemResource, ProtectedItem> items = new ConcurrentSkipListMap<>(
        new IdComparator<ProtectedItemResource>());

    private final CoralSessionFactory coralSessionFactory;

    private final Logger log;

    public ProtectedItemRegistryImpl(CoralSessionFactory coralSessionFactory, Logger log)
        throws EntityDoesNotExistException
    {
        this.coralSessionFactory = coralSessionFactory;
        this.log = log;
        try(CoralSession coralSession = coralSessionFactory.getRootSession())
        {
            Resource[] children = coralSession.getStore().getResourceByPath(RULES_ROOT + "/*");
            for(Resource child : children)
            {
                if(child instanceof ProtectedItemResource)
                {
                    ProtectedItemResource childItem = (ProtectedItemResource)child;
                    items.put(childItem, new ProtectedItem(childItem, coralSession, log));
                }
            }
            ResourceClass<?> protectedItemRc = coralSession.getSchema().getResourceClass(
                ProtectedItemResource.CLASS_NAME);
            coralSession.getEvent().addResourceCreationListener(this, protectedItemRc);
            coralSession.getEvent().addResourceDeletionListener(this, protectedItemRc);
        }
    }

    @Override
    public void resourceDeleted(Resource resource)
        throws Exception
    {
        items.remove(resource);
    }

    @Override
    public void resourceCreated(Resource resource)
    {
        if(resource instanceof ProtectedItemResource)
        {
            try(CoralSession coralSession = coralSessionFactory.getRootSession())
            {
                ProtectedItemResource childItem = (ProtectedItemResource)resource;
                items.put(childItem, new ProtectedItem(childItem, coralSession, log));
            }
            catch(EntityDoesNotExistException e)
            {
                log.error("internal error", e);
            }
        }
    }

    @Override
    public Optional<ProtectedItem> getProtectedItem(RequestInfo request)
    {
        for(Map.Entry<ProtectedItemResource, ProtectedItem> e : items.entrySet())
        {
            if(e.getValue().matches(request))
            {
                return Optional.of(e.getValue());
            }
        }
        return Optional.absent();
    }
}
