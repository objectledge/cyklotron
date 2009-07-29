package net.cyklotron.cms.workflow;

import java.util.Collection;

import org.objectledge.table.TableFilter;

/**
 * TableFilter implementation for filtering stateful resources according to their state.
 * 
 * @since 1.0.3
 * @author rafal
 */
public class InverseStateFilter
    implements TableFilter<StatefulResource>
{
    private final Collection<StateResource> rejectedStates;

    private final boolean acceptUndefinedState;

    /**
     * Creates a new filter instance.
     * 
     * @param rejectedStates collection of states that should be rejected by the filter.
     * @param acceptUndefinedState should undefined state be accepted by the filter.
     */
    public InverseStateFilter(Collection<StateResource> rejectedStates,
        boolean acceptUndefinedState)
    {
        this.rejectedStates = rejectedStates;
        this.acceptUndefinedState = acceptUndefinedState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(StatefulResource resource)
    {
        StateResource state = resource.getState();
        return state == null && acceptUndefinedState || !rejectedStates.contains(state);
    }
}
