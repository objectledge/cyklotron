package net.cyklotron.cms.link;

import net.labeo.LabeoException;


/**
 * Thrown by link service.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: LinkException.java,v 1.1 2005-01-12 20:45:17 pablo Exp $
 */
public class LinkException
    extends LabeoException
{
    /**
     * Constructs a new <code>LinkException</code>.
     * 
     * @param message detail message.
     */
    public LinkException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>LinkException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public LinkException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
