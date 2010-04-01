package net.cyklotron.cms.files;

import java.io.InputStream;
import java.io.OutputStream;

import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;

import net.cyklotron.cms.site.SiteResource;

/**
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: FilesService.java,v 1.6 2006-01-02 10:41:39 rafal Exp $
 */
public interface FilesService
{
    /** The name of the service (<code>"cms_files"</code>). */
    public final static String SERVICE_NAME = "cms_files";

    /**
     * The logging facility where the service issues it's informational
     * messages.
     */
    public static final String LOGGING_FACILITY = "cms_files";

    /**
     * return the files root node.
     *
     * @param site the site resource.
     * @return the files root resource.
     * @throws FilesException if the operation fails.
     */
    public FilesMapResource getFilesRoot(CoralSession coralSession, SiteResource site)
        throws FilesException;

    /**
     * Return the files administrator.
     * 
     * @param site the site resource.
     * @return the files adminstrator role.
     * @throws FilesException if the operation fails.
     */
    public Role getFilesAdministrator(CoralSession coralSession,SiteResource site)
        throws FilesException;
    
    /**
     * Create the root directory in site.
     *
     * @param site the site resource.
     * @param name the name of the directory.
     * @param external the type of the link to the resource.
     * @param path the base path to the parent directory in file system or
     * <code>null</code> if default.
     * @return the files root resource.
     * @throws FilesException if the operation fails.
     */
    public RootDirectoryResource createRootDirectory(CoralSession coralSession, SiteResource site, String name, boolean external, 
                                                     String path)
        throws FilesException;
    
    /**
     * Create the directory.
     *
     * @param name the name of the directory.
     * @param parent the parent directory.
     * @return the created directory.
     * @throws FilesException if the operation fails.
     */
    public DirectoryResource createDirectory(CoralSession coralSession, String name, DirectoryResource parent)
        throws FilesException;
    
    /**
     * Create the file.
     *
     * @param name the name of the file.
     * @param is the InputStream with file data, or <code>null</code> to create an empty file.
     * @param mimetype the mimetype of the file.
     * @param encoding the encoding of the file, or null if unknown.
     * @param parent the parent directory.
     * @return the created file.
     * @throws FilesException if the operation fails.
     */
    public FileResource createFile(CoralSession coralSession, String name, InputStream is, String mimetype, String encoding,
                                   DirectoryResource parent)
        throws FilesException;

    /**
     * Unpack the zip file.
     *
     * @param is the InputStream with file data, or <code>null</code> to create an empty file.
     * @param encoding the encoding of the file, or null if unknown.
     * @param parent the parent directory.
     * @throws FilesException if the operation fails.
     */
    public void unpackZipFile(CoralSession coralSession, InputStream is, String encoding,
                 			  DirectoryResource parent)
    	throws FilesException;
    
    /**
     * Copy the file.
     *
     * @param source the source file.
     * @param name the name of the new file.
     * @param parent the parent directory.
     * @return the copied file.
     * @throws FilesException if the operation fails.
     */
    public FileResource copyFile(CoralSession coralSession, FileResource source, String name, DirectoryResource parent)
        throws FilesException;

    /**
     * Delete the directory.
     *
     * @param directory the directory to delete.
     * @throws FilesException if the operation fails.
     */
    public void deleteDirectory(CoralSession coralSession, DirectoryResource directory)
        throws FilesException;
    
    /**
     * Delete the file.
     *
     * @param file the file to delete.
     * @throws FilesException if the operation fails.
     */
    public void deleteFile(CoralSession coralSession, FileResource file)
        throws FilesException;
    
    /**
     * Get the file input stream.
     *
     * @param file the file.
     * @return the input stream.
     */
    public InputStream getInputStream(FileResource file);

    /**
     * Get the file output stream.
     *
     * @param file the file.
     * @return the output stream.
     */
    public OutputStream getOutputStream(FileResource file);
    
    /**
     * Get the file last modified time.
     *
     * @param file the file.
     * @return the last modified time.
     */
    public long lastModified(FileResource file);
    
    /** 
     * Verify the name of the file.
     * 
     * @param name the name of the file.
     * @return <code>true</code> if accepted.
     */
    public boolean isValid(String name);
    
	/** 
	 * Convert name to accepted format.
	 * 
	 * @param name the name of the file.
	 * @return the converted name.
	 */
	public String convertName(String name);
    
    /**
     * Extracts text content from the file for the purpose of indexing (search).
     * 
     * @param file the file to be parsed.
     * @return extracted text content. If file format is not supported empty string will be returned.
     */
    public String extractContent(FileResource file);
    
    /**
     * Get the path of the item (file or directory).
     * 
     * @param item
     *            the item.
     * @return the path to the directory.
     */
    public String getPath(ItemResource item);
    
    /**
     * Get the site the given item (file or directory) belongs to.
     * 
     * @param file the file.
     */
    public SiteResource getSite(ItemResource item);
}
