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
 * An implementation of <code>integration.schema_role</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class SchemaRoleResourceImpl
    extends NodeImpl
    implements SchemaRoleResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>deletable</code> attribute. */
    private AttributeDefinition deletableDef;

    /** The AttributeDefinition object for the <code>recursive</code> attribute. */
    private AttributeDefinition recursiveDef;

    /** The AttributeDefinition object for the <code>roleAttributeName</code> attribute. */
    private AttributeDefinition roleAttributeNameDef;

    /** The AttributeDefinition object for the <code>subtreeRole</code> attribute. */
    private AttributeDefinition subtreeRoleDef;

    /** The AttributeDefinition object for the <code>suffixAttributeName</code> attribute. */
    private AttributeDefinition suffixAttributeNameDef;

    /** The AttributeDefinition object for the <code>superRole</code> attribute. */
    private AttributeDefinition superRoleDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>integration.schema_role</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public SchemaRoleResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("integration.schema_role");
            deletableDef = rc.getAttribute("deletable");
            recursiveDef = rc.getAttribute("recursive");
            roleAttributeNameDef = rc.getAttribute("roleAttributeName");
            subtreeRoleDef = rc.getAttribute("subtreeRole");
            suffixAttributeNameDef = rc.getAttribute("suffixAttributeName");
            superRoleDef = rc.getAttribute("superRole");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
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
     */
    public static SchemaRoleResource createSchemaRoleResource(CoralSession session, String name,
        Resource parent)
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("integration.schema_role");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
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
        if(isDefined(deletableDef))
        {
            return ((Boolean)get(deletableDef)).booleanValue();
        }
        else
        {
            throw new IllegalStateException("attribute value is undefined");
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
        if(isDefined(deletableDef))
        {
            return ((Boolean)get(deletableDef)).booleanValue();
        }
        else
        {
            return defaultValue;
        }
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
        if(isDefined(recursiveDef))
        {
            return ((Boolean)get(recursiveDef)).booleanValue();
        }
        else
        {
            throw new IllegalStateException("attribute value is undefined");
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
        if(isDefined(recursiveDef))
        {
            return ((Boolean)get(recursiveDef)).booleanValue();
        }
        else
        {
            return defaultValue;
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
        return (String)get(roleAttributeNameDef);
    }
    
    /**
     * Returns the value of the <code>roleAttributeName</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>roleAttributeName</code> attribute.
     */
    public String getRoleAttributeName(String defaultValue)
    {
        if(isDefined(roleAttributeNameDef))
        {
            return (String)get(roleAttributeNameDef);
        }
        else
        {
            return defaultValue;
        }
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
        if(isDefined(subtreeRoleDef))
        {
            return ((Boolean)get(subtreeRoleDef)).booleanValue();
        }
        else
        {
            throw new IllegalStateException("attribute value is undefined");
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
        if(isDefined(subtreeRoleDef))
        {
            return ((Boolean)get(subtreeRoleDef)).booleanValue();
        }
        else
        {
            return defaultValue;
        }
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
            set(subtreeRoleDef, new Boolean(value));
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
        return (String)get(suffixAttributeNameDef);
    }
    
    /**
     * Returns the value of the <code>suffixAttributeName</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>suffixAttributeName</code> attribute.
     */
    public String getSuffixAttributeName(String defaultValue)
    {
        if(isDefined(suffixAttributeNameDef))
        {
            return (String)get(suffixAttributeNameDef);
        }
        else
        {
            return defaultValue;
        }
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
        return (Resource)get(superRoleDef);
    }
    
    /**
     * Returns the value of the <code>superRole</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>superRole</code> attribute.
     */
    public Resource getSuperRole(Resource defaultValue)
    {
        if(isDefined(superRoleDef))
        {
            return (Resource)get(superRoleDef);
        }
        else
        {
            return defaultValue;
        }
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
