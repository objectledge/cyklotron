package net.cyklotron.cms.files.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.labeo.services.BaseService;
import net.labeo.services.ConfigurationError;
import net.labeo.services.file.FileService;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.mail.MailService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.generic.CrossReference;
import net.labeo.util.configuration.Configuration;

import net.cyklotron.cms.files.DirectoryNotEmptyException;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.DirectoryResourceImpl;
import net.cyklotron.cms.files.FileAlreadyExistsException;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FileResourceImpl;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesMapResource;
import net.cyklotron.cms.files.FilesMapResourceImpl;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.files.ItemResource;
import net.cyklotron.cms.files.RootDirectoryResource;
import net.cyklotron.cms.files.RootDirectoryResourceImpl;
import net.cyklotron.cms.files.plugins.ContentExtractorPlugin;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * Implementation of Files Service
 * 
 * @author <a href="mailto:publo@caltha.pl">Pawel Potempski </a>
 * @version $Id: FilesServiceImpl.java,v 1.1 2005-01-12 20:45:14 pablo Exp $
 */
public class FilesServiceImpl extends BaseService implements FilesService
{
    // instance variables ////////////////////////////////////////////////////

    /** logging facility */
    private LoggingFacility log;

    /** resource service */
    private ResourceService resourceService;

    /** site serive */
    private SiteService siteService;

    /** file service */
    private FileService fileService;

    /** mail service */
    private MailService mailService;

    /** system subject */
    private Subject rootSubject;

    /** protected (internal) default base path */
    private String defaultProtectedPath;

    /** public (external) default base path */
    private String defaultPublicPath;

    /** mimetype plugins */
    private Map pluginsMap;

    // initialization ////////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public void init()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME))
            .getFacility(LOGGING_FACILITY);
        resourceService = (ResourceService)broker.getService(ResourceService.SERVICE_NAME);
        siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
        fileService = (FileService)broker.getService(FileService.SERVICE_NAME);
        mailService = (MailService)broker.getService(MailService.SERVICE_NAME);
        try
        {
            rootSubject = resourceService.getSecurity().getSubject(Subject.ROOT);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ConfigurationError("Couldn't find system subject");
        }
        defaultPublicPath = config.get("default_public_path").asString("/files");
        defaultProtectedPath = config.get("default_protected_path").asString("/data/files");

