package net.cyklotron.util;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import net.labeo.services.ServiceBroker;
import net.labeo.services.file.table.NameComparator;
import net.labeo.services.logging.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.pool.RecyclableObject;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.resource.Subject;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ContextTool;
import net.labeo.webcore.RunData;

import net.cyklotron.services.workflow.AutomatonResource;
import net.cyklotron.services.workflow.StateResource;
import net.cyklotron.services.workflow.StatefulResource;
import net.cyklotron.services.workflow.TransitionResource;
import net.cyklotron.services.workflow.WorkflowException;
import net.cyklotron.services.workflow.WorkflowService;

/**
 * A context tool used for cms application.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: WorkflowTool.java,v 1.2 2005-01-18 17:38:16 pablo Exp $
 */
public class WorkflowTool
    extends RecyclableObject
    implements ContextTool
{
    /** the rundata for future use */
    private RunData data;

    /** the current subject */
    private Subject subject;

    /** logging service */
    private Logger log;

    /** resource service */
    private CoralSession resourceService;

    /** workflow service */
    private WorkflowService workflowService;

    /** initialization flag. */
    private boolean initialized = false;

    // public interface ///////////////////////////////////////////////////////

    public void init(ServiceBroker broker, Configuration config)
    {
        if(!initialized)
        {
            log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("cms");
            resourceService = (CoralSession)broker.getService(CoralSession.SERVICE_NAME);
            workflowService = (WorkflowService)broker.getService(WorkflowService.SERVICE_NAME);
            initialized = true;
        }
    }

    public void prepare(RunData data)
    {
        this.data = data;
        try
        {
            Principal principal = data.getUserPrincipal();
            if (principal == null)
            {
                subject = null;
            }
            else
            {
                String username = principal.getName();
                subject = resourceService.getSecurity().getSubject(username);
            }
        }
        catch(EntityDoesNotExistException e)
        {
            log.debug("cannot prepare workflow tool",e);
            subject = null;
        }
    }    
    
    public void reset()
    {
        data = null;
    }

    /**
     * Return current logged subject.
     *
     * @return the subject.
     */
    public Subject getSubject()
    {
        return subject;
    }

    public AutomatonResource getAutomaton(TransitionResource resource)
        throws WorkflowException
    {
        return workflowService.getAutomaton(resource);
    }

    public AutomatonResource getAutomaton(StateResource resource)
        throws WorkflowException
    {
        return workflowService.getAutomaton(resource);
    }


    public List getAllowedTransitions(Resource resource)
        throws WorkflowException
    {
        return Arrays.asList(workflowService.getAllowedTransitions((StatefulResource)resource, subject));
    }

    public List getAllowedTransitions(Resource resource, Locale locale)
        throws WorkflowException
    {
        List list = getAllowedTransitions(resource);
        NameComparator nc = new NameComparator(locale);
        Collections.sort(list,nc);
        return list;
    }
}

