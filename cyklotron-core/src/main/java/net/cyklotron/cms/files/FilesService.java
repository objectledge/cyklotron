package net.cyklotron.cms.files;

import java.io.InputStream;
import java.io.OutputStream;

import net.labeo.services.Service;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;

import net.cyklotron.cms.files.plugins.ContentExtractorPlugin;
import net.cyklotron.cms.site.SiteResource;

/**
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: FilesService.java,v 1.1 2005-01-12 20:44:42 pablo Exp $
 */
public interface FilesService
    extends Service
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
     * @throws FilesException.
     */
    public FilesMapResource getFilesRoot(SiteResource site)
        throws FilesException;

    /**
     * Return the files administrator.
     * 
     * @param site the site resource.
     * @return the files adminstrator role.
     * @throws FilesException.
     */
    public Role getFilesAdministrator(SiteResource site)
        throws FilesException;
    
    /**
     * Create the root directory in site.
     *
     * @param site the site resource.
     * @param name the name of the directory.
     * @param external the type of the link to the resource.
     * @param path the base path to the parent directory in file system or
     * <code>null</code> if default.
     * @param creator the creator.
     * @return the files root resource.
     * @throws FilesException.
     */
    public RootDirectoryResource createRootDirectory(SiteResource site, String name, boolean external, 
                                                     String path, Subject creator)
        throws FilesException;
    
    /**
     * Create the directory.
     *
     * @param name the name of the directory.
     * @param parent the parent directory.
     * @param creator the creator.
     * @return the created directory.
     * @throws FilesException.
     */
    public DirectoryResource createDirectory(String name, DirectoryResource parent, Subject creator)
        throws FilesException;
    
    /**
     * Create the file.
     *
     * @param name the name of the file.
     * @param is the InputStream with file data, or <code>null</code> to create an empty file.
     * @param mimetype the mimetype of the file.
     * @param encoding the encoding of the file, or null if unknown.
     * @param parent the parent directory.
     * @param creator the creator.
     * @return the created file.
     * @throws FilesException.
     */
    public FileResource createFile(String name, InputStream is, String mimetype, String encoding,
                                   DirectoryResource parent, Subject creator)
        throws FilesException;

    /**
     * Unpack the zip file.
     *
     * @param is the InputStream with file data, or <code>null</code> to create an empty file.
     * @param encoding the encoding of the file, or null if unknown.
     * @param parent the parent directory.
     * @param creator the creator.
     * @throws FilesException.
     */
    public void unpackZipFile(InputStream is, String encoding,
                 			  DirectoryResource parent, Subject creator)
    	throws FilesException;
    
    /**
     * Copy the file.
     *
     * @param source the source file.
     * @param name the name of the new file.
     * @param parent the parent directory.
     * @param subject the subject.
     * @return the copied file.
     * @throws FilesException.
     */
    public FileResource copyFile(FileResource source, String name, DirectoryResource parent, Subject subject)
        throws FilesException;

    /**
     * Delete the directory.
     *
     * @param directory the directory to delete.
     * @param subject the subject.
     * @throws FilesException.
     */
    public void deleteDirectory(DirectoryResource directory, Subject subject)
        throws FilesException;
    
    /**
     * Delete the file.
     *
     * @param file the file to delete.
     * @param subject the subject.
     * @throws FilesException.
     */
    public void deleteFile(FileResource file, Subject subject)
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
     * Get the content extractor for file.
     *
     * @param mimetype the mimetype.
     * @return the extractor class for given mimetype or <code>null</code> if
     * extactor is not registerd.
     */
    public ContentExtractorPlugin getExtractor(String mimetype);

}
