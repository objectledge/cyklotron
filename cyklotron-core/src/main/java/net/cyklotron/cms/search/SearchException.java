package net.cyklotron.cms.search;

import net.labeo.LabeoException;


/**
 * Thrown by search service 
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: SearchException.java,v 1.1 2005-01-12 20:44:36 pablo Exp $
 */
public class SearchException
    extends LabeoException
{
    /**
     * Constructs a new <code>SearchException</code>.
     * 
     * @param message detail message.
     */
    public SearchException(String message)
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
    public SearchException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
