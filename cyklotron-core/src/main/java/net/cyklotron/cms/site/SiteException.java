package net.cyklotron.cms.site;

import net.labeo.LabeoException;

/**
 * Thrown when a requested operation on a site fails.
 *
 * @version $Id: SiteException.java,v 1.1 2005-01-12 20:44:43 pablo Exp $
 * @author <a href="mailto:rkrzewsk@ngo.pl">Rafal Krzewski</a>
 */
public class SiteException
    extends LabeoException
{
    /**
     * Constructs a new <code>SiteException</code>.
     * 
     * @param message detail message.
     */
    public SiteException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>SiteException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public SiteException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
