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
import org.objectledge.coral.store.InvalidResourceNameException;
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

    /** The AttributeDefinition object for the <code>maxLength</code> attribute. */
    private AttributeDefinition maxLengthDef;

    /** The AttributeDefinition object for the <code>minLength</code> attribute. */
    private AttributeDefinition minLengthDef;

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
            maxLengthDef = rc.getAttribute("maxLength");
            minLengthDef = rc.getAttribute("minLength");
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
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static StringPreferenceResource createStringPreferenceResource(CoralSession session,
        String name, Resource parent, boolean required, String scope)
        throws ValueRequiredException, InvalidResourceNameException
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
     * Returns the value of the <code>maxLength</code> attribute.
     *
     * @return the value of the <code>maxLength</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getMaxLength()
        throws IllegalStateException
    {
        if(isDefined(maxLengthDef))
        {
            return ((Integer)get(maxLengthDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute maxLength is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>maxLength</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>maxLength</code> attribute.
     */
    public int getMaxLength(int defaultValue)
    {
        if(isDefined(maxLengthDef))
        {
            return ((Integer)get(maxLengthDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>maxLength</code> attribute.
     *
     * @param value the value of the <code>maxLength</code> attribute.
     */
    public void setMaxLength(int value)
    {
        try
        {
            set(maxLengthDef, new Integer(value));
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
     * Removes the value of the <code>maxLength</code> attribute.
     */
    public void unsetMaxLength()
    {
        try
        {
            unset(maxLengthDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>maxLength</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>maxLength</code> attribute is defined.
	 */
    public boolean isMaxLengthDefined()
	{
	    return isDefined(maxLengthDef);
	}

    /**
     * Returns the value of the <code>minLength</code> attribute.
     *
     * @return the value of the <code>minLength</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getMinLength()
        throws IllegalStateException
    {
        if(isDefined(minLengthDef))
        {
            return ((Integer)get(minLengthDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute minLength is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>minLength</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>minLength</code> attribute.
     */
    public int getMinLength(int defaultValue)
    {
        if(isDefined(minLengthDef))
        {
            return ((Integer)get(minLengthDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>minLength</code> attribute.
     *
     * @param value the value of the <code>minLength</code> attribute.
     */
    public void setMinLength(int value)
    {
        try
        {
            set(minLengthDef, new Integer(value));
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
     * Removes the value of the <code>minLength</code> attribute.
     */
    public void unsetMinLength()
    {
        try
        {
            unset(minLengthDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>minLength</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>minLength</code> attribute is defined.
	 */
    public boolean isMinLengthDefined()
	{
	    return isDefined(minLengthDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
}
