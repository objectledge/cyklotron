package net.cyklotron.cms.poll;

import net.labeo.LabeoException;


/**
 * Thrown by poll service.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: PollException.java,v 1.1 2005-01-12 20:45:01 pablo Exp $
 */
public class PollException
    extends LabeoException
{
    /**
     * Constructs a new <code>PollException</code>.
     * 
     * @param message detail message.
     */
    public PollException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>PollException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public PollException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
