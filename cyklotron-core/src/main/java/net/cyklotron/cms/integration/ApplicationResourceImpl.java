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
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.CmsNodeResourceImpl;

/**
 * An implementation of <code>integration.application</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class ApplicationResourceImpl
    extends CmsNodeResourceImpl
    implements ApplicationResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
	@SuppressWarnings("unused")
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>applicationName</code> attribute. */
	private static AttributeDefinition<String> applicationNameDef;

    /** The AttributeDefinition object for the <code>enabled</code> attribute. */
    private static AttributeDefinition<Boolean> enabledDef;

    /** The AttributeDefinition object for the <code>priority</code> attribute. */
    private static AttributeDefinition<Integer> priorityDef;

    /** The AttributeDefinition object for the <code>required</code> attribute. */
    private static AttributeDefinition<Boolean> requiredDef;

    /** The AttributeDefinition object for the <code>vendor</code> attribute. */
	private static AttributeDefinition<String> vendorDef;

    /** The AttributeDefinition object for the <code>version</code> attribute. */
	private static AttributeDefinition<String> versionDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>integration.application</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public ApplicationResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>integration.application</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static ApplicationResource getApplicationResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof ApplicationResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not integration.application");
        }
        return (ApplicationResource)res;
    }

    /**
     * Creates a new <code>integration.application</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param applicationName the applicationName attribute
     * @param enabled the enabled attribute
     * @param priority the priority attribute
     * @param required the required attribute
     * @param vendor the vendor attribute
     * @param version the version attribute
     * @return a new ApplicationResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static ApplicationResource createApplicationResource(CoralSession session, String
        name, Resource parent, String applicationName, boolean enabled, int priority, boolean
        required, String vendor, String version)
        throws ValueRequiredException, InvalidResourceNameException
    {
        try
        {
            ResourceClass<ApplicationResource> rc = session.getSchema().getResourceClass("integration.application", ApplicationResource.class);
			Map<AttributeDefinition<?>, Object> attrs = new HashMap<AttributeDefinition<?>, Object>();
            attrs.put(rc.getAttribute("applicationName"), applicationName);
            attrs.put(rc.getAttribute("enabled"), Boolean.valueOf(enabled));
            attrs.put(rc.getAttribute("priority"), Integer.valueOf(priority));
            attrs.put(rc.getAttribute("required"), Boolean.valueOf(required));
            attrs.put(rc.getAttribute("vendor"), vendor);
            attrs.put(rc.getAttribute("version"), version);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof ApplicationResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (ApplicationResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>applicationName</code> attribute.
     *
     * @return the value of the <code>applicationName</code> attribute.
     */
    public String getApplicationName()
    {
        return get(applicationNameDef);
    }
 
    /**
     * Sets the value of the <code>applicationName</code> attribute.
     *
     * @param value the value of the <code>applicationName</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setApplicationName(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(applicationNameDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute applicationName "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>enabled</code> attribute.
     *
     * @return the value of the <code>enabled</code> attribute.
     */
    public boolean getEnabled()
    {
		return get(enabledDef).booleanValue();
    }    

    /**
     * Sets the value of the <code>enabled</code> attribute.
     *
     * @param value the value of the <code>enabled</code> attribute.
     */
    public void setEnabled(boolean value)
    {
        try
        {
            set(enabledDef, Boolean.valueOf(value));
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
     * Returns the value of the <code>priority</code> attribute.
     *
     * @return the value of the <code>priority</code> attribute.
     */
    public int getPriority()
    {
		return get(priorityDef).intValue();
    }    

    /**
     * Sets the value of the <code>priority</code> attribute.
     *
     * @param value the value of the <code>priority</code> attribute.
     */
    public void setPriority(int value)
    {
        try
        {
            set(priorityDef, Integer.valueOf(value));
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
     * Returns the value of the <code>required</code> attribute.
     *
     * @return the value of the <code>required</code> attribute.
     */
    public boolean getRequired()
    {
		return get(requiredDef).booleanValue();
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
            set(requiredDef, Boolean.valueOf(value));
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
     * Returns the value of the <code>vendor</code> attribute.
     *
     * @return the value of the <code>vendor</code> attribute.
     */
    public String getVendor()
    {
        return get(vendorDef);
    }
 
    /**
     * Sets the value of the <code>vendor</code> attribute.
     *
     * @param value the value of the <code>vendor</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setVendor(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(vendorDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute vendor "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>version</code> attribute.
     *
     * @return the value of the <code>version</code> attribute.
     */
    public String getVersion()
    {
        return get(versionDef);
    }
 
    /**
     * Sets the value of the <code>version</code> attribute.
     *
     * @param value the value of the <code>version</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setVersion(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(versionDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute version "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
     
    // @custom methods ///////////////////////////////////////////////////////
}
