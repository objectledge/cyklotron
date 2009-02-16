package net.cyklotron.cms.security;



/**
 * Thrown by cms security service 
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CmsSecurityException.java,v 1.2 2005-01-12 21:08:06 pablo Exp $
 */
public class CmsSecurityException extends Exception
{
    /**
     * Constructs a new <code>SearchException</code>.
     * 
     * @param message detail message.
     */
    public CmsSecurityException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>SearchException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public CmsSecurityException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
