package net.cyklotron.cms.category.query;



/**
 * Thrown by category query service
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryException.java,v 1.2 2005-01-13 11:46:26 pablo Exp $
 */
public class CategoryQueryException
    extends LabeoException
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
