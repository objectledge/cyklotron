package net.cyklotron.cms.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FileAlreadyExistsException;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.files.FilesTool;
import net.cyklotron.cms.files.FilesToolFactory;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.web.LedgeServlet;
import org.objectledge.web.LedgeServletContextListener;
import org.picocontainer.PicoContainer;

import com.sun.jersey.core.header.FormDataContentDisposition;

public class FilesProvider {
	
    private SiteResource site = null;
    @javax.ws.rs.core.Context 
    private ServletContext context;
    private Logger logger;    
    
    public FilesProvider() {
        BasicConfigurator.configure();
        logger = Logger.getLogger(LedgeServlet.class);
    }           
    
    //CRUD
    
    /**
     * Creates CmsFile object stuffed with FileResource object retrieved from given path.
     * @param filepath path for FileResource
     * @return the CmsFile object
     */
    public CmsFile getCmsFile(String filepath) {
        final CoralSession session = getCoralSession();
        try
        {
            CmsFile file = new CmsFile(getFileService().getFileResource(session, filepath, getSite()), 
                getFilesTool());
            return file;
        }
        catch(EntityDoesNotExistException e)
        {            
            logger.error("Error while retrieving file.", e);
            throw new RESTWebException(Response.Status.NOT_FOUND, e.getMessage());
        }
        catch(FilesException e)
        {
            logger.error("Error while retrieving file.", e);
            throw new RESTWebException(Response.Status.NOT_FOUND, e.getMessage());
        }
    }    
 
    /**
     * Creates CmsFile object stuffed with FileResource object retrieved from given resource id.
     * @param id id of FileResource
     * @return the CmsFile object
     */
    public CmsFile getCmsFileById(long id) {
        final CoralSession session = getCoralSession();
        try
        {
            CmsFile file = new CmsFile(getFileService().getFileResource(session, id), 
                getFilesTool());
            return file;
        }
        catch(EntityDoesNotExistException e)
        {
            logger.error("Error while retrieving file.", e);
            throw new RESTWebException(Response.Status.NOT_FOUND, e.getMessage());            
        }
    }    
 

