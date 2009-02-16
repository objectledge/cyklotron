package net.cyklotron.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.NameComparator;

import net.cyklotron.cms.workflow.AutomatonResource;
import net.cyklotron.cms.workflow.StateResource;
import net.cyklotron.cms.workflow.StatefulResource;
import net.cyklotron.cms.workflow.TransitionResource;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * A context tool used for cms application.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: WorkflowTool.java,v 1.4 2005-02-09 22:20:33 rafal Exp $
 */
public class WorkflowTool
{
    private Context context;
    
    /** logging service */
    private Logger log;

    /** workflow service */
    private WorkflowService workflowService;


    // public interface ///////////////////////////////////////////////////////

    public WorkflowTool(Context context, Logger logger, WorkflowService workflowService)
    {
        this.context = context;
        this.log = logger;
        this.workflowService = workflowService;
    }

    /**
     * Return current logged subject.
     *
     * @return the subject.
     */
    public Subject getSubject()
    {
        return getCoralSession().getUserSubject();
    }

    public AutomatonResource getAutomaton(TransitionResource resource)
        throws WorkflowException
    {
        return workflowService.getAutomaton(getCoralSession(),resource);
    }

    public AutomatonResource getAutomaton(StateResource resource)
        throws WorkflowException
    {
        return workflowService.getAutomaton(getCoralSession(),resource);
    }


    public List getAllowedTransitions(Resource resource)
        throws WorkflowException
    {
        return Arrays.asList(workflowService.getAllowedTransitions(getCoralSession(),(StatefulResource)resource, getSubject()));
    }

    public List getAllowedTransitions(Resource resource, Locale locale)
        throws WorkflowException
    {
        List list = getAllowedTransitions(resource);
        NameComparator nc = new NameComparator(locale);
        Collections.sort(list,nc);
        return list;
    }
    
    protected CoralSession getCoralSession()
    {
        return (CoralSession)context.getAttribute(CoralSession.class);
    }
}

