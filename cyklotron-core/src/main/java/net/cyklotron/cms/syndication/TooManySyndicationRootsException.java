package net.cyklotron.cms.syndication;

/**
 * Thrown by syndication service when a site has more than one syndication app root.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TooManySyndicationRootsException.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class TooManySyndicationRootsException extends SyndicationException
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new <code>TooManySyndicationRootsException</code>.
     * 
     * @param message detail message.
     */
    public TooManySyndicationRootsException(String message)
    {
        super(message);
    }
}