    /**
     * Creates CmsFile object stuffed with newly created FileResource.
     * @param fpath path to create file resource
     * @param uploadedInputStream InputStream containing file content
     * @param fileDetail file details
     * @return response to parse to servlet
     */
    public Response createCmsFile(String fpath,
            InputStream uploadedInputStream,
            FormDataContentDisposition fileDetail
        ) {
        
        
        final FilesService filesService = getFileService();
        final CoralSession coralSession = getCoralSession();
        final SiteResource site = getSite();
        FileResource f = null;             
        
        if(fpath.length() == 0) {
            return errorResponse(Response.Status.BAD_REQUEST, "Empty File Name.");
        }
        final String[] tokens = fpath.split("/");
        String fname = tokens[tokens.length-1];
        
        if(uploadedInputStream == null) {
            return errorResponse(Response.Status.BAD_REQUEST, "Missing file content");
        }
        String mimeType = fileDetail.getType();
        try
        {
            DirectoryResource dir = filesService.createParentDirs(coralSession, fpath, site);
            f = filesService.createFile(
                coralSession, 
                fname, 
                uploadedInputStream, 
                mimeType, 
                null, 
                (DirectoryResource)dir);
        }
        catch (FileAlreadyExistsException e1) {
            logger.error("Error while creating file resource.", e1);
            return errorResponse(Response.Status.CONFLICT, e1.getMessage());
        }
        catch(FilesException e)
        {
            logger.error("Error while creating file resource.", e);
            return errorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        }
        catch(InvalidResourceNameException e)
        {
            logger.error("Error while creating file resource.", e);
            return errorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        
        return postResponse(new CmsFile(f, getFilesTool()));    
    }

    
    /**
     * Deletes FileResource of given path.
     * @param fpath path of resource to delete
     * @return response for servlet
     */
    public Response deleteCmsFileByPath(String fpath) {
        CmsFile file;
        file = getCmsFile(fpath);
        return deleteCmsFile(file);
    }
    
    /**
     * Deletes FileResource of given id.
     * @param id id of resource to delete
     * @return response for servlet
     */       
    public Response deleteCmsFileById(long id) {
        CmsFile file;
        file = getCmsFileById(id);
        return deleteCmsFile(file);
    }
       
    private Response deleteCmsFile(CmsFile file) {
        long id = file.getId();
        try
        {
            getFileService().deleteFile(getCoralSession(), file.getFileResource());
        }
        catch(FilesException e)
        {
            logger.error("Error while deleting file resource.", e);
            return errorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return deleteResponse("" + id);
    }
    /**
     * Returns list of FileResources in specified directory wrapped each with CmsFile object.
     * @param filepath path of DirectoryResource
     * @return list of CmsFiles
     */
    public List<CmsFile> getCmsFiles(String dirPath) {     
        final FilesService filesService = getFileService();
        final CoralSession coralSession = getCoralSession();       
        Resource siteRoot = null;        
        ArrayList<CmsFile> files = new ArrayList<CmsFile>();        
        try
        {
            siteRoot = (Resource)filesService.getFilesRoot(coralSession, getSite());
        }
        catch(FilesException e)
        {
            logger.error("Error while retrievin file resource list.", e);
            return files;    
        }        
        
        final Resource[] res = coralSession.getStore().getResourceByPath(siteRoot.getPath() + "/" + dirPath + "/*");
        for(int i=0; i<res.length; i++) {
            if(res[i] instanceof FileResource) {
                files.add(new CmsFile((FileResource)res[i]));
            }
        }
        
        return files;
    } 
    

    /**
     * Modifies existing file from given stream and file details.
     * 
     * @param fid id of FileResource to modify
     * @param uploadedInputStream stream containing new file content
     * @param fileDetail details of new file
     * @return response for servlet
     */
    public Response modifyCmsFileById(long fid, InputStream uploadedInputStream,
        FormDataContentDisposition fileDetail) {
            CmsFile file = getCmsFileById(fid);
            return modifyCmsFile(file, uploadedInputStream, fileDetail);
    }
    
    /**
     * Modifies existing file from given stream and file details.
     * 
     * @param fpath path of FileResource to modify
     * @param uploadedInputStream uploadedInputStream stream containing new file content
     * @param fileDetail fileDetail details of new file
     * @return response for servlet
     */
    public Response modifyCmsFileByPath(String fpath, InputStream uploadedInputStream,
        FormDataContentDisposition fileDetail)
    {
        CmsFile file;
        file = getCmsFile(fpath);
        return modifyCmsFile(file, uploadedInputStream, fileDetail);
    }
    
    private Response modifyCmsFile(CmsFile cmsfile, InputStream uploadedInputStream, FormDataContentDisposition fileDetail)
    {
        FilesService fileService = getFileService();
        long size = fileDetail.getSize();
        FileResource file  = cmsfile.getFileResource();
       
        try
        {
            fileService.replaceFile(file, uploadedInputStream, size);
        }
        catch(IOException e)
        {
            logger.error("Error while modifying file resource.", e);
            return errorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        }
        
        return putResponse(cmsfile);
    }
    
    //Responses
    

    private Response postResponse(CmsFile file) {   
        ResponseBuilder builder = null;
        Response res = Response.noContent().build();
        if(file != null) {
            try
            {
                String link = file.getLink();
                builder = Response.created(new URI(link));
                builder.tag(new EntityTag("{id:" +file.getId()+"}"));
            }
            catch(URISyntaxException e)
            {
                logger.error("Error while generating response.", e);
                res = errorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
        
        if(builder != null) {
            res = builder.build();           
        }
        return res;
    }

    /**
     * @param file
     * @return
     */
    private Response putResponse(CmsFile file) {    
        ResponseBuilder builder = null;
        Response res = Response.noContent().build();
        if(file != null) {
            String link = file.getLink();
            builder = Response.ok();
            builder.tag(new EntityTag("{id:" +file.getId()+"}"));
        }
        
        if(builder != null) {
            res = builder.build();           
        }
        return res;
    }

    private Response deleteResponse(String id)
    {
        Response res = Response.noContent().build();
        if(id != null) {
            ResponseBuilder builder = Response.ok();
            builder.tag(new EntityTag("{id:" +id+"}"));
            res = builder.build();
        }
        return res;
    }
    
    protected Response errorResponse(Response.Status status, String reason)
    {
        ResponseBuilder builder = null;
        Response res = Response.noContent().build();
        
        switch(status) {
            case BAD_REQUEST:
            case UNAUTHORIZED:
            case CONFLICT:
            case UNSUPPORTED_MEDIA_TYPE:
            case INTERNAL_SERVER_ERROR:
            case NOT_FOUND:
                builder = Response.status(status);
                break;
        }
        reason = reason.replace("'", "\'");
        if(reason != null) {
            builder.tag(new EntityTag("{ reason:'" + reason + "'}"));
        }
        if(builder != null) {
            res = builder.build();           
        }
       
        return res;
    }
    
    
    
    //Overridable accessors
    
    private CoralSession getCoralSession()
    {
        final PicoContainer container = (PicoContainer)context.getAttribute(LedgeServletContextListener.CONTAINER_CONTEXT_KEY);
        final Context ledgeContext = (Context)container.getComponentInstance(Context.class);
        return (CoralSession)ledgeContext.getAttribute(CoralSession.class);
    }

    private FilesService getFileService()
    {
        final PicoContainer container = (PicoContainer)context.getAttribute(LedgeServletContextListener.CONTAINER_CONTEXT_KEY);
        final FilesService filesService = (FilesService)container.getComponentInstance(FilesService.class);        
        return filesService;
    }
    
    private FilesTool getFilesTool() {
        final PicoContainer container = (PicoContainer)context.getAttribute(LedgeServletContextListener.CONTAINER_CONTEXT_KEY);
        final FilesTool tool = (FilesTool)((FilesToolFactory)container.getComponentInstance(FilesToolFactory.class)).getTool();        
        return tool;
    }
        
    private SiteService getSiteService() {
        final PicoContainer container = (PicoContainer)context.getAttribute(LedgeServletContextListener.CONTAINER_CONTEXT_KEY);
        final SiteService filesService = (SiteService)container.getComponentInstance(SiteService.class);        
        return filesService;
    }        
    
    public SiteResource getSite()
    {
        return site;
    }

    public void setSite(SiteResource site)
    {
        this.site = site;
    }

    public ServletContext getContext()
    {
        return context;
    }

    public void setContext(ServletContext context)
    {
        this.context = context;
    }  
    
    private class RESTWebException extends WebApplicationException {

        /**
          * Create a HTTP 401 (Unauthorized) exception.
         */
         public RESTWebException(Response.Status status, String desc) {
             super(errorResponse(status, desc));
         }


    }
    
}
