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
 * An implementation of <code>integration.component</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class ComponentResourceImpl
    extends NodeImpl
    implements ComponentResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>aggregation_source_view</code> attribute. */
    private AttributeDefinition aggregation_source_viewDef;

    /** The AttributeDefinition object for the <code>component_name</code> attribute. */
    private AttributeDefinition component_nameDef;

    /** The AttributeDefinition object for the <code>configuration_view</code> attribute. */
    private AttributeDefinition configuration_viewDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>integration.component</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public ComponentResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("integration.component");
            aggregation_source_viewDef = rc.getAttribute("aggregation_source_view");
            component_nameDef = rc.getAttribute("component_name");
            configuration_viewDef = rc.getAttribute("configuration_view");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
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
     * @param component_name the component_name attribute
     * @return a new ComponentResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     */
    public static ComponentResource createComponentResource(CoralSession session, String name,
        Resource parent, String component_name)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("integration.component");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("component_name"), component_name);
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
     * Returns the value of the <code>aggregation_source_view</code> attribute.
     *
     * @return the value of the <code>aggregation_source_view</code> attribute.
     */
    public String getAggregation_source_view()
    {
        return (String)get(aggregation_source_viewDef);
    }
    
    /**
     * Returns the value of the <code>aggregation_source_view</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>aggregation_source_view</code> attribute.
     */
    public String getAggregation_source_view(String defaultValue)
    {
        if(isDefined(aggregation_source_viewDef))
        {
            return (String)get(aggregation_source_viewDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>aggregation_source_view</code> attribute.
     *
     * @param value the value of the <code>aggregation_source_view</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregation_source_view(String value)
    {
        try
        {
            if(value != null)
            {
                set(aggregation_source_viewDef, value);
            }
            else
            {
                unset(aggregation_source_viewDef);
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
	 * Checks if the value of the <code>aggregation_source_view</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregation_source_view</code> attribute is defined.
	 */
    public boolean isAggregation_source_viewDefined()
	{
	    return isDefined(aggregation_source_viewDef);
	}
 
    /**
     * Returns the value of the <code>component_name</code> attribute.
     *
     * @return the value of the <code>component_name</code> attribute.
     */
    public String getComponent_name()
    {
        return (String)get(component_nameDef);
    }
 
    /**
     * Sets the value of the <code>component_name</code> attribute.
     *
     * @param value the value of the <code>component_name</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setComponent_name(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(component_nameDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute component_name "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>configuration_view</code> attribute.
     *
     * @return the value of the <code>configuration_view</code> attribute.
     */
    public String getConfiguration_view()
    {
        return (String)get(configuration_viewDef);
    }
    
    /**
     * Returns the value of the <code>configuration_view</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>configuration_view</code> attribute.
     */
    public String getConfiguration_view(String defaultValue)
    {
        if(isDefined(configuration_viewDef))
        {
            return (String)get(configuration_viewDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>configuration_view</code> attribute.
     *
     * @param value the value of the <code>configuration_view</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setConfiguration_view(String value)
    {
        try
        {
            if(value != null)
            {
                set(configuration_viewDef, value);
            }
            else
            {
                unset(configuration_viewDef);
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
	 * Checks if the value of the <code>configuration_view</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>configuration_view</code> attribute is defined.
	 */
    public boolean isConfiguration_viewDefined()
	{
	    return isDefined(configuration_viewDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////

    /**
     * Returns name of the labeo appliction the component belongs to.
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
}
