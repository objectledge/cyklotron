package net.cyklotron.cms.modules.rest.forum;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.web.rest.RequireAtLeastOneRole;
import org.objectledge.coral.web.rest.RequireCoralRole;

@Path("forum")
public class Forum
{

    private static final int LIMIT_OF_POSTS = 20;

    @Inject
    private CoralSessionFactory coralSessionFactory;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequireAtLeastOneRole({ @RequireCoralRole("cms.administrator"),
                    @RequireCoralRole("cms.registered") })
    public Collection<PostDto> getUserPosts(@QueryParam("user") String user,
        @QueryParam("limit") @DefaultValue("20") int requestedLimit,
        @QueryParam("offset") @DefaultValue("0") int offset)
    {
        final int limit = hardLimit(requestedLimit);
        try(CoralSession coralSession = coralSessionFactory.getRootSession())
        {

        }
        return null;
    }

    private int hardLimit(int requestedLimit)
    {
        return requestedLimit < LIMIT_OF_POSTS ? requestedLimit : LIMIT_OF_POSTS;
    }

}
