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

import org.objectledge.coral.BackendException;
import org.objectledge.coral.datatypes.NodeImpl;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;

import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>workflow.automaton</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class AutomatonResourceImpl
    extends NodeImpl
    implements AutomatonResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>assigned_class</code> attribute. */
    private AttributeDefinition assigned_classDef;

    /** The AttributeDefinition object for the <code>primary</code> attribute. */
    private AttributeDefinition primaryDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>workflow.automaton</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public AutomatonResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("workflow.automaton");
            assigned_classDef = rc.getAttribute("assigned_class");
            primaryDef = rc.getAttribute("primary");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>workflow.automaton</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static AutomatonResource getAutomatonResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof AutomatonResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not workflow.automaton");
        }
        return (AutomatonResource)res;
    }

    /**
     * Creates a new <code>workflow.automaton</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param assigned_class the assigned_class attribute
     * @param primary the primary attribute
     * @return a new AutomatonResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     */
    public static AutomatonResource createAutomatonResource(CoralSession session, String name,
        Resource parent, ResourceClass assigned_class, boolean primary)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("workflow.automaton");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("assigned_class"), assigned_class);
            attrs.put(rc.getAttribute("primary"), new Boolean(primary));
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof AutomatonResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (AutomatonResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>assigned_class</code> attribute.
     *
     * @return the value of the <code>assigned_class</code> attribute.
     */
    public ResourceClass getAssigned_class()
    {
        return (ResourceClass)get(assigned_classDef);
    }
 
    /**
     * Sets the value of the <code>assigned_class</code> attribute.
     *
     * @param value the value of the <code>assigned_class</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setAssigned_class(ResourceClass value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(assigned_classDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute assigned_class "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>primary</code> attribute.
     *
     * @return the value of the <code>primary</code> attribute.
     */
    public boolean getPrimary()
    {
        if(isDefined(primaryDef))
        {
            return ((Boolean)get(primaryDef)).booleanValue();
        }
        else
        {
            throw new BackendException("incompatible schema change");
        }
    }    

    /**
     * Sets the value of the <code>primary</code> attribute.
     *
     * @param value the value of the <code>primary</code> attribute.
     */
    public void setPrimary(boolean value)
    {
        try
        {
            set(primaryDef, new Boolean(value));
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
