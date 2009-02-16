package net.cyklotron.cms.syndication;

/**
 * Thrown in case of a giving a null url for a feed.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EmptyUrlException.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class EmptyUrlException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new <code>EmptyUrlException</code>.
     */
    public EmptyUrlException()
    {
    }
}
