package net.cyklotron.cms.search.searching;



/**
 * Thrown by searching facilities 
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchingException.java,v 1.2 2005-01-12 21:08:09 pablo Exp $
 */
public class SearchingException
    extends Exception
{
    /**
     * Constructs a new exception.
     * 
     * @param message detail message.
     */
    public SearchingException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new exception.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public SearchingException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
