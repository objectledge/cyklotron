package net.cyklotron.cms.files.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.mail.MailSystem;

/**
 * Implementation of Files Service
 * 
 * @author <a href="mailto:publo@caltha.pl">Pawel Potempski </a>
 * @version $Id: FilesServiceImpl.java,v 1.2 2005-01-18 10:06:41 pablo Exp $
 */
public class FilesServiceImpl
    implements FilesService
{
    // instance variables ////////////////////////////////////////////////////

    /** logging facility */
    private Logger log;

    /** site serive */
    private SiteService siteService;

    /** file service */
    private FileSystem fileSystem;

    /** mail service */
    private MailSystem mailSystem;

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
    public FilesServiceImpl(Configuration config, Logger logger, 
        SiteService siteService, FileSystem fileSystem,
        MailSystem mailSystem, ContentExtractorPlugin[] plugins)
    {
        this.log = logger;
        this.siteService = siteService;
        this.fileSystem = fileSystem;
        this.mailSystem = mailSystem;
        
        defaultPublicPath = config.getChild("default_public_path").getValue("/files");
        defaultProtectedPath = config.getChild("default_protected_path").getValue("/data/files");
        pluginsMap = new HashMap();
        for(int i = 0; i < plugins.length; i++)
        {
            String[] mimeTypeNames = plugins[i].getMimetypes();
            for(int j = 0; j < mimeTypeNames.length; j++)
            pluginsMap.put(mimeTypeNames[j], plugins[i]);
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
    public FilesMapResource getFilesRoot(CoralSession coralSession, SiteResource site) throws FilesException
    {
        Resource[] roots = coralSession.getStore().getResource(site, "files");
        if(roots.length == 1)
        {
            return (FilesMapResource)roots[0];
        }
        if(roots.length == 0)
        {
            try
            {
                return FilesMapResourceImpl.createFilesMapResource(coralSession, "files", site);
            }
            catch(Exception e)
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
    public Role getFilesAdministrator(CoralSession coralSession, SiteResource site) throws FilesException
    {
        return getFilesRoot(coralSession, site).getAdministrator();
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
    public RootDirectoryResource createRootDirectory(CoralSession coralSession, SiteResource site, String name,
        boolean external, String path) throws FilesException
    {
        try
        {
            FilesMapResource parent = getFilesRoot(coralSession, site);
            Resource[] resources = coralSession.getStore().getResource(parent, name);
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
            fileSystem.mkdirs(basePath);
            RootDirectoryResource directory = RootDirectoryResourceImpl
                .createRootDirectoryResource(coralSession, name, parent);
            directory.setRootPath(basePath);
            directory.setExternal(external);
            directory.update();
            return directory;
        }
        catch(Exception e)
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
    public DirectoryResource createDirectory(CoralSession coralSession, String name, DirectoryResource parent)
        throws FilesException
    {
        Resource[] resources = coralSession.getStore().getResource(parent, name);
        if(resources.length > 0)
        {
            throw new FileAlreadyExistsException("The directory '" + name
                + "' already exists in directory");
        }
        try
        {
            String path = getPath(parent) + "/" + name;
            fileSystem.mkdirs(path);
            DirectoryResource directory = DirectoryResourceImpl.createDirectoryResource(
                coralSession, name, parent);
            return directory;
        }
        catch(Exception e)
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
    public FileResource createFile(CoralSession coralSession, String name, InputStream is, String mimetype, String encoding,
        DirectoryResource parent) throws FilesException
    {
        Resource[] resources = coralSession.getStore().getResource(parent, name);
        if(resources.length > 0)
        {
            throw new FileAlreadyExistsException("The file '" + name
                + "' already exists in directory");
        }
        try
        {
            String path = getPath(parent) + "/" + name;
            boolean notExists = fileSystem.createNewFile(path);
            if(!notExists)
            {
                throw new FilesException("The file '" + name
                    + "' already exists in directory but the resource is missed");
            }
            if(is != null)
            {
                fileSystem.write(path, is);
            }
            FileResource file = FileResourceImpl.createFileResource(coralSession, name, parent);
            file.setSize(fileSystem.length(path));
            if(mimetype == null || mimetype.equals("")
                || mimetype.equals("application/octet-stream"))
            {
                mimetype = mailSystem.getContentType(name);
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
            file.update();
            return file;
        }
        catch(Exception e)
        {
            throw new FilesException("Exception occured during file upload '" + name + "' ", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void unpackZipFile(CoralSession coralSession, InputStream is, String encoding,
        					  DirectoryResource parent)
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
                                dirParent = createDirectory(coralSession, dirName, dirParent);
                            }
                            catch(FileAlreadyExistsException e)
                            {
                                dirParent = (DirectoryResource)coralSession.
                            		getStore().getUniqueResource(dirParent, dirName);
                            }
                        }
                    }
                    createFile(coralSession, name, zis, null, encoding, dirParent);                    
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
    public FileResource copyFile(CoralSession coralSession, FileResource source, String name, DirectoryResource parent) throws FilesException
    {
        Resource[] resources = coralSession.getStore().getResource(parent, name);
        if(resources.length > 0)
        {
            throw new FileAlreadyExistsException("The file '" + name
                + "' already exists in directory");
        }
        try
        {
            String path = getPath(parent) + "/" + name;
            boolean notExists = fileSystem.createNewFile(path);
            if(!notExists)
            {
                throw new FilesException("The file '" + name
                    + "' already exists in directory but the resource is missed");
            }
            OutputStream os = fileSystem.getOutputStream(path);
            String sourcePath = getPath(source);
            InputStream is = fileSystem.getInputStream(sourcePath);
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
            FileResource file = FileResourceImpl.createFileResource(coralSession, name, parent);
            file.setSize(fileSystem.length(path));
            file.setMimetype(source.getMimetype());
            file.update();
            return file;
        }
        catch(Exception e)
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
    public void deleteDirectory(CoralSession coralSession, DirectoryResource directory) throws FilesException
    {
        Resource[] resources = coralSession.getStore().getResource(directory);
        if(resources.length > 0)
        {
            throw new DirectoryNotEmptyException("The file '" + directory.getName()
                + "' is not empty");
        }
        String path = getPath(directory);
        try
        {
            coralSession.getStore().deleteResource(directory);
            fileSystem.delete(path);
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
    public void deleteFile(CoralSession coralSession, FileResource file) throws FilesException
    {
        String path = getPath(file);
        try
        {
            coralSession.getStore().deleteResource(file);
            fileSystem.delete(path);
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
        return fileSystem.getInputStream(path);
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
        return fileSystem.getOutputStream(path);
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
        return fileSystem.lastModified(path);
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

