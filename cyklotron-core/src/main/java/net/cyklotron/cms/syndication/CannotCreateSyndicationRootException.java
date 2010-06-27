package net.cyklotron.cms.syndication;


/**
 * Thrown by syndication service when a syndication app root resource cannot be created
 * for the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CannotCreateSyndicationRootException.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class CannotCreateSyndicationRootException extends SyndicationException
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new <code>CannotCreateSyndicationRootException</code>.
     * 
     * @param message detail message.
     */
    public CannotCreateSyndicationRootException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>CannotCreateSyndicationRootException</code>.
     * 
     * @param message detail message.
     * @param e root cause.
     */
    public CannotCreateSyndicationRootException(String message, Throwable e)
    {
        super(message, e);
    }
}
