package net.cyklotron.cms.files;



/**
 * Thrown by cms files stuff.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FilesException.java,v 1.2 2005-01-12 21:04:15 pablo Exp $
 */
public class FilesException
    extends Exception
{
    /**
     * Constructs a new <code>FilesException</code>.
     * 
     * @param message detail message.
     */
    public FilesException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>FilesException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public FilesException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
