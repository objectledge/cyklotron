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
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;

import net.cyklotron.cms.CmsNodeResourceImpl;
import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>integration.preference</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class PreferenceResourceImpl
    extends CmsNodeResourceImpl
    implements PreferenceResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>scope</code> attribute. */
    private AttributeDefinition scopeDef;

    /** The AttributeDefinition object for the <code>required</code> attribute. */
    private AttributeDefinition requiredDef;

    /** The AttributeDefinition object for the <code>default</code> attribute. */
    private AttributeDefinition defaultDef;

    /** The AttributeDefinition object for the <code>modifyPermission</code> attribute. */
    private AttributeDefinition modifyPermissionDef;

    /** The AttributeDefinition object for the <code>uiHint</code> attribute. */
    private AttributeDefinition uiHintDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>integration.preference</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public PreferenceResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("integration.preference");
            scopeDef = rc.getAttribute("scope");
            requiredDef = rc.getAttribute("required");
            defaultDef = rc.getAttribute("default");
            modifyPermissionDef = rc.getAttribute("modifyPermission");
            uiHintDef = rc.getAttribute("uiHint");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>integration.preference</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static PreferenceResource getPreferenceResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof PreferenceResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not integration.preference");
        }
        return (PreferenceResource)res;
    }

    /**
     * Creates a new <code>integration.preference</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param scope the scope attribute
     * @param required the required attribute
     * @return a new PreferenceResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static PreferenceResource createPreferenceResource(CoralSession session, String name,
        Resource parent, String scope, boolean required)
        throws ValueRequiredException, InvalidResourceNameException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("integration.preference");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("scope"), scope);
            attrs.put(rc.getAttribute("required"), new Boolean(required));
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof PreferenceResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (PreferenceResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>scope</code> attribute.
     *
     * @return the value of the <code>scope</code> attribute.
     */
    public String getScope()
    {
        return (String)get(scopeDef);
    }
 
    /**
     * Sets the value of the <code>scope</code> attribute.
     *
     * @param value the value of the <code>scope</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setScope(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(scopeDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute scope "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>required</code> attribute.
     *
     * @return the value of the <code>required</code> attribute.
     */
    public boolean getRequired()
    {
        if(isDefined(requiredDef))
        {
            return ((Boolean)get(requiredDef)).booleanValue();
        }
        else
        {
            throw new BackendException("incompatible schema change");
        }
    }    

    /**
     * Sets the value of the <code>required</code> attribute.
     *
     * @param value the value of the <code>required</code> attribute.
     */
    public void setRequired(boolean value)
    {
        try
        {
            set(requiredDef, new Boolean(value));
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
     * Returns the value of the <code>default</code> attribute.
     *
     * @return the value of the <code>default</code> attribute.
     */
    public String getDefault()
    {
        return (String)get(defaultDef);
    }
    
    /**
     * Returns the value of the <code>default</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>default</code> attribute.
     */
    public String getDefault(String defaultValue)
    {
        if(isDefined(defaultDef))
        {
            return (String)get(defaultDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>default</code> attribute.
     *
     * @param value the value of the <code>default</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setDefault(String value)
    {
        try
        {
            if(value != null)
            {
                set(defaultDef, value);
            }
            else
            {
                unset(defaultDef);
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
	 * Checks if the value of the <code>default</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>default</code> attribute is defined.
	 */
    public boolean isDefaultDefined()
	{
	    return isDefined(defaultDef);
	}
 
    /**
     * Returns the value of the <code>modifyPermission</code> attribute.
     *
     * @return the value of the <code>modifyPermission</code> attribute.
     */
    public Permission getModifyPermission()
    {
        return (Permission)get(modifyPermissionDef);
    }
    
    /**
     * Returns the value of the <code>modifyPermission</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>modifyPermission</code> attribute.
     */
    public Permission getModifyPermission(Permission defaultValue)
    {
        if(isDefined(modifyPermissionDef))
        {
            return (Permission)get(modifyPermissionDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>modifyPermission</code> attribute.
     *
     * @param value the value of the <code>modifyPermission</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setModifyPermission(Permission value)
    {
        try
        {
            if(value != null)
            {
                set(modifyPermissionDef, value);
            }
            else
            {
                unset(modifyPermissionDef);
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
	 * Checks if the value of the <code>modifyPermission</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>modifyPermission</code> attribute is defined.
	 */
    public boolean isModifyPermissionDefined()
	{
	    return isDefined(modifyPermissionDef);
	}
 
    /**
     * Returns the value of the <code>uiHint</code> attribute.
     *
     * @return the value of the <code>uiHint</code> attribute.
     */
    public String getUiHint()
    {
        return (String)get(uiHintDef);
    }
    
    /**
     * Returns the value of the <code>uiHint</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>uiHint</code> attribute.
     */
    public String getUiHint(String defaultValue)
    {
        if(isDefined(uiHintDef))
        {
            return (String)get(uiHintDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>uiHint</code> attribute.
     *
     * @param value the value of the <code>uiHint</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setUiHint(String value)
    {
        try
        {
            if(value != null)
            {
                set(uiHintDef, value);
            }
            else
            {
                unset(uiHintDef);
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
	 * Checks if the value of the <code>uiHint</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>uiHint</code> attribute is defined.
	 */
    public boolean isUiHintDefined()
	{
	    return isDefined(uiHintDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////

    // @order scope, required
}
