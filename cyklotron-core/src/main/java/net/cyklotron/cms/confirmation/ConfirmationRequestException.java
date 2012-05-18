package net.cyklotron.cms.confirmation;


/**
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: PeriodicalsException.java,v 1.2 2005-01-12 21:08:07 pablo Exp $
 */
public class ConfirmationRequestException extends Exception
{
    /**
     * Constructs a new <code>ConfirmationRequestException</code>.
     * 
     * @param message detail message.
     */
    public ConfirmationRequestException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>ConfirmationRequestException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public ConfirmationRequestException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }

    /**
     * Constructs a new <code>ConfirmationRequestException</code>.
     * 
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public ConfirmationRequestException(Throwable rootCause)
    {
        super(rootCause);
    }
}
