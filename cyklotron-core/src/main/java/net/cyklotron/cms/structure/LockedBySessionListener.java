package net.cyklotron.cms.structure;

import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.services.workflow.StatefulResource;
import net.cyklotron.services.workflow.TransitionResource;
import net.cyklotron.services.workflow.WorkflowException;

/**
 * Locked by session listener.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: LockedBySessionListener.java,v 1.1 2005-01-12 20:44:33 pablo Exp $
 */
public class LockedBySessionListener
    implements HttpSessionBindingListener
{
    private NavigationNodeResource node;

    private Subject subject;
    
    public LockedBySessionListener(NavigationNodeResource node, Subject subject)
    {
        this.node = node;
        this.subject = subject;
    }
    
    public void valueBound(HttpSessionBindingEvent event)
    {
    }
    
    public void valueUnbound(HttpSessionBindingEvent event)
    {
        if(subject.equals(node.getLockedBy()))
        {
            node.setLockedBy(null);
            node.update(subject);
        }
    }
}
