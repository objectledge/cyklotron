package net.cyklotron.cms.rest;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;

import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.files.FilesTool;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.web.LedgeServletContextListener;
import org.picocontainer.PicoContainer;

import com.sun.jersey.core.header.FormDataContentDisposition;

public class FilesProvider {
	
    private SiteResource site = null;
    @javax.ws.rs.core.Context 
    private ServletContext context;    
    
    public FilesProvider() {
    }           
    
    //CRUD
    
    /**
     * @param filepath
     * @return
     * @throws EntityDoesNotExistException
     * @throws FilesException
     */
    public CmsFile getCmsFile(String filepath, String filename) throws EntityDoesNotExistException, FilesException {
        final CoralSession session = getCoralSession();
        return new CmsFile(getFileService().getFileResource(session, filepath + "/" + filename, getSite()), 
            getFilesTool());
    }    
 
    /**
     * @param fpath
     * @param uploadedInputStream
     * @param fileDetail
     * @return
     */
    public Response createCmsFile(String fpath,
            InputStream uploadedInputStream,
            FormDataContentDisposition fileDetail
        ) {
        
        final FilesService filesService = getFileService();
        final CoralSession coralSession = getCoralSession();
        FileResource f = null;        
        DirectoryResource siteRoot = null;
        
        try
        {
            siteRoot = (DirectoryResource)filesService.getFilesRoot(coralSession, getSite());
        }
        catch(FilesException e1)
        {
            e1.printStackTrace();
            return postResponse(null);    
        }
        
        String mimeType = filesService.detectMimeType(uploadedInputStream, fileDetail.getFileName());
        try
        {
            f = getFileService().createFile(
                coralSession, 
                fileDetail.getFileName(), 
                uploadedInputStream, 
                mimeType, 
                null, 
                siteRoot);
        }
        catch(FilesException e)
        {
            e.printStackTrace();
        }
        
        return postResponse(new CmsFile(f, getFilesTool()));    
    }
    
    
    public Response deleteCmsFile(String fpath) {
        return deleteResponse(null);
    }
       
    /**
     * @param filepath
     * @return
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
            e.printStackTrace();
            return null;    
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
     * 
     * 
     * @param fid
     * @param uploadedInputStream
     * @param fileDetail
     * @return
     */
    public Response modifyCmsFile(long fid, InputStream uploadedInputStream,
        FormDataContentDisposition fileDetail) {
            return putResponse(null);
    }
    
    /**
     * 
     * 
     * @param file
     * @return
     */
    public Response modifyCmsFileMeta(CmsFile file) {
            return putResponse(file);
    }
    
    //Responses
    
    /**
     * @param file
     * @return
     */
    private Response postResponse(CmsFile file) {    
        Response res = Response.noContent().build();
        if(file != null) {
            try
            {
                res = Response.created(new URI(file.getLink())).build();
            }
            catch(URISyntaxException e)
            {
                e.printStackTrace();
                res = Response.noContent().build();
            }
        }
        return res;
    }

    /**
     * @param file
     * @return
     */
    private Response putResponse(CmsFile file) {    
        Response res = Response.noContent().build();
        if(file != null) {
            try
            {
                res = Response.created(new URI(file.getLink())).build();
            }
            catch(URISyntaxException e)
            {
                e.printStackTrace();
                res = Response.noContent().build();
            }
        }
        return res;
    }

    private Response deleteResponse(CmsFile file)
    {
        Response res = Response.noContent().build();
        if(file != null) {
            try
            {
                res = Response.created(new URI(file.getLink())).build();
            }
            catch(URISyntaxException e)
            {
                e.printStackTrace();
                res = Response.noContent().build();
            }
        }
        return res;
    }
    
    private Response errorResponse(CmsFile file)
    {
        Response res = Response.noContent().build();
        if(file != null) {
            try
            {
                res = Response.created(new URI(file.getLink())).build();
            }
            catch(URISyntaxException e)
            {
                e.printStackTrace();
                res = Response.noContent().build();
            }
        }
        return res;
    }
    
    
    //Overridable accessors
    
    /**
     * @return
     */
    private CoralSession getCoralSession()
    {
        final PicoContainer container = (PicoContainer)context.getAttribute(LedgeServletContextListener.CONTAINER_CONTEXT_KEY);
        final Context ledgeContext = (Context)container.getComponentInstance(Context.class);
        return (CoralSession)ledgeContext.getAttribute(CoralSession.class);
    }

    /**
     * @return
     */
    private FilesService getFileService()
    {
        final PicoContainer container = (PicoContainer)context.getAttribute(LedgeServletContextListener.CONTAINER_CONTEXT_KEY);
        final FilesService filesService = (FilesService)container.getComponentInstance(FilesService.class);        
        return filesService;
    }
    
    /**
     * @return
     */
    private FilesTool getFilesTool() {
        final PicoContainer container = (PicoContainer)context.getAttribute(LedgeServletContextListener.CONTAINER_CONTEXT_KEY);
        final FilesTool tool = (FilesTool)container.getComponentInstance(FilesTool.class);        
        return tool;
    }
        
    /**
     * @return
     */
    private SiteService getSiteService() {
        final PicoContainer container = (PicoContainer)context.getAttribute(LedgeServletContextListener.CONTAINER_CONTEXT_KEY);
        final SiteService filesService = (SiteService)container.getComponentInstance(SiteService.class);        
        return filesService;
    }
    
    /**
     * @return
     */
    public SiteResource getSite()
    {
        return site;
    }

    /**
     * @param siteName
     */
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
}
