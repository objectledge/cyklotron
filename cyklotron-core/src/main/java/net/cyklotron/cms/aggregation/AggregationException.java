package net.cyklotron.cms.aggregation;


/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: AggregationException.java,v 1.2 2005-01-12 21:01:36 pablo Exp $
 */
public class AggregationException extends Exception
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
