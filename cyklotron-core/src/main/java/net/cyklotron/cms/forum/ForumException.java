package net.cyklotron.cms.forum;

import net.labeo.LabeoException;


/**
 * Thrown by new forum service.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ForumException.java,v 1.1 2005-01-12 20:45:07 pablo Exp $
 */
public class ForumException
    extends LabeoException
{
    /**
     * Constructs a new <code>ForumException</code>.
     * 
     * @param message detail message.
     */
    public ForumException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>ForumException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public ForumException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
