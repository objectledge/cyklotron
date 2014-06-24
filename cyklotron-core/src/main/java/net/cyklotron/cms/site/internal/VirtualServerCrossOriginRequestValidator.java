package net.cyklotron.cms.site.internal;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.objectledge.ComponentInitializationError;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.event.ResourceChangeListener;
import org.objectledge.coral.event.ResourceCreationListener;
import org.objectledge.coral.event.ResourceDeletionListener;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.web.cors.CrossOriginRequestValidator;
import org.picocontainer.Startable;

import net.cyklotron.cms.site.VirtualServerResource;

public class VirtualServerCrossOriginRequestValidator
    implements CrossOriginRequestValidator, Startable, ResourceCreationListener,
    ResourceChangeListener, ResourceDeletionListener
{
    private final CoralSessionFactory coralSessionFactory;

    private Set<String> allowed = new CopyOnWriteArraySet<>();

    private Map<Resource, String> prevName = new ConcurrentHashMap<>();

    public VirtualServerCrossOriginRequestValidator(CoralSessionFactory coralSessionFactory)
        throws Exception
    {
        this.coralSessionFactory = coralSessionFactory;
    }

    public void start()
    {
        try(CoralSession coralSession = coralSessionFactory.getRootSession())
        {
            init(coralSession);
            ResourceClass<VirtualServerResource> rclass = coralSession.getSchema()
                .getResourceClass(VirtualServerResource.CLASS_NAME, VirtualServerResource.class);
            coralSession.getEvent().addResourceCreationListener(this, rclass);
            coralSession.getEvent().addResourceChangeListener(this, rclass);
            coralSession.getEvent().addResourceDeletionListener(this, rclass);
        }
        catch(MalformedQueryException | EntityDoesNotExistException e)
        {
            throw new ComponentInitializationError(
                "failed to initialize VirtualServerCrossOriginRequestValidator", e);
        }
    }

    public void stop()
    {

    }

    private void init(CoralSession coralSession)
        throws MalformedQueryException
    {
        QueryResults servers = coralSession.getQuery().executeQuery(
            "FIND RESOURCE FROM " + VirtualServerResource.CLASS_NAME);
        for(Resource resource : servers.getList(1))
        {
            if(resource instanceof VirtualServerResource)
            {
                VirtualServerResource server = (VirtualServerResource)resource;
                if(!server.getSite().getRequiresSecureChannel())
                {
                    allowed.add("http://" + server.getName());
                }
                allowed.add("https://" + server.getName());
                prevName.put(resource, resource.getName());
            }
        }
    }

    @Override
    public boolean isAllowed(String originUri)
    {
        return allowed.contains(originUri);
    }

    @Override
    public void resourceDeleted(Resource resource)
        throws Exception
    {
        allowed.remove("http://" + resource.getName());
        allowed.remove("https://" + resource.getName());
    }

    @Override
    public void resourceChanged(Resource resource, Subject subject)
    {
        allowed.remove("http://" + prevName.get(resource));
        allowed.remove("https://" + prevName.get(resource));
        resourceCreated(resource);
    }

    @Override
    public void resourceCreated(Resource resource)
    {
        if(resource instanceof VirtualServerResource)
        {
            VirtualServerResource server = (VirtualServerResource)resource;
            if(!server.getSite().getRequiresSecureChannel())
            {
                allowed.add("http://" + server.getName());
            }
            allowed.add("https://" + server.getName());
            prevName.put(resource, resource.getName());
        }
    }
}
