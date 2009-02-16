package net.cyklotron.cms.syndication;


/**
 * Thrown by outgoing feed manager service when a category query cannot be executed for
 * the outgoing feed.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CannotExecuteQueryException.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class CannotExecuteQueryException extends SyndicationException
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new <code>CannotCreateIncomingFeedsRootException</code>.
     * 
     * @param message detail message.
     */
    public CannotExecuteQueryException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>CannotCreateIncomingFeedsRootException</code>.
     * 
     * @param message detail message.
     * @param e root cause.
     */
    public CannotExecuteQueryException(String message, Throwable e)
    {
        super(message, e);
    }
}
