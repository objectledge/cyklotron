package net.cyklotron.cms.link;

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
 * Link Workflow Listener implementation
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: LinkWorkflowListener.java,v 1.4 2005-02-09 22:20:42 rafal Exp $
 */
public class LinkWorkflowListener
    implements StateChangeListener
{
    /** logging service */
    private Logger log;

    /** coral session factory */
    protected CoralSessionFactory sessionFactory;

    /** link service */
    private LinkService linkService;

    /** site service */
    private WorkflowService workflowService;

    public LinkWorkflowListener(Logger logger, CoralSessionFactory sessionFactory,
        LinkService linkService, WorkflowService workflowService)
    {
        this.log = logger;
        this.sessionFactory = sessionFactory;
        this.linkService = linkService;
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
        if(resource instanceof BaseLinkResource)
        {
            CoralSession coralSession = sessionFactory.getRootSession();
            try
            {
                if(resource.getState().getName().equals("ready"))
                {
                    BaseLinkResource link = (BaseLinkResource)resource;
                    Date today = Calendar.getInstance().getTime();
                    if(!link.getEternal() && today.after(link.getEndDate()))
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
                    if(today.after(link.getStartDate()))
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
                log.error("LinkWorkflowListener Exception: ",e);
            }
            finally
            {
                coralSession.close();
            }
        }
    }
}
