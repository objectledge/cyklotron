package net.cyklotron.cms.category;



/**
 * Thrown by category service
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: CategoryException.java,v 1.2 2005-01-12 21:01:34 pablo Exp $
 */
public class CategoryException
    extends Exception
{
    /**
     * Constructs a new <code>CategoryException</code>.
     * 
     * @param message detail message.
     */
    public CategoryException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>CategoryException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public CategoryException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
