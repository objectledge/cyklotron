package net.cyklotron.cms.security;

import net.labeo.LabeoException;


/**
 * Thrown by cms security service 
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CmsSecurityException.java,v 1.1 2005-01-12 20:44:49 pablo Exp $
 */
public class CmsSecurityException extends LabeoException
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
