package net.cyklotron.cms.syndication;


/**
 * Thrown by incoming feed manager service when a incoming feeds subapp root resource cannot be
 * created for the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CannotCreateFeedsRootException.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class CannotCreateFeedsRootException extends SyndicationException
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new <code>CannotCreateIncomingFeedsRootException</code>.
     * 
     * @param message detail message.
     */
    public CannotCreateFeedsRootException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>CannotCreateIncomingFeedsRootException</code>.
     * 
     * @param message detail message.
     * @param e root cause.
     */
    public CannotCreateFeedsRootException(String message, Throwable e)
    {
        super(message, e);
    }
}
