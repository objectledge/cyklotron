package net.cyklotron.cms.documents;

import net.labeo.LabeoException;

/**
 * Thrown by html service
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HTMLException.java,v 1.1 2005-01-12 20:44:39 pablo Exp $
 */
public class HTMLException extends LabeoException
{
    /**
     * Constructs a new <code>HTMLException</code>.
     * 
     * @param message detail message.
     */
    public HTMLException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>HTMLException</code>..
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public HTMLException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
