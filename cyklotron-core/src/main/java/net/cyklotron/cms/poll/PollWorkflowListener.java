package net.cyklotron.cms.poll;


import java.util.Calendar;
import java.util.Date;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;

import net.cyklotron.cms.workflow.ProtectedTransitionResource;
import net.cyklotron.cms.workflow.StateChangeListener;
import net.cyklotron.cms.workflow.StatefulResource;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * Poll Workflow Listener implementation
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: PollWorkflowListener.java,v 1.3 2005-02-09 22:20:13 rafal Exp $
 */
public class PollWorkflowListener
    implements StateChangeListener
{
    /** site service */
    private PollService pollService;

    /** logging service */
    private Logger log;

    /** coral session factory */
    protected CoralSessionFactory sessionFactory;

    /** site service */
    private WorkflowService workflowService;

    public PollWorkflowListener(Logger logger, CoralSessionFactory sessionFactory,
        PollService pollService, WorkflowService workflowService)
    {
        this.log = logger;
        this.sessionFactory = sessionFactory;
        this.pollService = pollService;
        this.workflowService = workflowService;
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
        if(resource instanceof PollResource)
        {
            CoralSession coralSession = sessionFactory.getRootSession();
            try
            {
                if(resource.getState().getName().equals("ready"))
                {
                    PollResource poll = (PollResource)resource;
                    Date today = Calendar.getInstance().getTime();
                    if(today.after(poll.getEndDate()))
                    {
                        ProtectedTransitionResource[] transitions = workflowService.getAllowedTransitions(coralSession, resource, coralSession.getUserSubject());
                        ProtectedTransitionResource transition = null;
                        for(int i = 0; i < transitions.length; i++)
                        {
                            if(transitions[i].getName().equals("expire_ready"))
                            {
                                transition = transitions[i];
                                break;
                            }
                        }
                        workflowService.performTransition(coralSession, resource, transition);
                        return;
                    }
                    if(today.after(poll.getStartDate()))
                    {
                        ProtectedTransitionResource[] transitions = workflowService.getAllowedTransitions(coralSession, resource, coralSession.getUserSubject());
                        ProtectedTransitionResource transition = null;
                        for(int i = 0; i < transitions.length; i++)
                        {
                            if(transitions[i].getName().equals("activate"))
                            {
                                transition = transitions[i];
                                break;
                            }
                        }
                        workflowService.performTransition(coralSession, resource, transition);
                        return;
                    }
                }
            }
            catch(WorkflowException e)
            {
                log.error("PollWorkflowListener Exception: ",e);
            }
            finally
            {
                coralSession.close();
            }
        }
    }
}
