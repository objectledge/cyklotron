package net.cyklotron.cms.syndication;

/**
 * Thrown by incoming feeds manager when a site has more than one incoming feeds subapp root.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TooManyFeedsRootsException.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class TooManyFeedsRootsException extends SyndicationException
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new <code>SyndicationException</code>.
     * 
     * @param message detail message.
     */
    public TooManyFeedsRootsException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>SyndicationException</code>.
     * 
     * @param message detail message.
     * @param e root cause.
     */
    public TooManyFeedsRootsException(String message, Throwable e)
    {
        super(message, e);
    }
}