        Configuration pluginsConfiguration = config.getSubset("plugins.");
        String[] pluginNames = pluginsConfiguration.getSubsetNames();
        pluginsMap = new HashMap();
        if(pluginNames != null)
        {
            for (int i = 0; i < pluginNames.length; i++)
            {
                String className = pluginsConfiguration.get(pluginNames[i]).asString("");
                try
                {
                    Class pluginClass = Class.forName(className);
                    Object plugin = (ContentExtractorPlugin)pluginClass.newInstance();
                    pluginsMap.put(pluginNames[i], plugin);
                }
                catch(ClassNotFoundException e)
                {
                    throw new ConfigurationError(
                        "Plugin adding failed - probably couldn't find class '" + className
                            + "' for mimetype: " + pluginNames[i]);
                }
                catch(InstantiationException e)
                {
                    throw new ConfigurationError("Instantiation Exception: " + pluginNames[i]);
                }
                catch(IllegalAccessException e)
                {
                    throw new ConfigurationError("Illegal access: " + pluginNames[i]);
                }
            }
        }
    }

    /**
     * Return the files root node.
     * 
     * @param site
     *            the site resource.
     * @return the files root resource.
     * @throws FilesException.
     */
    public FilesMapResource getFilesRoot(SiteResource site) throws FilesException
    {
        Resource[] roots = resourceService.getStore().getResource(site, "files");
        if(roots.length == 1)
        {
            return (FilesMapResource)roots[0];
        }
        if(roots.length == 0)
        {
            try
            {
                return FilesMapResourceImpl.createFilesMapResource(resourceService, "files", site,
                    new CrossReference(), rootSubject);
            }
            catch(ValueRequiredException e)
            {
                throw new FilesException("Couldn't create files root node");
            }
        }
        throw new FilesException("Too much files root resources for site: " + site.getName());
    }

    /**
     * Return the files administrator.
     * 
     * @param site
     *            the site resource.
     * @return the files adminstrator role.
     * @throws FilesException.
     */
    public Role getFilesAdministrator(SiteResource site) throws FilesException
    {
        return getFilesRoot(site).getAdministrator();
    }

    /**
     * Create the root directory in site.
     * 
     * @param site
     *            the site resource.
     * @param name
     *            the name of the directory.
     * @param external
     *            the type of the link to the resource.
     * @param path
     *            the base path to the parent directory in file system or <code>null</code> if
     *            default.
     * @param description
     *            the description.
     * @param creator
     *            the creator.
     * @return the files root resource.
     * @throws FilesException.
     */
    public RootDirectoryResource createRootDirectory(SiteResource site, String name,
        boolean external, String path, Subject creator) throws FilesException
    {
        try
        {
            FilesMapResource parent = getFilesRoot(site);
            Resource[] resources = resourceService.getStore().getResource(parent, name);
            if(resources.length > 0)
            {
                throw new FileAlreadyExistsException("The directory '" + name
                    + "' already exists in site '" + site.getName() + "'");
            }
            String basePath = null;
            if(path != null)
            {
                basePath = path;
            }
            else
            {
                if(external)
                {
                    basePath = defaultPublicPath;
                }
                else
                {
                    basePath = defaultProtectedPath;
                }
            }
            basePath = basePath + "/" + site.getName() + "/" + name;
            fileService.mkdirs(basePath);
            RootDirectoryResource directory = RootDirectoryResourceImpl
                .createRootDirectoryResource(resourceService, name, parent, creator);
            directory.setRootPath(basePath);
            directory.setExternal(external);
            directory.update(creator);
            return directory;
        }
        catch(ValueRequiredException e)
        {
            throw new FilesException("Exception occured during creating the directory '" + name
                + "' in site '" + site.getName() + "' ", e);
        }
        catch(IOException e)
        {
            throw new FilesException("Exception occured during creating the directory '" + name
                + "' in site '" + site.getName() + "' ", e);
        }
    }

    /**
     * Create the directory.
     * 
     * @param name
     *            the name of the directory.
     * @param parent
     *            the parent directory.
     * @param creator
     *            the creator.
     * @return the created directory.
     * @throws FilesException.
     */
    public DirectoryResource createDirectory(String name, DirectoryResource parent, Subject creator)
        throws FilesException
    {
        Resource[] resources = resourceService.getStore().getResource(parent, name);
        if(resources.length > 0)
        {
            throw new FileAlreadyExistsException("The directory '" + name
                + "' already exists in directory");
        }
        try
        {
            String path = getPath(parent) + "/" + name;
            fileService.mkdirs(path);
            DirectoryResource directory = DirectoryResourceImpl.createDirectoryResource(
                resourceService, name, parent, creator);
            return directory;
        }
        catch(ValueRequiredException e)
        {
            throw new FilesException("Exception occured during creating the directory '" + name, e);
        }
        catch(IOException e)
        {
            throw new FilesException("Exception occured during creating the directory '" + name, e);
        }
    }

    /**
     * Create the file.
     * 
     * @param name
     *            the name of the file.
     * @param is
     *            the InputStream with file data, or <code>null</code> to create an empty file.
     * @param mimetype
     *            the mimetype of the file.
     * @param encoding
     *            the encoding of the file, or null if unknown.
     * @param parent
     *            the parent directory.
     * @param creator
     *            the creator.
     * @return the created file.
     * @throws FilesException.
     */
    public FileResource createFile(String name, InputStream is, String mimetype, String encoding,
        DirectoryResource parent, Subject creator) throws FilesException
    {
        Resource[] resources = resourceService.getStore().getResource(parent, name);
        if(resources.length > 0)
        {
            throw new FileAlreadyExistsException("The file '" + name
                + "' already exists in directory");
        }
        try
        {
            String path = getPath(parent) + "/" + name;
            boolean notExists = fileService.createNewFile(path);
            if(!notExists)
            {
                throw new FilesException("The file '" + name
                    + "' already exists in directory but the resource is missed");
            }
            if(is != null)
            {
                fileService.write(path, is);
            }
            FileResource file = FileResourceImpl.createFileResource(resourceService, name, parent,
                creator);
            file.setSize(fileService.length(path));
            if(mimetype == null || mimetype.equals("")
                || mimetype.equals("application/octet-stream"))
            {
                mimetype = mailService.getContentType(name);
            }
            if(encoding != null && mimetype.startsWith("text/") && mimetype.indexOf("charset") < 0)
            {
                mimetype = mimetype + ";charset=" + encoding;
            }
            file.setMimetype(mimetype);
            if(encoding != null)
            {
                file.setEncoding(encoding);
            }
            file.update(creator);
            return file;
        }
        catch(ValueRequiredException e)
        {
            throw new FilesException("Exception occured during file upload '" + name + "' ", e);
        }
        catch(IOException e)
        {
            throw new FilesException("Exception occured during file upload '" + name + "' ", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void unpackZipFile(InputStream is, String encoding,
        					  DirectoryResource parent, Subject creator)
    	throws FilesException
    {
        try
        {
            String basePath = getPath(parent)+"/";
            ZipInputStream zis = new ZipInputStream(is);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null)
            {
                if(!ze.isDirectory())
                {
                    String path = ze.getName();
                    String parentPath = "";
                    String name = ze.getName();
                    int last = path.lastIndexOf('/');
                    if(last != -1)
                    {
                        name = path.substring(last+1);
                        parentPath = path.substring(0, last);
                    }
                    DirectoryResource dirParent = parent;
                    if(parentPath.length() > 0)
                    {                    
                        StringTokenizer st = new StringTokenizer(parentPath,"/");
                        while(st.hasMoreTokens())
                        {
                            String dirName = st.nextToken();
                            try
                            {
                                dirParent = createDirectory(dirName, dirParent, creator);
                            }
                            catch(FileAlreadyExistsException e)
                            {
                                dirParent = (DirectoryResource)resourceService.
                            		getStore().getUniqueResource(dirParent, dirName);
                            }
                        }
                    }
                    createFile(name, zis, null, encoding, dirParent, creator);                    
                    zis.closeEntry();
                }
                ze = zis.getNextEntry();
            }
        }
        catch(Exception e)
        {
            throw new FilesException("Exception occured during file upload with unzip", e);
        }
    }
    /**
     * Copy the file.
     * 
     * @param source
     *            the source file.
     * @param name
     *            the name of the new file.
     * @param parent
     *            the parent directory.
     * @param subject
     *            the subject.
     * @return the copied file.
     * @throws FilesException.
     */
    public FileResource copyFile(FileResource source, String name, DirectoryResource parent,
        Subject subject) throws FilesException
    {
        Resource[] resources = resourceService.getStore().getResource(parent, name);
        if(resources.length > 0)
        {
            throw new FileAlreadyExistsException("The file '" + name
                + "' already exists in directory");
        }
        try
        {
            String path = getPath(parent) + "/" + name;
            boolean notExists = fileService.createNewFile(path);
            if(!notExists)
            {
                throw new FilesException("The file '" + name
                    + "' already exists in directory but the resource is missed");
            }
            OutputStream os = fileService.getOutputStream(path);
            String sourcePath = getPath(source);
            InputStream is = fileService.getInputStream(sourcePath);
            int data = -1;
            while(true)
            {
                data = is.read();
                if(data != -1)
                {
                    os.write(data);
                }
                else
                {
                    os.close();
                    break;
                }
            }
            FileResource file = FileResourceImpl.createFileResource(resourceService, name, parent,
                subject);
            file.setSize(fileService.length(path));
            file.setMimetype(source.getMimetype());
            file.update(subject);
            return file;
        }
        catch(ValueRequiredException e)
        {
            throw new FilesException("Exception occured during file copying '" + name + "' ", e);
        }
        catch(IOException e)
        {
            throw new FilesException("Exception occured during file copying '" + name + "' ", e);
        }
    }

    /**
     * Delete the directory.
     * 
     * @param directory
     *            the directory to delete.
     * @param subject
     *            the subject.
     * @throws FilesException.
     */
    public void deleteDirectory(DirectoryResource directory, Subject subject) throws FilesException
    {
        Resource[] resources = resourceService.getStore().getResource(directory);
        if(resources.length > 0)
        {
            throw new DirectoryNotEmptyException("The file '" + directory.getName()
                + "' is not empty");
        }
        String path = getPath(directory);
        try
        {
            resourceService.getStore().deleteResource(directory);
            fileService.delete(path);
        }
        catch(EntityInUseException e)
        {
            throw new FilesException("Exception occured during deleting the directory resource '"
                + directory.getName() + "' ", e);
        }
        catch(IOException e)
        {
            throw new FilesException("Exception occured during deleting the directory '" + path
                + "' ", e);
        }
    }

    /**
     * Delete the file.
     * 
     * @param file
     *            the file to delete.
     * @param subject
     *            the subject.
     * @throws FilesException.
     */
    public void deleteFile(FileResource file, Subject subject) throws FilesException
    {
        String path = getPath(file);
        try
        {
            resourceService.getStore().deleteResource(file);
            fileService.delete(path);
        }
        catch(EntityInUseException e)
        {
            throw new FilesException("Exception occured during deleting the file resource '"
                + file.getName() + "' ", e);
        }
        catch(IOException e)
        {
            throw new FilesException("Exception occured during deleting the file '" + path + "' ",
                e);
        }
    }

    /**
     * Get the file input stream.
     * 
     * @param file
     *            the file.
     * @return the input stream.
     */
    public InputStream getInputStream(FileResource file)
    {
        String path = getPath(file);
        return fileService.getInputStream(path);
    }

    /**
     * Get the file output stream.
     * 
     * @param file
     *            the file.
     * @return the output stream.
     */
    public OutputStream getOutputStream(FileResource file)
    {
        String path = getPath(file);
        return fileService.getOutputStream(path);
    }

    /**
     * Get the file last modified time.
     * 
     * @param file
     *            the file.
     * @return the last modified time.
     */
    public long lastModified(FileResource file)
    {
        String path = getPath(file);
        return fileService.lastModified(path);
    }

    /**
     * Verify the name of the file.
     * 
     * @param name
     *            the name of the file.
     * @return <code>true</code> if accepted.
     */
    public boolean isValid(String name)
    {
        char[] chars = name.toCharArray();
        for (int i = 0; i < chars.length; i++)
        {
            int ch = (int)chars[i];
            if(ch == 45 || ch == 46 || ch == 95 || (ch >= 48 && ch <= 57)
                || (ch >= 97 && ch <= 122) || (ch >= 65 && ch <= 90))
            {
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     * Convert name to accepted format.
     * 
     * @param name
     *            the name of the file.
     * @return the converted name.
     */
    public String convertName(String name)
    {
        return name;
    }

    /**
     * Get the content extractor for file.
     * 
     * @param mimetype
     *            the mimetype.
     * @return the extractor class for given mimetype or <code>null</code> if extactor is not
     *         registerd.
     */
    public ContentExtractorPlugin getExtractor(String mimetype)
    {
        return (ContentExtractorPlugin)pluginsMap.get(mimetype);
    }

    /**
     * Get the path of the item (file or directory).
     * 
     * @param item
     *            the item.
     * @return the path to the directory.
     */
    private String getPath(ItemResource item)
    {
        String path = "";
        for (Resource parent = item; parent != null; parent = parent.getParent())
        {
            if(parent instanceof RootDirectoryResource)
            {
                path = ((RootDirectoryResource)parent).getRootPath() + path;
                break;
            }
            else
            {
                path = "/" + parent.getName() + path;
            }
        }
        return path;
    }
}

