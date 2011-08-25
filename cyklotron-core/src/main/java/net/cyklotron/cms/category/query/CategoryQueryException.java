package net.cyklotron.cms.category.query;



/**
 * Thrown by category query service
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryException.java,v 1.3 2005-01-18 17:38:20 pablo Exp $
 */
public class CategoryQueryException
    extends Exception
{
    /**
     * Constructs a new <code>CategoryException</code>.
     * 
     * @param message detail message.
     */
    public CategoryQueryException(String message)
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
    public CategoryQueryException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
