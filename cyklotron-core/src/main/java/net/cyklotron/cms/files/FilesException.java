package net.cyklotron.cms.files;

import net.labeo.LabeoException;


/**
 * Thrown by cms files stuff.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FilesException.java,v 1.1 2005-01-12 20:44:42 pablo Exp $
 */
public class FilesException
    extends LabeoException
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
