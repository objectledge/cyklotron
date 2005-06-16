package net.cyklotron.cms.syndication;


/**
 * Thrown by outgoing feed manager service when a the outgoing feed cannot be generated.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CannotGenerateFeedException.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class CannotGenerateFeedException extends SyndicationException
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new <code>CannotGenerateFeedException</code>.
     * 
     * @param message detail message.
     */
    public CannotGenerateFeedException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>CannotGenerateFeedException</code>.
     * 
     * @param message detail message.
     * @param e root cause.
     */
    public CannotGenerateFeedException(String message, Throwable e)
    {
        super(message, e);
    }
}
