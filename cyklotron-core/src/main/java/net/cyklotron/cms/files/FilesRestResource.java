package net.cyklotron.cms.files;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;


@Path("/files")
public class FilesRestResource {


    @GET
    @Produces("text/plain")
    public String getHelloMessage() {
        return "Hello World";    
    }


}
