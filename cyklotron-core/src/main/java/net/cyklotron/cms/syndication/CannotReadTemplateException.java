package net.cyklotron.cms.syndication;

/**
 * Thrown if the template cannot be open for reading.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CannotReadTemplateException.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class CannotReadTemplateException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new <code>CannotReadTemplateException</code>.
     * 
     * @param message detail message.
     */
    public CannotReadTemplateException(String message)
    {
        super(message);
    }
}
