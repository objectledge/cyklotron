package net.cyklotron.cms.search.searching;

import net.labeo.LabeoException;


/**
 * Thrown by searching facilities 
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchingException.java,v 1.1 2005-01-12 20:44:40 pablo Exp $
 */
public class SearchingException
    extends LabeoException
{
    /**
     * Constructs a new exception.
     * 
     * @param message detail message.
     */
    public SearchingException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new exception.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public SearchingException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
