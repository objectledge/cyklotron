package net.cyklotron.cms.category;

import net.labeo.LabeoException;


/**
 * Thrown by category service
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: CategoryException.java,v 1.1 2005-01-12 20:44:28 pablo Exp $
 */
public class CategoryException
    extends LabeoException
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
