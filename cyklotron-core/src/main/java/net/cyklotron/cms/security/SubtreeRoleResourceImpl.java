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
 
package net.cyklotron.cms.security;

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

/**
 * An implementation of <code>cms.security.subtree_role</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class SubtreeRoleResourceImpl
    extends RoleResourceImpl
    implements SubtreeRoleResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>subtreeRoot</code> attribute. */
    private AttributeDefinition subtreeRootDef;

    /** The AttributeDefinition object for the <code>recursive</code> attribute. */
    private AttributeDefinition recursiveDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.security.subtree_role</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public SubtreeRoleResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("cms.security.subtree_role");
            subtreeRootDef = rc.getAttribute("subtreeRoot");
            recursiveDef = rc.getAttribute("recursive");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.security.subtree_role</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static SubtreeRoleResource getSubtreeRoleResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof SubtreeRoleResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.security.subtree_role");
        }
        return (SubtreeRoleResource)res;
    }

    /**
     * Creates a new <code>cms.security.subtree_role</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param role the role attribute
     * @param deletable the deletable attribute
     * @param subtreeRoot the subtreeRoot attribute
     * @param recursive the recursive attribute
     * @return a new SubtreeRoleResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     */
    public static SubtreeRoleResource createSubtreeRoleResource(CoralSession session, String
        name, Resource parent, Role role, boolean deletable, Resource subtreeRoot, boolean
        recursive)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.security.subtree_role");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("role"), role);
            attrs.put(rc.getAttribute("deletable"), new Boolean(deletable));
            attrs.put(rc.getAttribute("subtreeRoot"), subtreeRoot);
            attrs.put(rc.getAttribute("recursive"), new Boolean(recursive));
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof SubtreeRoleResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (SubtreeRoleResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>subtreeRoot</code> attribute.
     *
     * @return the value of the <code>subtreeRoot</code> attribute.
     */
    public Resource getSubtreeRoot()
    {
        return (Resource)get(subtreeRootDef);
    }
 
    /**
     * Sets the value of the <code>subtreeRoot</code> attribute.
     *
     * @param value the value of the <code>subtreeRoot</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setSubtreeRoot(Resource value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(subtreeRootDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute subtreeRoot "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>recursive</code> attribute.
     *
     * @return the value of the <code>recursive</code> attribute.
     */
    public boolean getRecursive()
    {
        if(isDefined(recursiveDef))
        {
            return ((Boolean)get(recursiveDef)).booleanValue();
        }
        else
        {
            throw new BackendException("incompatible schema change");
        }
    }    

    /**
     * Sets the value of the <code>recursive</code> attribute.
     *
     * @param value the value of the <code>recursive</code> attribute.
     */
    public void setRecursive(boolean value)
    {
        try
        {
            set(recursiveDef, new Boolean(value));
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

    // @order role, deletable, subtreeRoot, recursive
}
