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
 * An implementation of <code>integration.string_preference</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class StringPreferenceResourceImpl
    extends PreferenceResourceImpl
    implements StringPreferenceResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>max_length</code> attribute. */
    private AttributeDefinition max_lengthDef;

    /** The AttributeDefinition object for the <code>min_length</code> attribute. */
    private AttributeDefinition min_lengthDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>integration.string_preference</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public StringPreferenceResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("integration.string_preference");
            max_lengthDef = rc.getAttribute("max_length");
            min_lengthDef = rc.getAttribute("min_length");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>integration.string_preference</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static StringPreferenceResource getStringPreferenceResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof StringPreferenceResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not integration.string_preference");
        }
        return (StringPreferenceResource)res;
    }

    /**
     * Creates a new <code>integration.string_preference</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param required the required attribute
     * @param scope the scope attribute
     * @return a new StringPreferenceResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     */
    public static StringPreferenceResource createStringPreferenceResource(CoralSession session,
        String name, Resource parent, boolean required, String scope)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("integration.string_preference");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("required"), new Boolean(required));
            attrs.put(rc.getAttribute("scope"), scope);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof StringPreferenceResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (StringPreferenceResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////

    /**
     * Returns the value of the <code>max_length</code> attribute.
     *
     * @return the value of the <code>max_length</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getMax_length()
        throws IllegalStateException
    {
        if(isDefined(max_lengthDef))
        {
            return ((Integer)get(max_lengthDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("attribute value is undefined");
        }
    }

    /**
     * Returns the value of the <code>max_length</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>max_length</code> attribute.
     */
    public int getMax_length(int defaultValue)
    {
        if(isDefined(max_lengthDef))
        {
            return ((Integer)get(max_lengthDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>max_length</code> attribute.
     *
     * @param value the value of the <code>max_length</code> attribute.
     */
    public void setMax_length(int value)
    {
        try
        {
            set(max_lengthDef, new Integer(value));
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
     * Removes the value of the <code>max_length</code> attribute.
     */
    public void unsetMax_length()
    {
        try
        {
            unset(max_lengthDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>max_length</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>max_length</code> attribute is defined.
	 */
    public boolean isMax_lengthDefined()
	{
	    return isDefined(max_lengthDef);
	}

    /**
     * Returns the value of the <code>min_length</code> attribute.
     *
     * @return the value of the <code>min_length</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getMin_length()
        throws IllegalStateException
    {
        if(isDefined(min_lengthDef))
        {
            return ((Integer)get(min_lengthDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("attribute value is undefined");
        }
    }

    /**
     * Returns the value of the <code>min_length</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>min_length</code> attribute.
     */
    public int getMin_length(int defaultValue)
    {
        if(isDefined(min_lengthDef))
        {
            return ((Integer)get(min_lengthDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>min_length</code> attribute.
     *
     * @param value the value of the <code>min_length</code> attribute.
     */
    public void setMin_length(int value)
    {
        try
        {
            set(min_lengthDef, new Integer(value));
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
     * Removes the value of the <code>min_length</code> attribute.
     */
    public void unsetMin_length()
    {
        try
        {
            unset(min_lengthDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>min_length</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>min_length</code> attribute is defined.
	 */
    public boolean isMin_lengthDefined()
	{
	    return isDefined(min_lengthDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
}
