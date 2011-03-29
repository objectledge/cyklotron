package net.cyklotron.cms.library;

/**
 * Thrown when LibraryService methods are invoked while relevant configuration for the site is
 * missing.
 * 
 * @author rafal
 */
public class NotConfiguredException
    extends Exception
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a new exception instance.
     * 
     * @param message the message.
     */
    public NotConfiguredException(String message)
    {
        super(message);
    }
   
}
