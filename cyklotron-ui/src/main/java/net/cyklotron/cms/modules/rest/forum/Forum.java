package net.cyklotron.cms.modules.rest.forum;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("forum")
public class Forum
{

    @Inject
    public Forum()
    {
        
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<PostDto> getUserPosts()
    {
        return null;
    }

}
