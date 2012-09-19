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

import net.cyklotron.cms.rest.FilesProvider;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

public class OrganizationFilesProvider extends FilesProvider
{
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
