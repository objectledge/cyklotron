package net.cyklotron.cms;


/**
 * Thrown by Cms application when the data fails to initialize.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: CmsError.java,v 1.3 2005-01-19 12:42:45 pablo Exp $
 */
public class CmsError
    extends Error
{
    /**
     * Constructs a new <code>CmsError</code>.
     * 
     * @param message detail message.
     */
    public CmsError(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>CmsError</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public CmsError(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
