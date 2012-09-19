package net.cyklotron.cms.rest;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.files.FilesTool;
import net.cyklotron.cms.site.SiteResource;

import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.web.LedgeServletContextListener;
import org.picocontainer.PicoContainer;


//import com.sun.jersey.spi.resource.Singleton;


@Produces({"application/xml","application/json"}) 
@Path("files")
//@Singleton
public class FilesProvider {
	
    @javax.ws.rs.core.Context 
    protected ServletContext context;    
    
    public FilesProvider() {
    }
    
    @GET
    @Produces("application/json")
    @Path("{filepath}/{filename}")
    public CmsFile getFile(@PathParam("filepath") String filepath, 
        @PathParam("filename") String filename) {         
        try
        {
            return getCmsFile(filepath + "/" + filename);
        }
        catch(EntityDoesNotExistException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch(FilesException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }    

//    @POST
//    @Produces("application/json")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Path("/{orgId: [a-zA-Z0-9_]+}/{ftype}/{fname}")
//    public String createFile(@PathParam("orgId") String orgId, 
//            @PathParam("ftype") String ftype,
//            @PathParam("fname") String fname,
//            @FormDataParam("file") InputStream uploadedInputStream,
//            @FormDataParam("file") FormDataContentDisposition fileDetail
//            ) {
//        return "Plik do stworzenia: \n " + orgId + " ftype:" + ftype +" fname:" + fname + "\nnazwa" +
//                 fileDetail.getFileName() + " o wielkości :" + fileDetail.getSize();    
//    }
//    
//    
//
//    @PUT
//    @Produces("application/json")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Path("/{orgId}/{ftype}/{fname}")
//    public String modifyFile(@PathParam("orgId") String orgId, 
//            @PathParam("ftype") String ftype,
//            @PathParam("fname") String fname,
//            @FormDataParam("file") InputStream uploadedInputStream,
//            @FormDataParam("file") FormDataContentDisposition fileDetail) {
//        return "Plik do modyfikacji\n orgId:" + orgId + " ftype:" + ftype +" fname:" + fname +
//                " \nnazwa: " + fileDetail.getFileName() + " o wielkości :" + fileDetail.getSize();    
//    }       
    
    
    //overridable accessors

    protected CoralSession getCoralSession()
    {
        final PicoContainer container = (PicoContainer)context.getAttribute(LedgeServletContextListener.CONTAINER_CONTEXT_KEY);
        final Context ledgeContext = (Context)container.getComponentInstance(Context.class);
        return (CoralSession)ledgeContext.getAttribute(CoralSession.class);
    }

    protected FilesService getFileService()
    {
        final PicoContainer container = (PicoContainer)context.getAttribute(LedgeServletContextListener.CONTAINER_CONTEXT_KEY);
        final FilesService filesService = (FilesService)container.getComponentInstance(FilesService.class);        
        return filesService;
    }
    
    protected FilesTool getFilesTool() {
        final PicoContainer container = (PicoContainer)context.getAttribute(LedgeServletContextListener.CONTAINER_CONTEXT_KEY);
        final FilesTool tool = (FilesTool)container.getComponentInstance(FilesTool.class);        
        return tool;
    }
    
    public CmsFile getCmsFile(String filepath) throws EntityDoesNotExistException, FilesException {
        final CoralSession session = getCoralSession();
        return new CmsFile(getFileService().getFileResource(session, filepath, getSite()), 
            getFilesTool());
    }
    
    protected SiteResource getSite() {
        return null;
    }
}
