package net.cyklotron.cms.structure;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.objectledge.coral.security.Subject;

/**
 * Locked by session listener.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: LockedBySessionListener.java,v 1.3 2005-02-10 17:46:32 rafal Exp $
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
        // ignored
    }
    
    public void valueUnbound(HttpSessionBindingEvent event)
    {
        if(subject.equals(node.getLockedBy()))
        {
            node.setLockedBy(null);
            node.update();
        }
    }
}
