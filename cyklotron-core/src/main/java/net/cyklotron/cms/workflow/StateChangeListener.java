package net.cyklotron.cms.workflow;

import org.objectledge.coral.security.Role;

public interface StateChangeListener
{
    /**
     * Notifies the apropriate subjects about the resource state change.
     *
     * @param role the subjects in this role should be notified.
     * @param resource the resource that changed state.
     */
    public void stateChanged(Role role, StatefulResource resource);
}
