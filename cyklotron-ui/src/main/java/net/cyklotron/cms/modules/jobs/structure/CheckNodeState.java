package net.cyklotron.cms.modules.jobs.structure;

import java.util.Calendar;
import java.util.Date;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18n;
import org.objectledge.mail.MailSystem;
import org.objectledge.scheduler.Job;

import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.workflow.ProtectedTransitionResource;
import net.cyklotron.cms.workflow.StateResource;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * A job that checks the start and expire date of the node and switch it's state.
 *
 */
public class CheckNodeState
   extends Job
{
    // instance variables ////////////////////////////////////////////////////

    /** logging facility */
    private Logger log;

    /** workflow service */
    private WorkflowService workflowService;

    /** mail service */
    private MailSystem mailSystem;

    /** i18 service */
    private I18n i18n;

    private CoralSessionFactory sessionFactory;
    
    // initialization ///////////////////////////////////////////////////////

    /**
     *
     */
    public CheckNodeState(Logger logger, WorkflowService workflowService, MailSystem mailSystem,
        I18n i18n, CoralSessionFactory sessionFactory)
    {            
        this.log = logger;
        this.workflowService = workflowService;
        this.mailSystem = mailSystem;
        this.i18n = i18n;
        this.sessionFactory = sessionFactory;
    }
    
    // Job interface ////////////////////////////////////////////////////////
    
    /**
     * Performs the mainteance.
     */
    public void run(String[] args)
    {
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            checkNodes(coralSession);
        }
        finally
        {
            coralSession.close();
        }
    }
    
    private void checkNodes(CoralSession coralSession)
    {
        try
        {
            Date today = Calendar.getInstance().getTime();
            Subject subject = coralSession.getSecurity().getSubject(Subject.ROOT);
            Resource acceptedState = coralSession.getStore()
                .getUniqueResourceByPath("/cms/workflow/automata/structure.navigation_node/states/accepted");
            Resource publishedState = coralSession.getStore()
                .getUniqueResourceByPath("/cms/workflow/automata/structure.navigation_node/states/published");
            QueryResults results = coralSession.getQuery().
                executeQuery("FIND RESOURCE FROM structure.navigation_node WHERE state = "+acceptedState.getIdString());
            
            Resource[] nodes = results.getArray(1);
            for(int i = 0; i < nodes.length; i++)
            {
                checkNode(coralSession, (NavigationNodeResource)nodes[i], today, subject);
            }
            nodes = null;
            results = coralSession.getQuery().
            executeQuery("FIND RESOURCE FROM structure.navigation_node WHERE state = "+publishedState.getIdString());
            Resource[] publishedNodes = results.getArray(1);
            for(int i = 0; i < publishedNodes.length; i++)
            {
                checkNode(coralSession, (NavigationNodeResource)publishedNodes[i], today, subject);
            }
        }
        catch(Exception e)
        {
            log.error("Structure: CheckNodeState Job Exception",e);            
        }
    }
    
    private void checkNode(CoralSession coralSession, NavigationNodeResource node, Date today, Subject subject)
    {
        try
        {
            ProtectedTransitionResource[] transitions = workflowService.getAllowedTransitions(coralSession, node, subject);
            String state = node.getState().getName();
            ProtectedTransitionResource transition = null;

            Date start = node.getValidityStart();
            Date end = node.getValidityEnd();
            
            String targetState = null;
            if(end != null && end.before(today))
            {
                targetState = "expired";
            }
            else
            {
                if(state.equals("accepted") && (start == null || start.before(today)))
                {
                    targetState = "published";
                }
                if(state.equals("published") && start != null && start.after(today))
                {
                    targetState = "accepted";
                }
            }
            if(targetState != null)
            {
                StateResource[] states = workflowService.getStates(coralSession, workflowService.getAutomaton(coralSession, node.getState()),false);
                int i = 0;
                for(;i < states.length; i++)
                {
                    if(states[i].getName().equals(targetState))
                    {
                        node.setState(states[i]);
                        workflowService.enterState(coralSession, node,states[i]);
                        node.update();
                        break;
                    }
                }
                if(i == states.length)
                {
                    log.error("Couldn't find state "+targetState);
                    return;
                }
            }
        }
        catch(WorkflowException e)
        {
            log.error("Structure: CheckNodeState Job Exception",e);
        }
    }
}
