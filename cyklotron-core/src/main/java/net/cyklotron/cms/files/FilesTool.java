package net.cyklotron.cms.files;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.ServiceBroker;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.pool.RecyclableObject;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ContextTool;
import net.labeo.webcore.LinkTool;
import net.labeo.webcore.RunData;

/**
 * A context tool used for files application.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: FilesTool.java,v 1.1 2005-01-12 20:44:42 pablo Exp $
 */
public class FilesTool
    extends RecyclableObject
    implements ContextTool
{
    /** the rundata for future use */
    private RunData data;

    /** logging service */
    private LoggingFacility log;

    /** resource service */
    private ResourceService resourceService;
    
    /** cms files service */
    private FilesService filesService;
    
    /** initialization flag. */
    private boolean initialized = false;
    
    // initialization ////////////////////////////////////////////////////////

    public void init(ServiceBroker broker, Configuration config)
    {
        if(!initialized)
        {
            log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
                getFacility("cms");
            resourceService = (ResourceService)broker.
                getService(ResourceService.SERVICE_NAME);
            filesService = (FilesService)broker.
                getService(FilesService.SERVICE_NAME);
            initialized = true;
        }
    }
    
    public void prepare(RunData data)
    {
        this.data = data;
    }
        
    public void reset()
    {
        data = null;
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
        return filesService.getFilesRoot(site);
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
        List list = new ArrayList();
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
     * @param file the file resource.
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
     * @param file the file resource.
     * @return the link to the file.
     */
    public String getAbsoluteLink(Resource resource)
        throws FilesException
    {
        return getLink(resource, true);
    }


    private String getLink(Resource resource, boolean absolute)
        throws FilesException
    {
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
                    path = "/"+URLEncoder.encode(parent.getName())+path;
                }
            }
            //path = URLEncoder.encode(path);
            
            StringBuffer sb = new StringBuffer();
            sb.append("http://");
            sb.append(data.getRequest().getServerName());
            if(data.getRequest().getServerPort() != 80)
            {
                sb.append(":");
                sb.append(data.getRequest().getServerPort());
            }
            sb.append(data.getRequest().getContextPath());
            sb.append("/files/");
            sb.append(rootDirectory.getParent().getParent().getName());
            sb.append("/");
            sb.append(rootDirectory.getName());
            sb.append(path);
            return sb.toString();
        }
        else
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
                    path = ","+parent.getName()+path;
                }
            }
            path = "/"+rootDirectory.getName()+path;

            LinkTool link = data.getLinkTool();
            if(absolute)
            {
                link = link.absolute();
            }
            link = link.sessionless().view("files,Download").set("path",path).set("file_id",file.getIdString());
            return link.toString();
        }
    }
}

