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

import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>integration.integer_preference</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class IntegerPreferenceResourceImpl
    extends PreferenceResourceImpl
    implements IntegerPreferenceResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>range_max</code> attribute. */
    private AttributeDefinition range_maxDef;

    /** The AttributeDefinition object for the <code>range_min</code> attribute. */
    private AttributeDefinition range_minDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>integration.integer_preference</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public IntegerPreferenceResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("integration.integer_preference");
            range_maxDef = rc.getAttribute("range_max");
            range_minDef = rc.getAttribute("range_min");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>integration.integer_preference</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static IntegerPreferenceResource getIntegerPreferenceResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof IntegerPreferenceResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not integration.integer_preference");
        }
        return (IntegerPreferenceResource)res;
    }

    /**
     * Creates a new <code>integration.integer_preference</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param required the required attribute
     * @param scope the scope attribute
     * @return a new IntegerPreferenceResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     */
    public static IntegerPreferenceResource createIntegerPreferenceResource(CoralSession
        session, String name, Resource parent, boolean required, String scope)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("integration.integer_preference");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("required"), new Boolean(required));
            attrs.put(rc.getAttribute("scope"), scope);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof IntegerPreferenceResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (IntegerPreferenceResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>range_max</code> attribute.
     *
     * @return the value of the <code>range_max</code> attribute.
     */
    public Number getRange_max()
    {
        return (Number)get(range_maxDef);
    }
    
    /**
     * Returns the value of the <code>range_max</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>range_max</code> attribute.
     */
    public Number getRange_max(Number defaultValue)
    {
        if(isDefined(range_maxDef))
        {
            return (Number)get(range_maxDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>range_max</code> attribute.
     *
     * @param value the value of the <code>range_max</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRange_max(Number value)
    {
        try
        {
            if(value != null)
            {
                set(range_maxDef, value);
            }
            else
            {
                unset(range_maxDef);
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
	 * Checks if the value of the <code>range_max</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>range_max</code> attribute is defined.
	 */
    public boolean isRange_maxDefined()
	{
	    return isDefined(range_maxDef);
	}
 
    /**
     * Returns the value of the <code>range_min</code> attribute.
     *
     * @return the value of the <code>range_min</code> attribute.
     */
    public Number getRange_min()
    {
        return (Number)get(range_minDef);
    }
    
    /**
     * Returns the value of the <code>range_min</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>range_min</code> attribute.
     */
    public Number getRange_min(Number defaultValue)
    {
        if(isDefined(range_minDef))
        {
            return (Number)get(range_minDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>range_min</code> attribute.
     *
     * @param value the value of the <code>range_min</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRange_min(Number value)
    {
        try
        {
            if(value != null)
            {
                set(range_minDef, value);
            }
            else
            {
                unset(range_minDef);
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
	 * Checks if the value of the <code>range_min</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>range_min</code> attribute is defined.
	 */
    public boolean isRange_minDefined()
	{
	    return isDefined(range_minDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
}
