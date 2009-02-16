package net.cyklotron.cms.syndication;

/**
 * Thrown if the temaple type cannot be recognized.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UnsupportedTemplateTypeException.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class UnsupportedTemplateTypeException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new <code>UnsupportedTemplateTypeException</code>.
     * 
     * @param message detail message.
     */
    public UnsupportedTemplateTypeException(String message)
    {
        super(message);
    }
}
