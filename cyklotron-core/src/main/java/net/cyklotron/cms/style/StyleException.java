package net.cyklotron.cms.style;

import net.labeo.LabeoException;


/**
 * Thrown by style service
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: StyleException.java,v 1.1 2005-01-12 20:45:06 pablo Exp $
 */
public class StyleException
    extends LabeoException
{
    /**
     * Constructs a new <code>StyleException</code>.
     * 
     * @param message detail message.
     */
    public StyleException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>StyleException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public StyleException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
