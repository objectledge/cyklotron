package net.cyklotron.cms.workflow.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import net.cyklotron.cms.workflow.AutomatonResource;
import net.cyklotron.cms.workflow.AutomatonResourceImpl;
import net.cyklotron.cms.workflow.HeuristicTransitionResource;
import net.cyklotron.cms.workflow.HeuristicTransitionResourceImpl;
import net.cyklotron.cms.workflow.ProtectedTransitionResource;
import net.cyklotron.cms.workflow.ProtectedTransitionResourceImpl;
import net.cyklotron.cms.workflow.StateChangeListener;
import net.cyklotron.cms.workflow.StateResource;
import net.cyklotron.cms.workflow.StateResourceImpl;
import net.cyklotron.cms.workflow.StatefulResource;
import net.cyklotron.cms.workflow.TemporalTransitionResource;
import net.cyklotron.cms.workflow.TemporalTransitionResourceImpl;
import net.cyklotron.cms.workflow.TransitionResource;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

import org.objectledge.ComponentInitializationError;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.event.EventWhiteboard;
import org.objectledge.scheduler.AbstractJobDescriptor;
import org.objectledge.scheduler.AbstractScheduler;
import org.objectledge.scheduler.Schedule;

public class WorkflowServiceImpl
    implements WorkflowService
{
    // instance variables ////////////////////////////////////////////////////

    /** The event service. */
    private EventWhiteboard event;

    /** The scheduler service. */
    private AbstractScheduler scheduler;

    /** stateChanged method of StateChangeListener interface. */
    private Method stateChanged;

    /** The globally defined automata root. */
    private Resource globalAutomata;

    /** The root subject. */
    private Subject root;

    /** The listeners. This list is needed to keep permanent references to the
     *  listener object, to protect them from being GCed. */
    private ArrayList listeners = new ArrayList();

    // initialization ////////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public WorkflowServiceImpl(CoralSessionFactory sessionFactory, EventWhiteboard whiteboard, 
        AbstractScheduler scheduler, StateChangeListener[] listeners)
    {
        event = whiteboard; 
        this.scheduler = scheduler;
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            Resource[] res = coralSession.getStore().getResourceByPath("/workflow/automata");
            if(res.length != 1)
            {
                throw new ComponentInitializationError("Could not lookup globally defined automata");
            }
            globalAutomata = res[0];
        }
        finally
        {
            coralSession.close();
        }
        for(int i = 0; i< listeners.length; i++)
        {
            event.addListener(StateChangeListener.class,listeners[i],null);
        }
        try
        {
            stateChanged = StateChangeListener.class.
                getMethod("stateChanged", new Class[] { Role.class, StatefulResource.class });
        }
        catch(Exception e)
        {
            throw new ComponentInitializationError("Incopatible change of StateChangedListener", e);
        }
    }

    // WorkflowService interface /////////////////////////////////////////////

    // automata //////////////////////////////////////////////////////////////

    /**
     * Returns the root resource the automaton is defined for.
     *
     * @param automaton the automaton.
     * @return the root resource, or <code>null</code> for globally defined automata.
     */
    public Resource getRoot(CoralSession coralSession, AutomatonResource automaton)
        throws WorkflowException
    {
        try
        {
            if(automaton.getParent().equals(globalAutomata))
            {
                return null;
            }
            else
            {
                return automaton.getParent().getParent().getParent();
            }
        }
        catch(NullPointerException e)
        {
            // fall through
        }
        throw new WorkflowException("improperly located automaton "+automaton.getPath());
    }

    /**
     * Returns the defined automata.
     *
     * @param resource the resource to return automata for. <code>null</code> for
     *        globally defined automata.
     * @return an array of automata objects.
     */
    public AutomatonResource[] getAutomata(CoralSession coralSession, Resource resource)
        throws WorkflowException
    {
        Resource[] res;
        Resource root = getAutomataRoot(coralSession, resource);
        res = coralSession.getStore().getResource(root);
        AutomatonResource[] result = new AutomatonResource[res.length];
        System.arraycopy(res,0,result,0,res.length);
        return result;    
    }

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
     */
    public AutomatonResource createAutomaton(CoralSession coralSession, Resource resource, String name,
                                             ResourceClass resourceClass,
                                             boolean primary,
                                             Subject subject)
        throws WorkflowException
    {
        Resource parent = getAutomataRoot(coralSession, resource);
        AutomatonResource automaton;
        try
        {
            automaton = AutomatonResourceImpl.
                createAutomatonResource(coralSession, name, parent, 
                                        resourceClass, primary);
        }
        catch(ValueRequiredException e)
        {
            throw new WorkflowException("ARL exception", e);
        }
        if(primary)
        {
            // unset old primary automaton's flag
            makeAutomatonPrimary(coralSession, automaton, subject);
        }
        return automaton;
    }
    
    /**
     * Deletes an autmaton.
     *
     * @param automaton the automaton to delete.
     * @param subject the subject that performs the operation.
     */
    public void deleteAutomaton(CoralSession coralSession, AutomatonResource automaton, Subject subject)
        throws EntityInUseException, WorkflowException
    {
        StateResource[] states = getStates(coralSession, automaton, false);
        for(int i=0; i<states.length; i++)
        {
            StateResource state = states[i];
            try
            {
                QueryResults res = coralSession.getQuery().executeQuery(
                    "FIND RESOURCE FROM workflow.stateful WHERE state = "+
                    state.getIdString());
                int count = res.getArray(1).length;
                if( count > 0)
                {
                    throw new EntityInUseException("there are "+count+" resources in the state "+
                                                    automaton.getName()+":"+state.getName());
                }
            }
            catch(MalformedQueryException e)
            {
                throw new WorkflowException("ARL exception", e);
            }
        }
        try
        {
            TransitionResource[] transitions = getTransitions(coralSession, automaton);
            for(int i=0; i<transitions.length; i++)
            {
                coralSession.getStore().deleteResource(transitions[i]);
            }
            for(int i=0; i<states.length; i++)
            {
                coralSession.getStore().deleteResource(states[i]);
            }
            coralSession.getStore().deleteResource(automaton);
        }
        catch(EntityInUseException e)
        {
            throw new WorkflowException("ARL exception", e);
        }
    }
    
    /**
     * Deems an automaton to be primary for it's resource class.
     *
     * @param automaton the automaton.
     * @param subject the subject that performs the operation.
     */
    public void makeAutomatonPrimary(CoralSession coralSession, AutomatonResource automaton, Subject subject)
        throws WorkflowException
    {
        Resource resource = getRoot(coralSession, automaton);
        AutomatonResource[] automata = getAutomata(coralSession, resource);
        
        for(int i=0; i<automata.length; i++)
        {
            if(automata[i] != automaton && automata[i].getPrimary())
            {
                automata[i].setPrimary(false);
                automata[i].update();
            }
        }
        automaton.setPrimary(true);
        automaton.update();
    }

    /**
     * Returns a primary automaton for a resource class.
     *
     * <p>If the root does not contain any automata for the resource class,
     * a fallback to the globally defined automata will be performed.<p>
     * <p>If there are automata assigend to the resource class, but none of
     * them mareked as primary for the resource class, exception will be
     * thrown.</p> 
     *
     * @param root the resource where the automaton is to be searched for.
     * @param resourceClass the resource class.
     */
    public AutomatonResource getPrimaryAutomaton(CoralSession coralSession, Resource root, 
                                                ResourceClass resourceClass)
        throws WorkflowException
    {
        AutomatonResource[] automata = getAutomata(coralSession, root);
        if(automata.length == 0 && root != null)
        {
            return getPrimaryAutomaton(coralSession, null, resourceClass);
        }
        ResourceClass[] parentClasses = resourceClass.getParentClasses();
        HashSet classesSet = new HashSet();
        for(int i=0; i<parentClasses.length; i++)
        {
            classesSet.add(parentClasses[i]);
        }
        classesSet.add(resourceClass);
        for(int i=0; i<automata.length; i++)
        {
            if(automata[i].getPrimary() && 
               classesSet.contains(automata[i].getAssignedClass()))
            {
                return automata[i];
            }
        }
        if(automata.length == 0)
        {
            throw new WorkflowException("no automata defined for class "+
                                        resourceClass.getName());
        }
        else
        {
            throw new WorkflowException("none of the automatons for class "+
                                        resourceClass.getName()+
                                        " is marked as primary");
        }
    }

    /**
     * Returns the automaton the state belongs to.
     *
     * @param state the state.
     * @return the automaton.
     */
    public AutomatonResource getAutomaton(CoralSession coralSession, StateResource state)
        throws WorkflowException
    {
        try
        {
            Resource grandParent = state.getParent().getParent();
            if(grandParent instanceof AutomatonResource)
            {
                return (AutomatonResource)grandParent;
            }
        }
        catch(NullPointerException e)
        {
            // fall through
        }
        throw new WorkflowException("improperly localted state "+state.getPath());
    }

    /**
     * Returns the states of an automaton.
     *
     * @param automaton the automaton.
     * @param initial <code>true</code> to return valid initial states only,
     *        <code>false</code> to return all states.
     */
    public StateResource[] getStates(CoralSession coralSession, AutomatonResource automaton, 
                                     boolean initial)
        throws WorkflowException
    {
        Resource[] res = coralSession.getStore().getResource(getStateRoot(coralSession, automaton));
        if(initial)
        {
            ArrayList temp = new ArrayList();
            for(int i=0; i<res.length; i++)
            {
                if(((StateResource)res[i]).getInitial())
                {
                    temp.add(res[i]);
                }
            }
            StateResource[] result = new StateResource[temp.size()];
            temp.toArray(result);
            return result;
        }
        else
        {
            StateResource[] result = new StateResource[res.length];
            System.arraycopy(res,0,result,0,res.length);
            return result;
        }
    }
    
	/**
	 * Returns the states of an automaton.
	 *
	 * @param automaton the automaton.
	 * @param state the name of the state.
	 */
	public StateResource getState(CoralSession coralSession, AutomatonResource automaton, String state)
		throws WorkflowException
	{
		Resource[] res = coralSession.getStore().getResource(getStateRoot(coralSession, automaton));
		ArrayList temp = new ArrayList();
		for(int i=0; i<res.length; i++)
		{
			if(((StateResource)res[i]).getName().equals(state))
			{
				return (StateResource)res[i];
			}
		}
		throw new WorkflowException("Couldn;t find state '"+state+"' in automaton: "+automaton.getPath());
	}
    /**
     * Creates a new state of an automaton
     *
     * @param automaton the automaton to modify.
     * @param name the name of the state.
     * @param initial <code>true</code> if the state is a valid initial state.
     * @param asignee the role that should be informed about resources entering
     *        this state.
     * @param subject the subject that performs the operation.
     */
    public StateResource createState(CoralSession coralSession, AutomatonResource automaton, String name,
                                     boolean initial, Role asignee, Subject subject)
        throws WorkflowException
    {
        Resource parent = getStateRoot(coralSession, automaton);
        try
        {
            StateResource state = StateResourceImpl.
                createStateResource(coralSession, name, parent, initial);
            if(asignee != null)
            {
                state.setAssignee(asignee);
                state.update();
            }
            return state;
        }
        catch(ValueRequiredException e)
        {
            throw new WorkflowException("ARL exception", e);
        }       
    }
    
    /**
     * Deletes a state of an automaton.
     *
     * @param state the state.
     * @param subject the subject that performs the operation.
     */
    public void deleteState(CoralSession coralSession, StateResource state, Subject subject)
        throws WorkflowException, EntityInUseException
    {
        try
        {
            QueryResults res = coralSession.getQuery().executeQuery(
                "FIND RESOURCE FROM workflow.stateful WHERE state = "+
                state.getIdString());
            int count = res.getArray(1).length;
            if( count > 0)
            {
                throw new EntityInUseException("there are "+count+" resources in the state "+
                                               state.getName());
            }
        }
        catch(MalformedQueryException e)
        {
            throw new WorkflowException("ARL exception", e);
        }
        
        TransitionResource[] transitions = getTransitions(coralSession, getAutomaton(coralSession, state));
        for(int i=0; i<transitions.length; i++)
        {
            if(transitions[i].getTo().equals(state))
            {
                throw new EntityInUseException("there is a "+transitions[i].getName()+
                                               " transition into this state");
            }
        }

        try
        {
            for(int i=0; i<transitions.length; i++)
            {
                if(transitions[i].getFrom().equals(state))
                {
                    coralSession.getStore().deleteResource(transitions[i]);
                }
            }
            coralSession.getStore().deleteResource(state);
        }
        catch(EntityInUseException e)
        {
            throw new WorkflowException("ARL exception", e);
        }
    }
    
    /**
     * Returns the automaton the transition belongs to.
     *
     * @param transition the transition.
     * @return the automaton.
     */
    public AutomatonResource getAutomaton(CoralSession coralSession, TransitionResource transition)
        throws WorkflowException
    {
        try
        {
            Resource grandParent = transition.getParent().getParent();
            if(grandParent instanceof AutomatonResource)
            {
                return (AutomatonResource)grandParent;
            }
        }
        catch(NullPointerException e)
        {
            // fall through
        }
        throw new WorkflowException("improperly localted transition "+transition.getPath());
    }

    /**
     * Returns transitions defined in an automaton.
     *
     * @param automaton the automaton.
     * @return transitions defined for a state.
     */
    public TransitionResource[] getTransitions(CoralSession coralSession, AutomatonResource automaton)
        throws WorkflowException
    {
        Resource[] res = coralSession.getStore().getResource(getTransitionRoot(coralSession, automaton));
        TransitionResource[] result = new TransitionResource[res.length];
        System.arraycopy(res,0,result,0,res.length);
        return result;
    }

    /**
     * Returns transitions defined for a state.
     *
     * @param state the state.
     * @return transitions defined for a state.
     */
    public TransitionResource[] getTransitions(CoralSession coralSession, StateResource state)
        throws WorkflowException
    {
        TransitionResource[] transitions = getTransitions(coralSession, getAutomaton(coralSession, state));
        ArrayList temp = new ArrayList();
        for(int i=0; i<transitions.length; i++)
        {
            if(transitions[i].getFrom().equals(state))
            {
                temp.add(transitions[i]);
            }
        }
        TransitionResource[] result = new TransitionResource[temp.size()];
        temp.toArray(result);
        return result;
    }
    
    /**
     * Returns ProtectedTransitions that the specified subject can perform.
     *
     * @param resource the resource in question.
     * @param subject the subject.
     * @return protected transitions that the subject is allowed to perform on
     *         the specified resource.
     */
    public ProtectedTransitionResource[] getAllowedTransitions(CoralSession coralSession, StatefulResource resource,
                                                               Subject subject)
        throws WorkflowException
    {
        StateResource state = resource.getState();
        if(state == null)
        {
            return new ProtectedTransitionResource[0];
        }
        TransitionResource[] transitions = getTransitions(coralSession, getAutomaton(coralSession, state));
        ArrayList temp = new ArrayList();
        for(int i=0; i<transitions.length; i++)
        {
            if(transitions[i].getFrom().equals(state))
            {
                if((transitions[i] instanceof ProtectedTransitionResource))
                {
                    Permission permission = ((ProtectedTransitionResource)transitions[i]).
                        getPerformPermission();
                    if(subject.hasPermission(resource,permission))
                    {
                        temp.add(transitions[i]);
                    }
                }
            }
        }
        ProtectedTransitionResource[] result = 
            new ProtectedTransitionResource[temp.size()];
        temp.toArray(result);
        return result;
    }

    /**
     * Creates a protected transition.
     *
     * @param name the name of the transition.
     * @param from the original state.
     * @param to the desitination state.
     * @param permission the permission required to perform the transition.
     * @param subject the subject that performs the operation.
     * @return the newly created transition.
     */
    public ProtectedTransitionResource createProtectedTransition(CoralSession coralSession, String name,
                                                                 StateResource from,
                                                                 StateResource to,
                                                                 Permission permission,
                                                                 Subject subject)
        throws WorkflowException
    {
        if(!getAutomaton(coralSession, from).equals(getAutomaton(coralSession, to)))
        {
            throw new WorkflowException("source and destination states "+
                                        "belong to different automata");
        }        
        Resource parent = getTransitionRoot(coralSession, getAutomaton(coralSession, from));
        try
        {
            return ProtectedTransitionResourceImpl.
                createProtectedTransitionResource(coralSession, name, parent, 
                    from, permission, to);
        }
        catch(ValueRequiredException e)
        {
            throw new WorkflowException("ARL exception");
        }
    }

    /**
     * Creates a temporal transistion.
     *
     * @param name the name of the transition.
     * @param from the original state.
     * @param to the desitination state.
     * @param delay the delay in milliseconds.
     * @param subject the subject that performs the operation.
     * @return the newly created transition.
     */
    public TemporalTransitionResource createTemporalTransition(CoralSession coralSession, String name, 
                                                               StateResource from,
                                                               StateResource to,
                                                               long delay,
                                                               Subject subject)
        throws WorkflowException
    {
        if(!getAutomaton(coralSession, from).equals(getAutomaton(coralSession, to)))
        {
            throw new WorkflowException("source and destination states "+
                                        "belong to different automata");
        }        
        Resource parent = getTransitionRoot(coralSession, getAutomaton(coralSession, from));
        TransitionResource[] transitions = getTransitions(coralSession, from);
        for(int i=0; i<transitions.length; i++)
        {
            if(transitions[i] instanceof TemporalTransitionResource)
            {
                throw new WorkflowException("there is alreray a temporal transition from "+
                                            "state "+from.getName());
            }
        }
        try
        {
            return TemporalTransitionResourceImpl.
                createTemporalTransitionResource(coralSession, name, parent, 
                    delay, to, from);
        }
        catch(ValueRequiredException e)
        {
            throw new WorkflowException("ARL exception");
        }
    }

    /**
     * Creates a heuristic transition.
     *
     * @param name the name of the transition.
     * @param from the original state.
     * @param to the desitination state.
     * @param probability the probability of transition.
     * @param subject the subject that performs the operation.
     * @return the newly created transition.
     */
    public HeuristicTransitionResource createHeuristicTransition(CoralSession coralSession, String name,
                                                                 StateResource from,
                                                                 StateResource to,
                                                                 double probability,
                                                                 Subject subject)
        throws WorkflowException
    {
        if(!getAutomaton(coralSession, from).equals(getAutomaton(coralSession, to)))
        {
            throw new WorkflowException("source and destination states "+
                                        "belong to different automata");
        }        
        Resource parent = getTransitionRoot(coralSession, getAutomaton(coralSession, from));
        double existing = 0.0;
        TransitionResource[] transitions = getTransitions(coralSession, from);
        for(int i=0; i<transitions.length; i++)
        {
            if(transitions[i] instanceof HeuristicTransitionResource)
            {
                existing += ((HeuristicTransitionResource)transitions[i]).
                    getProbability().doubleValue();
            }
        }
        if(existing + probability > 1.0)
        {
            throw new WorkflowException("the sum of probabilities of heruistic transitions "+
                                        "from state "+from.getName()+" exceeds one");
        }
        try
        {
            return HeuristicTransitionResourceImpl.
                createHeuristicTransitionResource(coralSession, name, parent, 
                                                  from, new Double(probability),to);
        }
        catch(ValueRequiredException e)
        {
            throw new WorkflowException("ARL exception");
        }
    }

    /**
     * Deletes a transition.
     *
     * @param transition the transition to delete.
     * @param subject the subject that performs the operation.
     */
    public void deleteTransition(CoralSession coralSession, TransitionResource transition, Subject subject)
        throws WorkflowException
    {
        try
        {
            coralSession.getStore().deleteResource(transition);
        }
        catch(EntityInUseException e)
        {
            throw new WorkflowException("ARL exception", e);
        }
    }
    
    /**
     * Assigns an initial state to a resource.
     * @param resource the resource.
     * @param state the initial state.
     */
    public void assignState(CoralSession coralSession, StatefulResource resource, StateResource state)
        throws WorkflowException
    {
        AutomatonResource automaton = getAutomaton(coralSession, state);
        if(!resource.getResourceClass().equals(automaton.getAssignedClass()))
        {
            ResourceClass[] parentClasses = resource.getResourceClass().getParentClasses();
            HashSet classesSet = new HashSet();
            for(int i=0; i<parentClasses.length; i++)
            {
                classesSet.add(parentClasses[i]);
            }
            if(!classesSet.contains(automaton.getAssignedClass()))
            {
                throw new WorkflowException("state "+automaton.getName()+":"+
                                            state.getName()+" can be assigned to "+
                                            "intances of "+automaton.getAssignedClass().getName()+
                                            " only");
            }
        }
        if(!state.getInitial())
        {
            throw new WorkflowException("state "+automaton.getName()+":"+
                                        state.getName()+" is not a valid initial state");
        }
        resource.setState(state);
        resource.update();
        enterState(coralSession, resource, state);
    }

    /**
     * Assigns an initial state to a resource.
     *
     * <p>First primary automaton for the resource's
     * class will be looked up in the specified definition area.
     * If the automaton is successfully found, and it contains 
     * precisely one initial state, the state will
     * be assigned to the resource. Otherwise exception will be thrown.</p>
     * @param root the workflow definition root.
     * @param resource the resource.
     */
    public void assignState(CoralSession coralSession, Resource root, StatefulResource resource)
        throws WorkflowException
    {
        AutomatonResource automaton = getPrimaryAutomaton(coralSession, root, resource.getResourceClass());
        StateResource[] initial = getStates(coralSession, automaton, true);
        if(initial.length == 0)
        {
            throw new WorkflowException("no initial state in automaton "+
                                        automaton.getPath());
        }
        if(initial.length > 1)
        {
            throw new WorkflowException("multiple initial states in automaton "+
                                        automaton.getPath());
        }
        assignState(coralSession, resource, initial[0]);
    }
    
    /**
     * Performs a transition.
     * @param resource the resource.
     * @param transition the transition.
     */
    public void performTransition(CoralSession coralSession, StatefulResource resource, 
                                  ProtectedTransitionResource transition)
        throws WorkflowException
    {
        if(!transition.getFrom().equals(resource.getState()))
        {
            throw new WorkflowException("resource #"+resource.getIdString()+
                                        " is not in the expected state "+
                                        transition.getFrom().getPath());
        }
        Subject subject = coralSession.getUserSubject();
        if(!subject.hasPermission(resource, transition.getPerformPermission()))
        {
            throw new WorkflowException(subject.getName()+" is not allowed "+
                                        "to perform this transition");
        }
        resource.setState(transition.getTo());
        resource.update();
        enterState(coralSession, resource, transition.getTo());
    }

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
    public void enterState(CoralSession coralSession, StatefulResource resource, StateResource state)
        throws WorkflowException
    {
        Object[] args = null;
        if(state.getAssignee() != null)
        {
            args = new Object[] { state.getAssignee(), resource };
        }
        else
        {
            args = new Object[] { coralSession.getSecurity().getUniqueRole("root"), resource };
        }
        event.fireEvent(stateChanged,args,resource);
        //        event.fireEvent(stateChanged,args,state.getAssignee());
        event.fireEvent(stateChanged,args,null);
        
        TransitionResource[] transitions = getTransitions(coralSession, state);
        outer: for(int i=0; i<transitions.length; i++)
        {
            if(transitions[i] instanceof HeuristicTransitionResource)
            {
                double p = 0.0;
                double q = Math.random();
                inner: for(; i<transitions.length; i++)
                {
                    if(!(transitions[i] instanceof HeuristicTransitionResource))
                    {
                        continue inner;
                    }
                    p += ((HeuristicTransitionResource)transitions[i]).
                        getProbability().doubleValue();
                    if(p >= q)
                    {
                        resource.setState(transitions[i].getTo());
                        resource.update();
                        enterState(coralSession, resource, transitions[i].getTo());
                    }
                }
                break outer;
            }
            if(transitions[i] instanceof TemporalTransitionResource)
            {
                String arg = resource.getIdString()+","+transitions[i].getIdString();
                Date when = new Date(System.currentTimeMillis()+
                                     ((TemporalTransitionResource)transitions[i]).getDelay());
                try
                {
                    Schedule schedule = scheduler.createSchedule("at", when.toString());
                    AbstractJobDescriptor job = scheduler.createJobDescriptor("workflow:"+arg, schedule, 
                                                           "cms:workflow,TemporalTransition");
                    job.setArgument(arg);
                    job.setAutoClean(true);
                }
                catch(Exception e)
                {
                    throw new WorkflowException("filed to create scheduled job", e);
                }
                break outer;
            }
        }
    }

    // implementation ////////////////////////////////////////////////////////

    /**
     * Returns the automata parent node for the specified root resource.
     */
    private Resource getAutomataRoot(CoralSession coralSession, Resource root)
        throws WorkflowException
    {
        if(root == null)
        {
            return globalAutomata;
        }
        else
        {
            Resource[] res = coralSession.getStore().
                getResourceByPath(root.getPath()+"/workflow/automata");
            if(res.length != 1)
            {
                return globalAutomata;
            }
            return res[0];
        }
    }

    /**
     * Returns the state root for a specific automaton.
     */
    private Resource getStateRoot(CoralSession coralSession, AutomatonResource automaton)
        throws WorkflowException
    {
        Resource[] res = coralSession.getStore().getResource(automaton, "states");
        if(res.length != 1)
        {
            throw new WorkflowException("failed to lookup states for automaton "+
                                        automaton.getPath());
        }
        return res[0];
    }

    /**
     * Returns the transition root for a specific automaton.
     */
    private Resource getTransitionRoot(CoralSession coralSession, AutomatonResource automaton)
        throws WorkflowException
    {
        Resource[] res = coralSession.getStore().getResource(automaton, "transitions");
        if(res.length != 1)
        {
            throw new WorkflowException("failed to lookup transitions for automaton "+
                                        automaton.getPath());
        }
        return res[0];
    }
    
	/**
	 * Fire transition.
	 *
	 * @param resource the resource.
	 * @param transition the name of the transition.
	 * @param subject the subject.
	 */
	public void performTransition(CoralSession coralSession, StatefulResource resource, String transition, Subject subject)
		throws WorkflowException
	{
		TransitionResource[] transitions = getTransitions(coralSession, resource.getState());
		int i = 0;
		for(; i<transitions.length; i++)
		{
			if(transitions[i].getName().equals(transition))
			{
				break;
			}
		}
		if(i == transitions.length)
		{
			throw new WorkflowException("Illegal transition name '"+transition+
										 "' for navigation node in state '"+resource.getState().getName());
        }
		resource.setState(transitions[i].getTo());
		enterState(coralSession, resource, transitions[i].getTo());
		resource.update();
	}
}
