package net.cyklotron.cms.periodicals;

import net.labeo.LabeoException;

/**
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: PeriodicalsException.java,v 1.1 2005-01-12 20:45:08 pablo Exp $
 */
public class PeriodicalsException extends LabeoException
{
    /**
     * Constructs a new <code>PeriodicalsException</code>.
     * 
     * @param message detail message.
     */
    public PeriodicalsException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>PeriodicalsException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public PeriodicalsException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }

    /**
     * Constructs a new <code>PeriodicalsException</code>.
     * 
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public PeriodicalsException(Throwable rootCause)
    {
        super(rootCause);
    }
}
