package net.cyklotron.cms.structure;



/**
 * Thrown by structure service
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: StructureException.java,v 1.2 2005-01-12 20:58:26 pablo Exp $
 */
public class StructureException
    extends Exception
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
