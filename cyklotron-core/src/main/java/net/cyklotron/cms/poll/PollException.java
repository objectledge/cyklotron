package net.cyklotron.cms.poll;



/**
 * Thrown by poll service.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: PollException.java,v 1.2 2005-01-12 21:08:12 pablo Exp $
 */
public class PollException
    extends Exception
{
    /**
     * Constructs a new <code>PollException</code>.
     * 
     * @param message detail message.
     */
    public PollException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>PollException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public PollException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
