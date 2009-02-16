package net.cyklotron.cms.syndication;

/**
 * Thrown in case of problems with feed creation.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: FeedCreationException.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class FeedCreationException extends SyndicationException
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new <code>FeedCreationException</code>.
     * 
     * @param message detail message.
     */
    public FeedCreationException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>FeedCreationException</code>.
     * 
     * @param message detail message.
     * @param e root cause.
     */
    public FeedCreationException(String message, Throwable e)
    {
        super(message, e);
    }
}
