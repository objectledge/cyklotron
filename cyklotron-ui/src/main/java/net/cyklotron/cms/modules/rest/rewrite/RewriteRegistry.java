package net.cyklotron.cms.modules.rest.rewrite;

import java.util.Collections;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;

import net.cyklotron.cms.ProtectedResource;
import net.cyklotron.cms.rewrite.UrlRewriteRegistry;

@Path("/rewriteRegistry")
public class RewriteRegistry
{
    @Inject
    private UrlRewriteRegistry registry;

    @Inject
    private CoralSessionFactory coralSessionFactory;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response info()
    {
        return Response.ok(registry.getRewriteInfo()).build();
    }

    @GET
    @Path("{path}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response check(@PathParam("path") String path)
    {
        boolean defined = registry.getPaths().contains(path);
        return Response.ok(Collections.singletonMap("defined", defined)).build();
    }

    @DELETE
    @Path("{path}")
    public Response drop(@PathParam("path") String path)
    {
        if(registry.getPaths().contains(path))
        {
            ProtectedResource guard = registry.guard(path);
            CoralSession coralSession = coralSessionFactory.getCurrentSession();
            if(coralSession == null
                || !guard.canModify(coralSession, coralSession.getUserSubject()))
            {
                return Response.status(Status.UNAUTHORIZED).build();
            }
            registry.drop(path);
            return Response.ok().build();
        }
        else
        {
            return Response.status(Status.NOT_FOUND).build();
        }
    }
}
