package net.cyklotron.cms.banner;

import net.labeo.LabeoException;


/**
 * Thrown by banner stuff.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BannerException.java,v 1.1 2005-01-12 20:45:04 pablo Exp $
 */
public class BannerException
    extends LabeoException
{
    /**
     * Constructs a new <code>BannerException</code>.
     * 
     * @param message detail message.
     */
    public BannerException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>BannerException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public BannerException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
