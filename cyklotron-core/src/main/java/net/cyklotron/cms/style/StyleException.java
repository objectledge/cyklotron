package net.cyklotron.cms.style;



/**
 * Thrown by style service
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: StyleException.java,v 1.2 2005-01-12 20:58:49 pablo Exp $
 */
public class StyleException
    extends Exception
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
