package net.cyklotron.cms.files;

/**
 * Thrown by cms files stuff when try to create file or directory which
 * already exists.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: DirectoryNotEmptyException.java,v 1.1 2005-01-12 20:44:42 pablo Exp $
 */
public class DirectoryNotEmptyException
    extends FilesException
{
    /**
     * Constructs a new <code>DirectoryNotEmptyException</code>.
     * 
     * @param message detail message.
     */
    public DirectoryNotEmptyException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>DirectoryNotEmptyException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public DirectoryNotEmptyException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
