package net.cyklotron.cms.modules.rest.ping;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ping")
public class Ping
{
    public Ping() { }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPong()
    {
       return Response.ok("pong").build();
    }
}
