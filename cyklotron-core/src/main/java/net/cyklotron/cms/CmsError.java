package net.cyklotron.cms;


/**
 * Thrown by Cms application when the data fails to initialize.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawe� Potempski</a>
 * @version $Id: CmsError.java,v 1.2 2005-01-13 11:46:23 pablo Exp $
 */
public class CmsError
    extends LabeoError
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
