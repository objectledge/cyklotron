package net.cyklotron.cms.workflow;

import java.util.Collection;

import org.objectledge.table.TableFilter;

/**
 * TableFilter implementation for filtering stateful resources according to their state.
 * 
 * @since 1.0.3
 * @author rafal
 */
public class StateFilter
    implements TableFilter<StatefulResource>
{
    private final Collection<StateResource> acceptedStates;

    private final boolean acceptUndefinedState;

    /**
     * Creates a new filter instance.
     * 
     * @param acceptedStates collection of states that should be accepted by the filter.
     * @param acceptUndefinedState should undefined state be accepted by the filter.
     */
    public StateFilter(Collection<StateResource> acceptedStates,
        boolean acceptUndefinedState)
    {
        this.acceptedStates = acceptedStates;
        this.acceptUndefinedState = acceptUndefinedState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(StatefulResource resource)
    {
        StateResource state = resource.getState();
        return state == null && acceptUndefinedState || acceptedStates.contains(state);
    }
}
