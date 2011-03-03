package net.cyklotron.cms.structure;

/**
 * Thrown by structure service when user tries to create a node with the same name as one of its
 * future sibling nodes.
 *
 * @author <a href="mailo:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: NavigationNodeAlreadyExistException.java,v 1.1 2005-01-12 20:44:33 pablo Exp $
 */
public class NavigationNodeAlreadyExistException extends StructureException
{
    /**
     * Constructs a new <code>NavigationNodeAlreadyExistException</code>.
     * 
     * @param message detail message.
     */
    public NavigationNodeAlreadyExistException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>NavigationNodeAlreadyExistException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public NavigationNodeAlreadyExistException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
