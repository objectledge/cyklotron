package net.cyklotron.ngo.bazy.files;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.web.LedgeServletContextListener;
import org.picocontainer.PicoContainer;

import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.rest.FilesProvider;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.ngo.bazy.BazyngoService;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import net.cyklotron.cms.rest.CmsFile;

@Path("/org_files")
public class OrganizationFilesProvider extends FilesProvider
{
    private static final String BASE_NODE_NAME = "org_files";

    @GET
    @Path("/{orgId: [a-zA-Z0-9_]+}/{ftype}/{fname}")
    @Produces("application/json")
    public CmsFile getFile(@PathParam("orgId") String orgId, 
            @PathParam("ftype") String ftype,
            @PathParam("fname") String fname) {
        try
        {
            return getCmsFile(orgId + "/" + ftype, fname);
        }
        catch(EntityDoesNotExistException e)
        {
            e.printStackTrace();
        }
        catch(FilesException e)
        {
            e.printStackTrace();
        }
        
        return null;
                        
    }

    @GET
    @Path("/{orgId: [a-zA-Z0-9_]+}/{ftype}")
    @Produces("application/json")
    public List<CmsFile> getFiles(@PathParam("orgId") String orgId, 
            @PathParam("ftype") String ftype) {
        return getCmsFiles(orgId + "/" + ftype);
    }

//    @GET
//    @Path("/file/{fid}")
//    @Produces("application/json")
//    public List<CmsFile> getFiles(@PathParam("fid") String fid) {
//        return getCmsFileById(fid);
//    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/{orgId: [a-zA-Z0-9_]+}/{ftype}")
    public Response createFile(@PathParam("orgId") String orgId, 
            @PathParam("ftype") String ftype,
            @FormDataParam("fname") String fname,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail
            ) {
        return createCmsFile(buildPath(orgId, ftype, fname), uploadedInputStream, fileDetail);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/{fid}")
    public Response modifyFile(@PathParam("fid") long fid,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {
        return modifyCmsFile(fid, uploadedInputStream, fileDetail);
    }    
    
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/meta/{fid}")
    public Response modifyFileMeta(JAXBElement<CmsFile> file, 
        @PathParam("fid") String fid) {
        CmsFile f = file.getValue();
        f.setId(Long.parseLong(fid));
        return modifyCmsFileMeta(f);
    }    
    
    @DELETE
    @Produces("application/json")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/{orgId}/{ftype}/{fname}")
    public Response deleteFile(@PathParam("orgId") String orgId, 
            @PathParam("ftype") String ftype,
            @PathParam("fname") String fname) {
        return deleteCmsFile(buildPath(orgId, ftype, fname));                    
    }    
  
    @Override
    public SiteResource getSite() {
        final PicoContainer container = (PicoContainer)getContext().getAttribute(LedgeServletContextListener.CONTAINER_CONTEXT_KEY);
        final BazyngoService bazyngo = (BazyngoService)container.getComponentInstance(BazyngoService.class);        

        return bazyngo.getSite();
    }
    
    private String buildPath(String orgId, String ftype, String fname) {
        return BASE_NODE_NAME + "/" + buildOrgIdPath(orgId) + "/" + ftype + "/" + fname;
    }
    
    private String buildOrgIdPath(String orgId) {
        return orgId;
    }
}
