package net.cyklotron.cms.documents;


/**
 * Thrown by document service
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DocumentException.java,v 1.2 2005-01-12 21:02:42 pablo Exp $
 */
public class DocumentException extends Exception
{
    /**
     * Constructs a new <code>DocumentException</code>.
     * 
     * @param message detail message.
     */
    public DocumentException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>DocumentException</code>..
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public DocumentException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
