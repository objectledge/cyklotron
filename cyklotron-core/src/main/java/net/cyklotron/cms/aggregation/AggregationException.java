package net.cyklotron.cms.aggregation;

import net.labeo.LabeoException;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: AggregationException.java,v 1.1 2005-01-12 20:44:57 pablo Exp $
 */
public class AggregationException extends LabeoException
{
    /**
     * Constructs a new <code>AggregationException</code>.
     * 
     * @param message detail message.
     */
    public AggregationException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>AggregationException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public AggregationException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }

    /**
     * Constructs a new <code>AggregationException</code>.
     * 
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public AggregationException(Throwable rootCause)
    {
        super(rootCause);
    }
}
