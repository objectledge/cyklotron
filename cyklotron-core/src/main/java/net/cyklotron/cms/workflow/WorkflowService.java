package net.cyklotron.cms.workflow;

import java.util.HashMap;
import java.util.Map;

import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;



public interface WorkflowService
{
    // constants /////////////////////////////////////////////////////////////
    
    /** The service name */
    public static final String SERVICE_NAME = "workflow";

    /** The logging facility where the service issues it's informational
     * messages. */
    public static final String LOGGING_FACILITY = "workflow";
    
    // automata //////////////////////////////////////////////////////////////

    /**
     * Returns the root resource the automaton is defined for.
     *
     * @param automaton the automaton.
     * @return the resource, or <code>null</code> for globally defined automata.
     */
    public Resource getRoot(CoralSession coralSession, AutomatonResource automaton)
        throws WorkflowException;

    /**
     * Returns the defined automata.
     *
     * @param resource the resource to return automata for. <code>null</code> for
     *        globally defined automata.
     * @return an array of automata objects.
     */
    public AutomatonResource[] getAutomata(CoralSession coralSession, Resource resource)
        throws WorkflowException;

    /**
     * Creates a new automaton.
     *
     * @param resource the resource to return automata in. <code>null</code> to
     *        define the automaton globally.
     * @param name the name of the automaton.
     * @param resourceClass the class of resources this automaton applies to,
     *        must be a subclass of <code>workflow.stateful</code>
     * @param primary is this automaton primary for the resource class.
     * @param subject the subject that performs the operation.
     * @return the newly created automaton.
     * @throws InvalidResourceNameException if the name attribute contains illegal characters.
     */
    public AutomatonResource createAutomaton(CoralSession coralSession, Resource resource,
        String name, ResourceClass resourceClass, boolean primary, Subject subject)
        throws WorkflowException, InvalidResourceNameException;
    
    /**
     * Deletes an autmaton.
     * 
     * @param automaton the automaton to delete.
     * @param subject the subject that performs the operation.
     */
    public void deleteAutomaton(CoralSession coralSession,AutomatonResource automaton, Subject subject)
        throws EntityInUseException, WorkflowException;
    
    /**
     * Deems an automaton to be primary for it's resource class.
     *
     * @param automaton the automaton.
     * @param subject the subject that performs the operation.
     */
    public void makeAutomatonPrimary(CoralSession coralSession,AutomatonResource automaton, Subject subject)
        throws WorkflowException;

    /**
     * Returns a primary automaton for a resource class.
     *
     * <p>If the site does not contain any automata for the resource class,
     * a fallback to the globally defined automata will be performed.<p>
     * <p>If there are automata assigend to the resource class, but none of
     * them mareked as primary for the resource class, exception will be
     * thrown.</p> 
     *
     * @param resource the resource where the automaton is to be searched for.
     * @param resourceClass the resource class.
     */
    public AutomatonResource getPrimaryAutomaton(CoralSession coralSession,Resource resource, 
                                                ResourceClass resourceClass)
        throws WorkflowException;

    /**
     * Returns the automaton the state belongs to.
     *
     * @param state the state.
     * @return the automaton.
     */
    public AutomatonResource getAutomaton(CoralSession coralSession,StateResource state)
        throws WorkflowException;

    /**
     * Returns the states of an automaton.
     *
     * @param automaton the automaton.
     * @param initial <code>true</code> to return valid initial states only,
     *        <code>false</code> to return all states.
     */
    public StateResource[] getStates(CoralSession coralSession,AutomatonResource automaton, 
                                     boolean initial)
        throws WorkflowException;

	/**
	 * Returns the states of an automaton.
	 *
	 * @param automaton the automaton.
	 * @param state the name of the state.
	 */
	public StateResource getState(CoralSession coralSession,AutomatonResource automaton, String state)
		throws WorkflowException;

    /**
     * Creates a new state of an automaton
     *
     * @param automaton the automaton to modify.
     * @param name the name of the state.
     * @param initial <code>true</code> if the state is a valid initial state.
     * @param asignee the role that should be informed about resources entering
     *        this state.
     * @param subject the subject that performs the operation.
     * @throws InvalidResourceNameException if the name attribute contains illegal characters.
     */
    public StateResource createState(CoralSession coralSession, AutomatonResource automaton,
        String name, boolean initial, Role asignee, Subject subject)
        throws WorkflowException, InvalidResourceNameException;
    
    /**
     * Deletes a state of an automaton.
     *
     * @param state the state.
     * @param subject the subject that performs the operation.
     */
    public void deleteState(CoralSession coralSession,StateResource state, Subject subject)
        throws EntityInUseException, WorkflowException;
    
    /**
     * Returns the automaton the transition belongs to.
     *
     * @param transition the transition.
     * @return the automaton.
     */
    public AutomatonResource getAutomaton(CoralSession coralSession, TransitionResource transition)
        throws WorkflowException;

    /**
     * Returns transitions defined in an automaton.
     *
     * @param automaton the automaton.
     * @return transitions defined for a state.
     */
    public TransitionResource[] getTransitions(CoralSession coralSession,AutomatonResource automaton)
        throws WorkflowException;

    /**
     * Returns transitions defined for a state.
     *
     * @param state the state.
     * @return transitions defined for a state.
     */
    public TransitionResource[] getTransitions(CoralSession coralSession,StateResource state)
        throws WorkflowException;
    
