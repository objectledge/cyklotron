package net.cyklotron.cms.httpfeed;


/**
 * Thrown by http feed service
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HttpFeedException.java,v 1.2 2005-01-12 21:04:14 pablo Exp $
 */
public class HttpFeedException extends Exception
{
    /**
     * Constructs a new <code>HttpFeedException</code>.
     * 
     * @param message detail message.
     */
    public HttpFeedException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>HttpFeedException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public HttpFeedException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
