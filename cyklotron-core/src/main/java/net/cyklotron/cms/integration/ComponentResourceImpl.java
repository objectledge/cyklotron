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
 * An implementation of <code>integration.component</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class ComponentResourceImpl
    extends CmsNodeResourceImpl
    implements ComponentResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
	@SuppressWarnings("unused")
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>componentName</code> attribute. */
	private static AttributeDefinition<String> componentNameDef;

    /** The AttributeDefinition object for the <code>configurationView</code> attribute. */
	private static AttributeDefinition<String> configurationViewDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>integration.component</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public ComponentResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>integration.component</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static ComponentResource getComponentResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof ComponentResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not integration.component");
        }
        return (ComponentResource)res;
    }

    /**
     * Creates a new <code>integration.component</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param componentName the componentName attribute
     * @return a new ComponentResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static ComponentResource createComponentResource(CoralSession session, String name,
        Resource parent, String componentName)
        throws ValueRequiredException, InvalidResourceNameException
    {
        try
        {
            ResourceClass<ComponentResource> rc = session.getSchema().getResourceClass("integration.component", ComponentResource.class);
			Map<AttributeDefinition<?>, Object> attrs = new HashMap<AttributeDefinition<?>, Object>();
            attrs.put(rc.getAttribute("componentName"), componentName);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof ComponentResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (ComponentResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>componentName</code> attribute.
     *
     * @return the value of the <code>componentName</code> attribute.
     */
    public String getComponentName()
    {
        return get(componentNameDef);
    }
 
    /**
     * Sets the value of the <code>componentName</code> attribute.
     *
     * @param value the value of the <code>componentName</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setComponentName(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(componentNameDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute componentName "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>configurationView</code> attribute.
     *
     * @return the value of the <code>configurationView</code> attribute.
     */
    public String getConfigurationView()
    {
        return get(configurationViewDef);
    }
    
    /**
     * Returns the value of the <code>configurationView</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>configurationView</code> attribute.
     */
    public String getConfigurationView(String defaultValue)
    {
        return get(configurationViewDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>configurationView</code> attribute.
     *
     * @param value the value of the <code>configurationView</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setConfigurationView(String value)
    {
        try
        {
            if(value != null)
            {
                set(configurationViewDef, value);
            }
            else
            {
                unset(configurationViewDef);
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
	 * Checks if the value of the <code>configurationView</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>configurationView</code> attribute is defined.
	 */
    public boolean isConfigurationViewDefined()
	{
	    return isDefined(configurationViewDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////

    /**
     * Returns name of the appliction the component belongs to.
     */
    public String getApplicationName()
    {
        try
        {
            Resource app = getParent().getParent();
            try
            {
                return ((ApplicationResource)app).getApplicationName();
            }
            catch(ClassCastException e)
            {
                throw new BackendException("structural schema violation: "+
                                           app.getResourceClass().getName(), e);
            }
        }
        catch(NullPointerException e)
        {
            throw new BackendException("structural schema violation", e);            
        }
    }
    
    /**
     * Returns name of the appliction the component belongs to.
     */
    public String getApplicationResourceName()
    {
        return getParent().getParent().getName();
    }
}
