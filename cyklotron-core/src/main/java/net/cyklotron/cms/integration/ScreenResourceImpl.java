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
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;

import net.cyklotron.cms.CmsNodeResourceImpl;
import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>integration.screen</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class ScreenResourceImpl
    extends CmsNodeResourceImpl
    implements ScreenResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>configurationView</code> attribute. */
    private AttributeDefinition configurationViewDef;

    /** The AttributeDefinition object for the <code>screenName</code> attribute. */
    private AttributeDefinition screenNameDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>integration.screen</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public ScreenResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("integration.screen");
            configurationViewDef = rc.getAttribute("configurationView");
            screenNameDef = rc.getAttribute("screenName");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>integration.screen</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static ScreenResource getScreenResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof ScreenResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not integration.screen");
        }
        return (ScreenResource)res;
    }

    /**
     * Creates a new <code>integration.screen</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param screenName the screenName attribute
     * @return a new ScreenResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     */
    public static ScreenResource createScreenResource(CoralSession session, String name,
        Resource parent, String screenName)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("integration.screen");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("screenName"), screenName);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof ScreenResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (ScreenResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>configurationView</code> attribute.
     *
     * @return the value of the <code>configurationView</code> attribute.
     */
    public String getConfigurationView()
    {
        return (String)get(configurationViewDef);
    }
    
    /**
     * Returns the value of the <code>configurationView</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>configurationView</code> attribute.
     */
    public String getConfigurationView(String defaultValue)
    {
        if(isDefined(configurationViewDef))
        {
            return (String)get(configurationViewDef);
        }
        else
        {
            return defaultValue;
        }
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
 
    /**
     * Returns the value of the <code>screenName</code> attribute.
     *
     * @return the value of the <code>screenName</code> attribute.
     */
    public String getScreenName()
    {
        return (String)get(screenNameDef);
    }
 
    /**
     * Sets the value of the <code>screenName</code> attribute.
     *
     * @param value the value of the <code>screenName</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setScreenName(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(screenNameDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute screenName "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
     
    // @custom methods ///////////////////////////////////////////////////////

    /**
     * Returns name of the labeo appliction the screen belongs to.
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
