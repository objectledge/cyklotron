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

import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.CmsNodeResourceImpl;

/**
 * An implementation of <code>cms.security.role</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class RoleResourceImpl
    extends CmsNodeResourceImpl
    implements RoleResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>role</code> attribute. */
    private static AttributeDefinition roleDef;

    /** The AttributeDefinition object for the <code>deletable</code> attribute. */
    private static AttributeDefinition deletableDef;

    /** The AttributeDefinition object for the <code>descriptionKey</code> attribute. */
    private static AttributeDefinition descriptionKeyDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.security.role</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public RoleResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.security.role</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static RoleResource getRoleResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof RoleResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.security.role");
        }
        return (RoleResource)res;
    }

    /**
     * Creates a new <code>cms.security.role</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param role the role attribute
     * @param deletable the deletable attribute
     * @return a new RoleResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static RoleResource createRoleResource(CoralSession session, String name, Resource
        parent, Role role, boolean deletable)
        throws ValueRequiredException, InvalidResourceNameException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.security.role");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("role"), role);
            attrs.put(rc.getAttribute("deletable"), new Boolean(deletable));
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof RoleResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (RoleResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>role</code> attribute.
     *
     * @return the value of the <code>role</code> attribute.
     */
    public Role getRole()
    {
        return (Role)getInternal(roleDef, null);
    }
 
    /**
     * Sets the value of the <code>role</code> attribute.
     *
     * @param value the value of the <code>role</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setRole(Role value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(roleDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute role "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>deletable</code> attribute.
     *
     * @return the value of the <code>deletable</code> attribute.
     */
    public boolean getDeletable()
    {
		return ((Boolean)getInternal(deletableDef, null)).booleanValue();
    }    

    /**
     * Sets the value of the <code>deletable</code> attribute.
     *
     * @param value the value of the <code>deletable</code> attribute.
     */
    public void setDeletable(boolean value)
    {
        try
        {
            set(deletableDef, new Boolean(value));
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
     * Returns the value of the <code>descriptionKey</code> attribute.
     *
     * @return the value of the <code>descriptionKey</code> attribute.
     */
    public String getDescriptionKey()
    {
        return (String)getInternal(descriptionKeyDef, null);
    }
    
    /**
     * Returns the value of the <code>descriptionKey</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>descriptionKey</code> attribute.
     */
    public String getDescriptionKey(String defaultValue)
    {
        return (String)getInternal(descriptionKeyDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>descriptionKey</code> attribute.
     *
     * @param value the value of the <code>descriptionKey</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setDescriptionKey(String value)
    {
        try
        {
            if(value != null)
            {
                set(descriptionKeyDef, value);
            }
            else
            {
                unset(descriptionKeyDef);
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
	 * Checks if the value of the <code>descriptionKey</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>descriptionKey</code> attribute is defined.
	 */
    public boolean isDescriptionKeyDefined()
	{
	    return isDefined(descriptionKeyDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////

    // @order role, deletable
}
