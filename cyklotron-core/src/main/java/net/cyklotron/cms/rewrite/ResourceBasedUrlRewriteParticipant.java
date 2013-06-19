package net.cyklotron.cms.rewrite;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.objectledge.ComponentInitializationError;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.event.ResourceChangeListener;
import org.objectledge.coral.event.ResourceCreationListener;
import org.objectledge.coral.event.ResourceDeletionListener;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.AttributeFlags;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.schema.UnknownAttributeException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.ProtectedResource;

import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;

public abstract class ResourceBasedUrlRewriteParticipant<T extends Resource>
    implements ResourceCreationListener, ResourceDeletionListener, ResourceChangeListener,
    UrlRewriteParticipant
{
    private final ResourceClass<T> rc;

    private final Map<String, ResourceRef<T>> cache = new HashMap<>();

    private final LongKeyMap invCache = new LongKeyOpenHashMap();

    private final Lock r;

    private final Lock w;

    private AttributeDefinition<String> pathAttr;

    private final CoralSessionFactory coralSessionFactory;

    private void preloadCache(CoralSession coralSession)
        throws MalformedQueryException
    {
        QueryResults results = coralSession.getQuery().executeQuery(
            "FIND RESOURCE FROM " + rc.getName() + " WHERE DEFINED " + pathAttribute());
        for(T res : (List<T>)results.getList(1))
        {
            if(res.isDefined(pathAttr))
            {
                String path = res.get(pathAttr);
                cache.put(path, new ResourceRef<T>(res));
                invCache.put(res.getId(), path);
            }
        }
    }

    public ResourceBasedUrlRewriteParticipant(String rcName, Class<T> rcl,
        CoralSessionFactory coralSessionFactory)
    {
        this.coralSessionFactory = coralSessionFactory;
        ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
        r = rwl.readLock();
        w = rwl.writeLock();

        try(CoralSession coralSession = coralSessionFactory.getRootSession())
        {
            ResourceClass<T> rc = coralSession.getSchema().getResourceClass(rcName, rcl);
            this.rc = rc;
            pathAttr = rc.getAttribute(pathAttribute(), String.class);
            if((pathAttr.getFlags() & (AttributeFlags.REQUIRED | AttributeFlags.READONLY)) != 0)
            {
                throw new Error(pathAttribute() + " must not be REQUIRED or READONLY");
            }
            preloadCache(coralSession);
            coralSession.getEvent().addResourceCreationListener(this, rc);
            coralSession.getEvent().addResourceDeletionListener(this, rc);
            coralSession.getEvent().addResourceChangeListener(this, rc);
        }
        catch(Exception e)
        {
            throw new ComponentInitializationError("initialization failed", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void resourceChanged(Resource resource, Subject subject)
    {
        w.lock();
        try
        {
            final long id = resource.getId();
            final String oldPath = (String)invCache.get(id);
            cache.remove(oldPath);
            if(resource.isDefined(pathAttr))
            {
                final String path = resource.get(pathAttr);
                cache.put(path, new ResourceRef<T>((T)resource));
                invCache.put(id, path);
            }
        }
        finally
        {
            w.unlock();
        }
    }

    @Override
    public void resourceDeleted(Resource resource)
        throws Exception
    {
        w.lock();
        try
        {
            final String path = (String)invCache.remove(resource.getId());
            cache.remove(path);
        }
        finally
        {
            w.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void resourceCreated(Resource resource)
    {
        w.lock();
        try
        {
            if(resource.isDefined(pathAttr))
            {
                final String path = resource.get(pathAttr);
                cache.put(path, new ResourceRef<T>((T)resource));
                invCache.put(resource.getId(), path);
            }
        }
        finally
        {
            w.unlock();
        }
    }

    @Override
    public boolean matches(String path)
    {
        r.lock();
        try
        {
            return cache.containsKey(path);
        }
        finally
        {
            r.unlock();
        }
    }

    @Override
    public Set<String> getPaths()
    {
        r.lock();
        try
        {
            return Collections.unmodifiableSet(cache.keySet());
        }
        finally
        {
            r.unlock();
        }
    }

    @Override
    public void drop(String path)
    {
        w.lock();
        try
        {
            if(cache.containsKey(path))
            {
                ResourceRef<T> ref = cache.remove(path);
                invCache.remove(ref.getId());
                try(CoralSession coralSession = coralSessionFactory.getRootSession())
                {
                    T resource = ref.get(coralSession);
                    resource.unset(pathAttr);
                }
                catch(EntityDoesNotExistException e)
                {
                    // resource disappeared - ignore
                }
                catch(UnknownAttributeException | ValueRequiredException e)
                {
                    throw new RuntimeException("unexpected", e);
                }
            }
        }
        finally
        {
            w.unlock();
        }
    }

    @Override
    public RewriteTarget rewrite(String path)
    {
        r.lock();
        try
        {
            if(cache.containsKey(path))
            {
                try(CoralSession coralSession = coralSessionFactory.getRootSession())
                {
                    ResourceRef<T> ref = cache.get(path);
                    T resource = ref.get(coralSession);
                    return getTarget(resource);
                }
                catch(EntityDoesNotExistException e)
                {
                    // resource disappeared - can't rewrite
                    return null;
                }
            }
            return null;
        }
        finally
        {
            r.unlock();
        }
    }

    @Override
    public String path(Object object)
    {
        if(rc.getJavaClass().isAssignableFrom(object.getClass()))
        {
            @SuppressWarnings("unchecked")
            long id = ((T)object).getId();
            return (String)invCache.get(id);
        }
        return null;
    }

    public ProtectedResource guard(String path)
    {
        r.lock();
        try
        {
            if(cache.containsKey(path))
            {
                try(CoralSession coralSession = coralSessionFactory.getRootSession())
                {
                    ResourceRef<T> ref = cache.get(path);
                    T resource = ref.get(coralSession);
                    if(resource instanceof ProtectedResource)
                    {
                        return (ProtectedResource)resource;
                    }
                }
                catch(EntityDoesNotExistException e)
                {
                    // resource disappeared - can't rewrite
                    return null;
                }
            }
            return null;
        }
        finally
        {
            r.unlock();
        }
    }

    protected abstract String pathAttribute();

    protected abstract RewriteTarget getTarget(T resource);

    private class ResourceRef<R extends Resource>
    {
        private final long id;

        private WeakReference<R> ref;

        public ResourceRef(R resource)
        {
            id = resource.getId();
            ref = new WeakReference<>(resource);
        }

        public long getId()
        {
            return id;
        }

        public R get(CoralSession coralSession)
            throws EntityDoesNotExistException
        {
            R r = ref.get();
            if(r == null)
            {
                r = (R)coralSession.getStore().getResource(id);
                ref = new WeakReference<>(r);
            }
            return r;
        }
    }
}
