package net.cyklotron.cms.link;



/**
 * Thrown by link service.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: LinkException.java,v 1.3 2005-01-18 10:55:45 pablo Exp $
 */
public class LinkException
    extends Exception
{
    /**
     * Constructs a new <code>LinkException</code>.
     * 
     * @param message detail message.
     */
    public LinkException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>LinkException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public LinkException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
