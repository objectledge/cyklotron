package net.cyklotron.cms.skins;

import net.labeo.LabeoException;

/**
 * Thrown by skin service
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: SkinException.java,v 1.1 2005-01-12 04:41:38 pablo Exp $
 */
public class SkinException
    extends LabeoException
{
    /**
     * Constructs a new <code>SkinException</code>.
     * 
     * @param message detail message.
     */
    public SkinException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>SkinException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public SkinException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
