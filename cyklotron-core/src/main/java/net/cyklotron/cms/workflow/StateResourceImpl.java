// 
// Copyright (c) 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
// 
// * Redistributions of source code must retain the above copyright notice,  
//       this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//       this list of conditions and the following disclaimer in the documentation  
//       and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//       nor the names of its contributors may be used to endorse or promote products  
//       derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
// 
 
package net.cyklotron.cms.workflow;

import java.util.HashMap;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;

import net.cyklotron.cms.CmsNodeResourceImpl;

/**
 * An implementation of <code>workflow.state</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class StateResourceImpl
    extends CmsNodeResourceImpl
    implements StateResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>assignee</code> attribute. */
    private AttributeDefinition assigneeDef;

    /** The AttributeDefinition object for the <code>initial</code> attribute. */
    private AttributeDefinition initialDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>workflow.state</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public StateResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("workflow.state");
            assigneeDef = rc.getAttribute("assignee");
            initialDef = rc.getAttribute("initial");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>workflow.state</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static StateResource getStateResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof StateResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not workflow.state");
        }
        return (StateResource)res;
    }

    /**
     * Creates a new <code>workflow.state</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param initial the initial attribute
     * @return a new StateResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     */
    public static StateResource createStateResource(CoralSession session, String name, Resource
        parent, boolean initial)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("workflow.state");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("initial"), new Boolean(initial));
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof StateResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (StateResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>assignee</code> attribute.
     *
     * @return the value of the <code>assignee</code> attribute.
     */
    public Role getAssignee()
    {
        return (Role)get(assigneeDef);
    }
    
    /**
     * Returns the value of the <code>assignee</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>assignee</code> attribute.
     */
    public Role getAssignee(Role defaultValue)
    {
        if(isDefined(assigneeDef))
        {
            return (Role)get(assigneeDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>assignee</code> attribute.
     *
     * @param value the value of the <code>assignee</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAssignee(Role value)
    {
        try
        {
            if(value != null)
            {
                set(assigneeDef, value);
            }
            else
            {
                unset(assigneeDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>assignee</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>assignee</code> attribute is defined.
	 */
    public boolean isAssigneeDefined()
	{
	    return isDefined(assigneeDef);
	}
 
    /**
     * Returns the value of the <code>initial</code> attribute.
     *
     * @return the value of the <code>initial</code> attribute.
     */
    public boolean getInitial()
    {
        if(isDefined(initialDef))
        {
            return ((Boolean)get(initialDef)).booleanValue();
        }
        else
        {
            throw new BackendException("incompatible schema change");
        }
    }    

    /**
     * Sets the value of the <code>initial</code> attribute.
     *
     * @param value the value of the <code>initial</code> attribute.
     */
    public void setInitial(boolean value)
    {
        try
        {
            set(initialDef, new Boolean(value));
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
     
    // @custom methods ///////////////////////////////////////////////////////
}
