package net.cyklotron.cms.link;



/**
 * Thrown by link service.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: LinkException.java,v 1.2 2005-01-13 11:46:40 pablo Exp $
 */
public class LinkException
    extends LabeoException
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
