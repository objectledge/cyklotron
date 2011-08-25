package net.cyklotron.cms.syndication;

/**
 * Thrown by syndication facilities..
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SyndicationException.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public abstract class SyndicationException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new <code>SyndicationException</code>.
     * 
     * @param message detail message.
     */
    public SyndicationException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>SyndicationException</code>.
     * 
     * @param message detail message.
     * @param e root cause.
     */
    public SyndicationException(String message, Throwable e)
    {
        super(message, e);
    }
}
