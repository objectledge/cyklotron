package net.cyklotron.cms.structure;

import net.labeo.LabeoException;


/**
 * Thrown by structure service
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: StructureException.java,v 1.1 2005-01-12 20:44:33 pablo Exp $
 */
public class StructureException
    extends LabeoException
{
    /**
     * Constructs a new <code>StructureException</code>.
     * 
     * @param message detail message.
     */
    public StructureException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>StructureException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public StructureException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
