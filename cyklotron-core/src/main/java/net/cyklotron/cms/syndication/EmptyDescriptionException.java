package net.cyklotron.cms.syndication;

/**
 * Thrown in case of a giving a null name for a feed.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EmptyDescriptionException.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class EmptyDescriptionException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new <code>EmptyFeedNameException</code>.
     */
    public EmptyDescriptionException()
    {
    }
}
