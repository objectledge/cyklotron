package net.cyklotron.ngo.bazy.files;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.cyklotron.cms.rest.FilesRestResource;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Produces({ "application/xml", "application/json" })
@Path("/bazy/pliki")
public class OgranizationFilesResource extends FilesRestResource
{

    

}
