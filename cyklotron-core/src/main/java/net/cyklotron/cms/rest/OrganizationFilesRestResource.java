package net.cyklotron.cms.rest;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.core.header.FormDataContentDisposition;


@Produces({"application/xml","application/json"}) 
@Path("/pliki/{orgId}/{ftype}/{fname}")
public class OrganizationFilesRestResource {

    @GET
    @Produces("application/json")
    public String getFile(@PathParam("{orgId}") long orgId, 
    		@PathParam("{ftype}") String ftype,
    		@PathParam("{fname}") String fname) {
        return "Pobierz pjedynczy plik :" + orgId + " ftype:" + ftype +" fname:" + fname;    
    }

    @GET
    @Path("/pliki/{orgId}")
    @Produces("application/json")
    public String getFiles(@PathParam("{orgId}") long orgId) {
        return "Wszystkie pliki z podziałem an kategorie.";    
    }

    @POST
    @Produces("application/json")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String createFile(@PathParam("{orgId}") long orgId, 
    		@PathParam("{ftype}") String ftype,
    		@PathParam("{fname}") String fname,
    		@FormParam("file") InputStream uploadedInputStream,
    		@FormParam("file") FormDataContentDisposition fileDetail
    		) {
        return "Plik do stworzenia: \n " + orgId + " ftype:" + ftype +" fname:" + fname + "\nnazwa" +
    		     fileDetail.getFileName() + " o wielkości :" + fileDetail.getSize();    
    }

    @PUT
    @Produces("application/json")
    public String modifyFile(@PathParam("{orgId}") long orgId, 
    		@PathParam("{ftype}") String ftype,
    		@PathParam("{fname}") String fname,
    		@FormParam("file") InputStream uploadedInputStream,
    		@FormParam("file") FormDataContentDisposition fileDetail) {
        return "Plik do modyfikacji\n orgId:" + orgId + " ftype:" + ftype +" fname:" + fname +
        		" \nnazwa: " + fileDetail.getFileName() + " o wielkości :" + fileDetail.getSize();    
    }    

}
