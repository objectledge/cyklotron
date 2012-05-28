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
 
package net.cyklotron.cms.integration;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.CmsNodeResourceImpl;

/**
 * An implementation of <code>integration.schema_role</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class SchemaRoleResourceImpl
    extends CmsNodeResourceImpl
    implements SchemaRoleResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>deletable</code> attribute. */
    private static AttributeDefinition<Boolean> deletableDef;

    /** The AttributeDefinition object for the <code>recursive</code> attribute. */
    private static AttributeDefinition<Boolean> recursiveDef;

    /** The AttributeDefinition object for the <code>roleAttributeName</code> attribute. */
	private static AttributeDefinition<String> roleAttributeNameDef;

    /** The AttributeDefinition object for the <code>subtreeRole</code> attribute. */
    private static AttributeDefinition<Boolean> subtreeRoleDef;

    /** The AttributeDefinition object for the <code>suffixAttributeName</code> attribute. */
	private static AttributeDefinition<String> suffixAttributeNameDef;

    /** The AttributeDefinition object for the <code>superRole</code> attribute. */
	private static AttributeDefinition<Resource> superRoleDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>integration.schema_role</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public SchemaRoleResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>integration.schema_role</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static SchemaRoleResource getSchemaRoleResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof SchemaRoleResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not integration.schema_role");
        }
        return (SchemaRoleResource)res;
    }

    /**
     * Creates a new <code>integration.schema_role</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new SchemaRoleResource instance.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static SchemaRoleResource createSchemaRoleResource(CoralSession session, String name,
        Resource parent)
        throws InvalidResourceNameException
    {
        try
        {
            ResourceClass<SchemaRoleResource> rc = session.getSchema().getResourceClass("integration.schema_role", SchemaRoleResource.class);
		    Resource res = session.getStore().createResource(name, parent, rc,
                java.util.Collections.<AttributeDefinition<?>, Object> emptyMap());			
            if(!(res instanceof SchemaRoleResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (SchemaRoleResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////

    /**
     * Returns the value of the <code>deletable</code> attribute.
     *
     * @return the value of the <code>deletable</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getDeletable()
        throws IllegalStateException
    {
	    Boolean value = get(deletableDef);
        if(value != null)
        {
            return value.booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute deletable is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>deletable</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>deletable</code> attribute.
     */
    public boolean getDeletable(boolean defaultValue)
    {
		return get(deletableDef, Boolean.valueOf(defaultValue)).booleanValue();
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
            set(deletableDef, Boolean.valueOf(value));
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
     * Removes the value of the <code>deletable</code> attribute.
     */
    public void unsetDeletable()
    {
        try
        {
            unset(deletableDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>deletable</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>deletable</code> attribute is defined.
	 */
    public boolean isDeletableDefined()
	{
	    return isDefined(deletableDef);
	}

    /**
     * Returns the value of the <code>recursive</code> attribute.
     *
     * @return the value of the <code>recursive</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getRecursive()
        throws IllegalStateException
    {
	    Boolean value = get(recursiveDef);
        if(value != null)
        {
            return value.booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute recursive is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>recursive</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>recursive</code> attribute.
     */
    public boolean getRecursive(boolean defaultValue)
    {
		return get(recursiveDef, Boolean.valueOf(defaultValue)).booleanValue();
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
            set(recursiveDef, Boolean.valueOf(value));
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
     * Removes the value of the <code>recursive</code> attribute.
     */
    public void unsetRecursive()
    {
        try
        {
            unset(recursiveDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>recursive</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>recursive</code> attribute is defined.
	 */
    public boolean isRecursiveDefined()
	{
	    return isDefined(recursiveDef);
	}
 
    /**
     * Returns the value of the <code>roleAttributeName</code> attribute.
     *
     * @return the value of the <code>roleAttributeName</code> attribute.
     */
    public String getRoleAttributeName()
    {
        return get(roleAttributeNameDef);
    }
    
    /**
     * Returns the value of the <code>roleAttributeName</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>roleAttributeName</code> attribute.
     */
    public String getRoleAttributeName(String defaultValue)
    {
        return get(roleAttributeNameDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>roleAttributeName</code> attribute.
     *
     * @param value the value of the <code>roleAttributeName</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRoleAttributeName(String value)
    {
        try
        {
            if(value != null)
            {
                set(roleAttributeNameDef, value);
            }
            else
            {
                unset(roleAttributeNameDef);
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
	 * Checks if the value of the <code>roleAttributeName</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>roleAttributeName</code> attribute is defined.
	 */
    public boolean isRoleAttributeNameDefined()
	{
	    return isDefined(roleAttributeNameDef);
	}

    /**
     * Returns the value of the <code>subtreeRole</code> attribute.
     *
     * @return the value of the <code>subtreeRole</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getSubtreeRole()
        throws IllegalStateException
    {
	    Boolean value = get(subtreeRoleDef);
        if(value != null)
        {
            return value.booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute subtreeRole is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>subtreeRole</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>subtreeRole</code> attribute.
     */
    public boolean getSubtreeRole(boolean defaultValue)
    {
		return get(subtreeRoleDef, Boolean.valueOf(defaultValue)).booleanValue();
	}

    /**
     * Sets the value of the <code>subtreeRole</code> attribute.
     *
     * @param value the value of the <code>subtreeRole</code> attribute.
     */
    public void setSubtreeRole(boolean value)
    {
        try
        {
            set(subtreeRoleDef, Boolean.valueOf(value));
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
     * Removes the value of the <code>subtreeRole</code> attribute.
     */
    public void unsetSubtreeRole()
    {
        try
        {
            unset(subtreeRoleDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>subtreeRole</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>subtreeRole</code> attribute is defined.
	 */
    public boolean isSubtreeRoleDefined()
	{
	    return isDefined(subtreeRoleDef);
	}
 
    /**
     * Returns the value of the <code>suffixAttributeName</code> attribute.
     *
     * @return the value of the <code>suffixAttributeName</code> attribute.
     */
    public String getSuffixAttributeName()
    {
        return get(suffixAttributeNameDef);
    }
    
    /**
     * Returns the value of the <code>suffixAttributeName</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>suffixAttributeName</code> attribute.
     */
    public String getSuffixAttributeName(String defaultValue)
    {
        return get(suffixAttributeNameDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>suffixAttributeName</code> attribute.
     *
     * @param value the value of the <code>suffixAttributeName</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSuffixAttributeName(String value)
    {
        try
        {
            if(value != null)
            {
                set(suffixAttributeNameDef, value);
            }
            else
            {
                unset(suffixAttributeNameDef);
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
	 * Checks if the value of the <code>suffixAttributeName</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>suffixAttributeName</code> attribute is defined.
	 */
    public boolean isSuffixAttributeNameDefined()
	{
	    return isDefined(suffixAttributeNameDef);
	}
 
    /**
     * Returns the value of the <code>superRole</code> attribute.
     *
     * @return the value of the <code>superRole</code> attribute.
     */
    public Resource getSuperRole()
    {
        return get(superRoleDef);
    }
    
    /**
     * Returns the value of the <code>superRole</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>superRole</code> attribute.
     */
    public Resource getSuperRole(Resource defaultValue)
    {
        return get(superRoleDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>superRole</code> attribute.
     *
     * @param value the value of the <code>superRole</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSuperRole(Resource value)
    {
        try
        {
            if(value != null)
            {
                set(superRoleDef, value);
            }
            else
            {
                unset(superRoleDef);
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
	 * Checks if the value of the <code>superRole</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>superRole</code> attribute is defined.
	 */
    public boolean isSuperRoleDefined()
	{
	    return isDefined(superRoleDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
}
