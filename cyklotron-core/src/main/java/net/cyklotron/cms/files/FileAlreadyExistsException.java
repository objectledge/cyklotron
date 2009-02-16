package net.cyklotron.cms.files;

/**
 * Thrown by cms files stuff when try to create file or directory which
 * already exists.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FileAlreadyExistsException.java,v 1.1 2005-01-12 20:44:42 pablo Exp $
 */
public class FileAlreadyExistsException
    extends FilesException
{
    /**
     * Constructs a new <code>FileAlreadyExistsException</code>.
     * 
     * @param message detail message.
     */
    public FileAlreadyExistsException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>FileAlreadyExistsException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public FileAlreadyExistsException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
