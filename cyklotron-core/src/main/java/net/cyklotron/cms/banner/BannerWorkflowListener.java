package net.cyklotron.cms.banner;

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
 * Link Workflow Listener implementation
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BannerWorkflowListener.java,v 1.1 2005-01-12 20:45:04 pablo Exp $
 */
public class BannerWorkflowListener
    implements StateChangeListener
{
    /** service broker */
    private ServiceBroker broker;

    /** logging service */
    private LoggingFacility log;

    /** resource service */
    private ResourceService resourceService;

    /** link service */
    private BannerService bannerService;

    /** site service */
    private WorkflowService workflowService;

    /** system subject */
    private Subject subject;

    /** init switch */
    private boolean initialized;

    public BannerWorkflowListener()
    {
        broker = Labeo.getBroker();
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(BannerService.LOGGING_FACILITY);
        initialized = false;
    }

    private synchronized void init()
    {
        if(!initialized)
        {
            resourceService = (ResourceService)broker.getService(ResourceService.SERVICE_NAME);
            bannerService = (BannerService)broker.getService(BannerService.SERVICE_NAME);
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
        if(resource instanceof BannerResource)
        {
            try
            {
                if(resource.getState().getName().equals("ready"))
                {
                    BannerResource poll = (BannerResource)resource;
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
                log.error("BannerWorkflowListener Exception: ",e);
            }
        }
    }
}
