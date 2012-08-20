package net.cyklotron.cms.rest;

import java.io.InputStream;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.objectledge.context.Context;

import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.files.internal.FilesServiceImpl;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.jersey.spi.resource.Singleton;


@Produces({"application/xml","application/json"}) 
@Path("files")
@Singleton
public class FilesRestResource {
	
    @javax.ws.rs.core.Context 
    ServletContext context;
    
    FilesService filesService;
    
    public FilesRestResource() {
    }
    
    @GET
    @Produces("application/json")
    @Path("{filepath: .*}")
    public CmsFile getFile(@PathParam("filepath") String filepath, 
    		@PathParam("filename") String filename) {
                
        Context ledgeContext = (Context)context.getAttribute("ledgeContext");
        filesService = (FilesService)ledgeContext.getAttribute(FilesService.class);
        return new CmsFile();
    }

//    @GET
//    @Path("/{orgId: [a-zA-Z0-9_]+}")
//    @Produces("application/json")
//    public String getFiles(@PathParam("orgId") String orgId) {
//        return "Wszystkie pliki organizacji orgId:" + orgId + ", z podziałem an kategorie.";    
//    }

    @GET
    @Path("/{orgId: [a-zA-Z0-9_]+}/{ftype}")
    @Produces("application/json")
    public String getFiles(@PathParam("orgId") String orgId, 
    		@PathParam("ftype") String ftype) {
        return "Wszystkie pliki organizacji orgId:" + orgId + ", typu:" + ftype + ".";    
    }

    @POST
    @Produces("application/json")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/{orgId: [a-zA-Z0-9_]+}/{ftype}/{fname}")
    public String createFile(@PathParam("orgId") String orgId, 
    		@PathParam("ftype") String ftype,
    		@PathParam("fname") String fname,
    		@FormDataParam("file") InputStream uploadedInputStream,
    		@FormDataParam("file") FormDataContentDisposition fileDetail
    		) {
        return "Plik do stworzenia: \n " + orgId + " ftype:" + ftype +" fname:" + fname + "\nnazwa" +
    		     fileDetail.getFileName() + " o wielkości :" + fileDetail.getSize();    
    }

    @PUT
    @Produces("application/json")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/{orgId}/{ftype}/{fname}")
    public String modifyFile(@PathParam("orgId") String orgId, 
    		@PathParam("ftype") String ftype,
    		@PathParam("fname") String fname,
    		@FormDataParam("file") InputStream uploadedInputStream,
    		@FormDataParam("file") FormDataContentDisposition fileDetail) {
        return "Plik do modyfikacji\n orgId:" + orgId + " ftype:" + ftype +" fname:" + fname +
        		" \nnazwa: " + fileDetail.getFileName() + " o wielkości :" + fileDetail.getSize();    
    }    

}
