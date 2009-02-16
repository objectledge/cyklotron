package net.cyklotron.cms.workflow;



/**
 * Thrown by style service
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: WorkflowException.java,v 1.1 2005-01-18 08:19:07 pablo Exp $
 */
public class WorkflowException
    extends Exception
{
    /**
     * Constructs a new <code>StyleException</code>.
     * 
     * @param message detail message.
     */
    public WorkflowException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>StyleException</code>.
     * 
     * @param message detail message.
     * @param rootCause the exception that was caught but could not be 
     * handled. 
     */
    public WorkflowException(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }
}
