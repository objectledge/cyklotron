package net.cyklotron.cms.files;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.upload.FileUpload;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.tools.LinkTool;

import net.cyklotron.cms.site.SiteResource;

/**
 * A context tool used for files application.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: FilesTool.java,v 1.15 2008-04-10 16:45:23 rafal Exp $
 */
public class FilesTool
{
    /** logging service */
    private Logger log;

    /** cms files service */
    private FilesService filesService;
    
    private Context context;
    
    private FileUpload fileUpload;
    
    // initialization ////////////////////////////////////////////////////////

    public FilesTool(Context context, Logger logger, FilesService filesService,
        FileUpload fileUpload)
    {
        this.context = context;
        this.log = logger;
        this.filesService = filesService;
        this.fileUpload = fileUpload;
    }
    
    // public interface ///////////////////////////////////////////////////////
    
    /**
     * Get the main files node for the site
     * 
     * @param site the site resource.
     * @return the root directory.
     */
    public Resource getFilesRoot(SiteResource site)
        throws FilesException
    {
        return filesService.getFilesRoot(getCoralSession(context), site);
    }
    
    /**
     * Returns a file or directory item for the given path.
     * 
     * @param site the site.
     * @param path the pathname.
     * @return the file or directory item, or null if not found.
     * @throws FilesException 
     */
    public Resource getItem(SiteResource site, String path) throws FilesException
    {
        if(path.startsWith("/"))
        {
            path = path.substring(1);
        }
        final CoralSession coralSession = getCoralSession(context);
        final Resource root = filesService.getFilesRoot(coralSession, site);
        final Resource[] res = coralSession.getStore().getResourceByPath(root.getPath()+"/"+path);        
        if(res.length == 1)
        {
            return res[0];
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Checks whether the resource is directory.
     *
     * @param resource the resource.
     * @return <code>true</code> if resource is the directory.
     */
    public boolean isDirectory(Resource resource)
    {
        return (resource instanceof DirectoryResource);
    }

    /**
     * Checks whether the resource is root directory.
     *
     * @param resource the resource.
     * @return <code>true</code> if resource is the root directory.
     */
    public boolean isRootDirectory(Resource resource)
    {
        return (resource instanceof RootDirectoryResource);
    }

    /**
     * Checks whether the resource is ordinary directory.
     *
     * @param resource the resource.
     * @return <code>true</code> if resource is the ordinary directory.
     */
    public boolean isOrdinaryDirectory(Resource resource)
    {
        return (isDirectory(resource) && !isRootDirectory(resource));
    }

    /**
     * Checks whether the resource is files map class.
     *
     * @param resource the resource.
     * @return <code>true</code> if resource is the files map.
     */
    public boolean isFilesMap(Resource resource)
    {
        return (resource instanceof FilesMapResource);
    }

    /**
     * Checks whether resource is file.
     *
     * @param resource the resource.
     * @return <code>true</code> if resource is the file.
     */
    public boolean isFile(Resource resource)
    {
        return (resource instanceof FileResource);
    }

    /**
     * gets the pathlist of all nodes in between two nodes.
     *
     * @param root the root node.
     * @param leaf the leaf.
     * @return list of resources.
     */
    public List getPath(Resource root, Resource leaf)
    {
        List<Resource> list = new ArrayList<Resource>();
        for(Resource parent = leaf; parent!=null; parent = parent.getParent())
        {
            list.add(parent);
            if(parent.equals(root))
            {
                break;
            }
        }
        Collections.reverse(list);
        return list;
    }
    
    /**
     * get the link to the file resource.
     *
     * @param resource the file resource.
     * @return the link to the file.
     */
    public String getLink(Resource resource)
        throws FilesException
    {
        return getLink(resource, false);
    }

    /**
     * get the link to the file resource.
     *
     * @param resource the file resource.
     * @return the link to the file.
     */
    public String getAbsoluteLink(Resource resource)
        throws FilesException
    {
        return getLink(resource, true);
    }


    public String getLink(Resource resource, boolean absolute)
        throws FilesException
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        if(!(resource instanceof FileResource))
        {
            throw new FilesException("Resource is not the instance of cms.files.file class");
        }
        FileResource file = (FileResource)resource;
        RootDirectoryResource rootDirectory = null;
        for(Resource parent = file.getParent(); parent != null; parent = parent.getParent())
        {
            if(parent instanceof RootDirectoryResource)
            {
                rootDirectory = ((RootDirectoryResource)parent);
                break;
            }
        }
        if(rootDirectory.getExternal())
        {
            String path = "";
            for(Resource parent = file; parent != null; parent = parent.getParent())
            {
                if(parent instanceof RootDirectoryResource)
                {
                    break;
                }
                else
                {
                    try
                    {
                        path = "/"+URLEncoder.encode(parent.getName(), "UTF-8")+path;
                        path = path.replace("+", "%20");
                    }
                    catch(UnsupportedEncodingException e)
                    {
                        throw new FilesException("really should not happen", e);
                    }
                }
            }
            //path = URLEncoder.encode(path);
            
            StringBuilder sb = new StringBuilder();
            if(absolute)
            {
                if(!httpContext.getRequest().isSecure())
                {
                    sb.append("http://");
                    sb.append(httpContext.getRequest().getServerName());
                    if(httpContext.getRequest().getServerPort() != 80)
                    {
                        sb.append(":");
                        sb.append(httpContext.getRequest().getServerPort());
                    }                
                }
                else
                {
                    sb.append("https://");
                    sb.append(httpContext.getRequest().getServerName());
                    if(httpContext.getRequest().getServerPort() != 443)
                    {
                        sb.append(":");
                        sb.append(httpContext.getRequest().getServerPort());
                    }                
                }
            }
            sb.append(httpContext.getRequest().getContextPath());
            sb.append("/files/");
            sb.append(rootDirectory.getParent().getParent().getName());
            sb.append("/");
            sb.append(rootDirectory.getName());
            sb.append(path);
            return sb.toString();
        }
        else
        {
            TemplatingContext tContext = (TemplatingContext)
                context.getAttribute(TemplatingContext.class);
            LinkTool link = (LinkTool)tContext.get("link");
            if(absolute)
            {
                link = link.absolute();
            }
            link = link.view("files.Download").pathInfoSuffix(file.getName()).set(
                "file_id", file.getIdString());
            if(absolute)
            {
                link = link.absolute();
            }
            return link.toString();
        }
    }
    
    /**
     * Returns a link for loading resized image.
     * <P>
     * When either w or h parameter is negative, the image will be scaled to fit the other
     * dimension, while preserving image aspect ratio. When both parameters are positive, image will
     * be scaled to fit the dimensions exactly.
     * </p>
     * 
     * @param resource
     * @param w desired width of the image.
     * @param h desired height of the image.
     */
    public String getResized(Resource resource, int w, int h)
        throws FilesException
    {
        if(!(resource instanceof FileResource))
        {
            throw new FilesException("Resource is not the instance of cms.files.file class");
        }
        TemplatingContext tContext = (TemplatingContext)context
            .getAttribute(TemplatingContext.class);
        LinkTool link = (LinkTool)tContext.get("link");
        link = link.view("files.Resized").set("file_id", resource.getId());
        if(w > 0)
        {
            link = link.add("w", w);
        }
        if(h > 0)
        {
            link = link.add("h", h);
        }
        return link.toString();
    }
    
    /**
     * Returns the path of resized image.
     * <P>
     * When either w or h parameter is negative, the image will be scaled to fit the other
     * dimension, while preserving image aspect ratio. When both parameters are positive, image will
     * be scaled using rm method if rm param not set image will be scaled to fit the dimensions exactly. 
     * If crop flag is positive image will be croped to defined dimension. Crop x and y position are parametrized.
     * </p>
     * @param resource file source file.
     * @param w desired width of the image.
     * @param h desired height of the image.
     * @param rm resize method: f - fixed, a - automatic, w - fix_to_width, h - fix_to_height
     * @param crop image crop flag  
     * @param crop_x desired crop x position
     * @param crop_y desired crop x position
     * @return ledge files system path to the resized image.
     * @throws IOException when both dimensions are negative, file is not an image or an IO error
     *         occurs.
     */
    public String getResized(Resource resource, int w, int h, String rm, boolean crop, int crop_x, int crop_y, String file_ext)
        throws FilesException
    {
        if(!(resource instanceof FileResource))
        {
            throw new FilesException("Resource is not the instance of cms.files.file class");
        }
        TemplatingContext tContext = (TemplatingContext)context
            .getAttribute(TemplatingContext.class);
        LinkTool link = (LinkTool)tContext.get("link");
        link = link.view("files.Resized").set("file_id", resource.getId());
        if(w > 0)
        {
            link = link.add("w", w);
        }
        if(h > 0)
        {
            link = link.add("h", h);
        }
        if(rm != null && !"f".equalsIgnoreCase(rm))
        {
            link = link.add("rm", rm.toLowerCase());
        }
        if(crop)
        {
            link = link.add("c", crop);
        }
        if(crop_x > -1)
        {
            link = link.add("c_x", crop_x);
        }
        if(crop_y > -1)
        {
            link = link.add("c_y", crop_y);
        }
        if(file_ext != null)
        {
            link = link.add("f_ext", file_ext);
        }
        return link.toString();
    }

    /**
     * Returns the path of resized image.
     * <P>
     * Return image resized to defined max width and is cropped to defined max height. 
     * </P>
     * 
     * @param resource file source file.
     * @param maxW desired max width of the image.
     * @param maxH desired max height of the image.
     * @return ledge files system path to the resized image.
     * @throws FilesException
     */
    public String getFixedToWidth(Resource resource, int maxW, int maxH)
        throws FilesException
    {
        return getResized(resource, maxW, maxH, "w", true, -1, -1, null);
    }

    /**
     * Returns the path of resized image.
     * <P>
     * Return image resized to defined max height and is cropped to defined max width. 
     * </P>
     * 
     * @param resource file source file.
     * @param maxW desired max width of the image.
     * @param maxH desired max height of the image.
     * @return ledge files system path to the resized image.
     * @throws FilesException
     */
    public String getFixedToHeight(Resource resource, int maxW, int maxH)
        throws FilesException
    {
        return getResized(resource, maxW, maxH, "h", true, -1, -1, null);
    }
    
    private CoralSession getCoralSession(Context context)
    {
        return (CoralSession)context.getAttribute(CoralSession.class);
    }
    
    public String getUploadLimit()
    {
        return ""+fileUpload.getUploadLimit()+" B";
    }
    
    public String getExtension(Resource resource)
    {
        int dot = resource.getName().lastIndexOf('.');
        if(dot < 0)
        {
            return "";
        }
        else
        {
            return resource.getName().substring(dot+1);
        }
    }
}

