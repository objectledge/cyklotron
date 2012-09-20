package net.cyklotron.cms.rest;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.bind.JAXBException;

import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FileAlreadyExistsException;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.files.FilesTool;
import net.cyklotron.cms.files.FilesToolFactory;
import net.cyklotron.cms.files.ItemResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
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
    public CmsFile getCmsFile(String filepath) throws EntityDoesNotExistException, FilesException {
        final CoralSession session = getCoralSession();
        return new CmsFile(getFileService().getFileResource(session, filepath, getSite()), 
            getFilesTool());
    }    
 
    /**
     * @param fpath
     * @param uploadedInputStream
     * @param fileDetail
     * @return
     * @throws FilesException 
     */
    public Response createCmsFile(String fpath,
            InputStream uploadedInputStream,
            FormDataContentDisposition fileDetail
        ) throws FilesException {
        
        
        final FilesService filesService = getFileService();
        final CoralSession coralSession = getCoralSession();
        final SiteResource site = getSite();
        FileResource f = null;             
        
        if(fpath.length() == 0) {
            throw new FilesException("Empty file name.");
        }
        final String[] tokens = fpath.split("/");
        String fname = tokens[tokens.length-1];
        
        if(uploadedInputStream == null) {
            return errorResponse(Response.Status.BAD_REQUEST, "Missing file content");
        }
        String mimeType = filesService.detectMimeType(uploadedInputStream, fileDetail.getFileName());
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
            e1.printStackTrace();
            return errorResponse(Response.Status.CONFLICT, e1.getMessage());
        }
        catch(FilesException e)
        {
            e.printStackTrace();
            return errorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        }
        catch(InvalidResourceNameException e)
        {
            e.printStackTrace();
            return errorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        
        return postResponse(new CmsFile(f, getFilesTool()));    
    }
//    protected void deleteSiteNode(CoralSession coralSession, Resource node)
//                    throws Exception
//                {
//                    Resource[] children = coralSession.getStore().getResource(node);
//                    for(Resource child: children)
//                    {
//                        deleteSiteNode(coralSession, child);
//                    }
//                    coralSession.getStore().deleteResource(node);
//                }
    
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
                e.printStackTrace();
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
        Response res = Response.noContent().build();
        if(file != null) {
            try
            {
                res = Response.created(new URI(file.getLink())).build();
            }
            catch(URISyntaxException e)
            {
                e.printStackTrace();
                res = errorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
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
                res = errorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
            }
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
        final FilesTool tool = (FilesTool)((FilesToolFactory)container.getComponentInstance(FilesToolFactory.class)).getTool();        
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
