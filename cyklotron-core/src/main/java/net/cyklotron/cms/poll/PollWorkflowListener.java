package net.cyklotron.cms.poll;


import java.util.Calendar;
import java.util.Date;

import net.labeo.Labeo;
import net.labeo.services.InitializationError;
import net.labeo.services.ServiceBroker;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;

import net.cyklotron.services.workflow.ProtectedTransitionResource;
import net.cyklotron.services.workflow.StateChangeListener;
import net.cyklotron.services.workflow.StatefulResource;
import net.cyklotron.services.workflow.WorkflowException;
import net.cyklotron.services.workflow.WorkflowService;

/**
 * Poll Workflow Listener implementation
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: PollWorkflowListener.java,v 1.1 2005-01-12 20:45:01 pablo Exp $
 */
public class PollWorkflowListener
    implements StateChangeListener
{
    /** service broker */
    private ServiceBroker broker;

    /** logging service */
    private LoggingFacility log;

    /** resource service */
    private ResourceService resourceService;

    /** site service */
    private PollService pollService;

    /** site service */
    private WorkflowService workflowService;

    /** system subject */
    private Subject subject;

    /** init switch */
    private boolean initialized;

    public PollWorkflowListener()
    {
        broker = Labeo.getBroker();
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(PollService.LOGGING_FACILITY);
        initialized = false;
    }

    private synchronized void init()
    {
        if(!initialized)
        {
            resourceService = (ResourceService)broker.getService(ResourceService.SERVICE_NAME);
            pollService = (PollService)broker.getService(PollService.SERVICE_NAME);
            workflowService = (WorkflowService)broker.getService(WorkflowService.SERVICE_NAME);
            try
            {
                subject = resourceService.getSecurity().getSubject(Subject.ROOT);
            }
            catch(EntityDoesNotExistException e)
            {
                throw new InitializationError("Couldn't find root subject");
            }
            initialized = true;
        }
    }

    //  --------------------       listeners implementation
    /**
     * Notifies the apropriate subjects about the resource state change.
     *
     * @param role the subjects in this role should be notified.
     * @param resource the resource that changed state.
     */
    public void stateChanged(Role role, StatefulResource resource)
    {
        init();
        if(resource instanceof PollResource)
        {
            try
            {
                if(resource.getState().getName().equals("ready"))
                {
                    PollResource poll = (PollResource)resource;
                    Date today = Calendar.getInstance().getTime();
                    if(today.after(poll.getEndDate()))
                    {
                        ProtectedTransitionResource[] transitions = workflowService.getAllowedTransitions(resource, subject);
                        ProtectedTransitionResource transition = null;
                        for(int i = 0; i < transitions.length; i++)
                        {
                            if(transitions[i].getName().equals("expire_ready"))
                            {
                                transition = transitions[i];
                                break;
                            }
                        }
                        workflowService.performTransition(resource, transition, subject);
                        return;
                    }
                    if(today.after(poll.getStartDate()))
                    {
                        ProtectedTransitionResource[] transitions = workflowService.getAllowedTransitions(resource, subject);
                        ProtectedTransitionResource transition = null;
                        for(int i = 0; i < transitions.length; i++)
                        {
                            if(transitions[i].getName().equals("activate"))
                            {
                                transition = transitions[i];
                                break;
                            }
                        }
                        workflowService.performTransition(resource, transition, subject);
                        return;
                    }
                }
            }
            catch(WorkflowException e)
            {
                log.error("PollWorkflowListener Exception: ",e);
            }
        }
    }
}