    /**
     * Returns ProtectedTransitions that the specified subject can perform.
     *
     * @param resource the resource in question.
     * @param subject the subject.
     * @return protected transitions that the subject is allowed to perform on
     *         the specified resource.
     */
    public ProtectedTransitionResource[] getAllowedTransitions(CoralSession coralSession,StatefulResource resource,
                                                               Subject subject)
        throws WorkflowException;

    /**
     * Creates a protected transition.
     *
     * @param name the name of the transition.
     * @param from the original state.
     * @param to the desitination state.
     * @param permission the permission required to perform the transition.
     * @param subject the subject that performs the operation.
     * @return the newly created transition.
     * @throws InvalidResourceNameException if the name attribute contains illegal characters.
     */
    public ProtectedTransitionResource createProtectedTransition(CoralSession coralSession,
        String name, StateResource from, StateResource to, Permission permission, Subject subject)
        throws WorkflowException, InvalidResourceNameException;

    /**
     * Creates a temporal transistion.
     * 
     * @param name the name of the transition.
     * @param from the original state.
     * @param to the desitination state.
     * @param delay the delay in milliseconds.
     * @param subject the subject that performs the operation.
     * @return the newly created transition.
     * @throws InvalidResourceNameException if the name attribute contains illegal characters.
     */
    public TemporalTransitionResource createTemporalTransition(CoralSession coralSession,
        String name, StateResource from, StateResource to, long delay, Subject subject)
        throws WorkflowException, InvalidResourceNameException;

    /**
     * Creates a heuristic transition.
     * 
     * @param name the name of the transition.
     * @param from the original state.
     * @param to the desitination state.
     * @param probability the probability of transition.
     * @param subject the subject that performs the operation.
     * @return the newly created transition.
     * @throws InvalidResourceNameException if the name attribute contains illegal characters.
     */
    public HeuristicTransitionResource createHeuristicTransition(CoralSession coralSession,
        String name, StateResource from, StateResource to, double probability, Subject subject)
        throws WorkflowException, InvalidResourceNameException;

    /**
     * Deletes a transition.
     * 
     * @param transition the transition to delete.
     * @param subject the subject that performs the operation.
     */
    public void deleteTransition(CoralSession coralSession,TransitionResource transition, Subject subject)
        throws WorkflowException;
    
    /**
     * Assigns an initial state to a resource.
     * @param resource the resource.
     * @param state the initial state.
     */
    public void assignState(CoralSession coralSession,StatefulResource resource, StateResource state)
        throws WorkflowException;

    /**
     * Assigns an initial state to a resource.
     *
     * <p>First, an attempt will be made to find a <code>SiteResource</code>
     * ancestor of the resource. Then, primary automaton for the resource's
     * class will be looked up in the site (or in the global repository if 
     * the site ancestor was not found). If the automaton is successfully
     * found, and it contains precisely one initial state, the state will
     * be assigned to the resource. Otherwise exception will be thrown.</p>
     * @param resource the resource.
     */
    public void assignState(CoralSession coralSession,Resource root, StatefulResource resource)
        throws WorkflowException;
    

    /**
     * Performs a transition.
     * @param resource the resource.
     * @param transition the transition.
     */
    public void performTransition(CoralSession coralSession,StatefulResource resource, 
                                  TransitionResource transition)
        throws WorkflowException;

    /**
     * Perform actions neccessary when resource enters a new state.
     *
     * <p>You should not call this method from application code under normal
     * circumstantces. It is public to implement TemporalTransition scheduled
     * job transparently.</p>
     * <p>This method <b>does not</b> set the <code>state</code> of the
     * resource, this should be done before calling this method.</p>
     *
     * @param resource the resource.
     * @param state the new state
     */
    public void enterState(CoralSession coralSession,StatefulResource resource, StateResource state)
        throws WorkflowException;
        
	/**
	 * Fire transition.
	 *
	 * @param resource the resource.
	 * @param transition the name of the transition.
	 * @param subject the subject.
	 */
	public void performTransition(CoralSession coralSession,StatefulResource resource, String transition, Subject subject)
			throws WorkflowException;

    /**
     * Returns a named transition from a given state.
     * 
     * @param coralSession coral session.
     * @param state the state.
     * @param transitionName name of the requested transition.
     * @return transition resource.
     * @throws WorkflowException if outgoing transition with the given name is not found.
     */
    public TransitionResource getTransition(CoralSession coralSession, StateResource state, String transitionName)
        throws WorkflowException;

    /**
     * Returns valid transitions from a resource's current state as a map keyed by transaction name.
     * 
     * @param coralSession coral session
     * @param resource a stateful resource
     * @return map of valid transitions. If state is undefined, empty map is returned.
     * @throws WorkflowException
     */
    public Map<String, TransitionResource> getTransitionMap(CoralSession coralSession, StatefulResource resource)
        throws WorkflowException;

    /**
     * Returns transitions that specified subject is allowed to perform.
     * 
     * @param coralSession coral session
     * @param resource a stateful resource
     * @param subject a subject
     * @return map of allowed transitions keyed by transition name. If resource state is unefined empty map is returned.
     * @throws WorkflowException
     */
    public Map<String, TransitionResource> getAllowedTransitionMap(CoralSession coralSession, StatefulResource resource, Subject subject)
        throws WorkflowException;
}
