package net.cyklotron.cms.search;



/**
 * Thrown by search service 
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: SearchException.java,v 1.2 2005-01-12 21:08:10 pablo Exp $
 */
public class SearchException
    extends Exception
{
    /**
     * Constructs a new <code>SearchException</code>.
     * 
     * @param message detail message.
     */
    public SearchException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>SearchException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public SearchException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
