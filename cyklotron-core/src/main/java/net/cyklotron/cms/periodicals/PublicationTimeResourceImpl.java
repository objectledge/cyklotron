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
 
package net.cyklotron.cms.periodicals;

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
 * An implementation of <code>cms.periodicals.publication_time</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class PublicationTimeResourceImpl
    extends NodeImpl
    implements PublicationTimeResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>day_of_month</code> attribute. */
    private AttributeDefinition day_of_monthDef;

    /** The AttributeDefinition object for the <code>day_of_week</code> attribute. */
    private AttributeDefinition day_of_weekDef;

    /** The AttributeDefinition object for the <code>hour</code> attribute. */
    private AttributeDefinition hourDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.periodicals.publication_time</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public PublicationTimeResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("cms.periodicals.publication_time");
            day_of_monthDef = rc.getAttribute("day_of_month");
            day_of_weekDef = rc.getAttribute("day_of_week");
            hourDef = rc.getAttribute("hour");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.periodicals.publication_time</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static PublicationTimeResource getPublicationTimeResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof PublicationTimeResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.periodicals.publication_time");
        }
        return (PublicationTimeResource)res;
    }

    /**
     * Creates a new <code>cms.periodicals.publication_time</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new PublicationTimeResource instance.
     */
    public static PublicationTimeResource createPublicationTimeResource(CoralSession session,
        String name, Resource parent)
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.periodicals.publication_time");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof PublicationTimeResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (PublicationTimeResource)res;
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
     * Returns the value of the <code>day_of_month</code> attribute.
     *
     * @return the value of the <code>day_of_month</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getDay_of_month()
        throws IllegalStateException
    {
        if(isDefined(day_of_monthDef))
        {
            return ((Integer)get(day_of_monthDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("attribute value is undefined");
        }
    }

    /**
     * Returns the value of the <code>day_of_month</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>day_of_month</code> attribute.
     */
    public int getDay_of_month(int defaultValue)
    {
        if(isDefined(day_of_monthDef))
        {
            return ((Integer)get(day_of_monthDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>day_of_month</code> attribute.
     *
     * @param value the value of the <code>day_of_month</code> attribute.
     */
    public void setDay_of_month(int value)
    {
        try
        {
            set(day_of_monthDef, new Integer(value));
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
     * Removes the value of the <code>day_of_month</code> attribute.
     */
    public void unsetDay_of_month()
    {
        try
        {
            unset(day_of_monthDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>day_of_month</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>day_of_month</code> attribute is defined.
	 */
    public boolean isDay_of_monthDefined()
	{
	    return isDefined(day_of_monthDef);
	}

    /**
     * Returns the value of the <code>day_of_week</code> attribute.
     *
     * @return the value of the <code>day_of_week</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getDay_of_week()
        throws IllegalStateException
    {
        if(isDefined(day_of_weekDef))
        {
            return ((Integer)get(day_of_weekDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("attribute value is undefined");
        }
    }

    /**
     * Returns the value of the <code>day_of_week</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>day_of_week</code> attribute.
     */
    public int getDay_of_week(int defaultValue)
    {
        if(isDefined(day_of_weekDef))
        {
            return ((Integer)get(day_of_weekDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>day_of_week</code> attribute.
     *
     * @param value the value of the <code>day_of_week</code> attribute.
     */
    public void setDay_of_week(int value)
    {
        try
        {
            set(day_of_weekDef, new Integer(value));
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
     * Removes the value of the <code>day_of_week</code> attribute.
     */
    public void unsetDay_of_week()
    {
        try
        {
            unset(day_of_weekDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>day_of_week</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>day_of_week</code> attribute is defined.
	 */
    public boolean isDay_of_weekDefined()
	{
	    return isDefined(day_of_weekDef);
	}

    /**
     * Returns the value of the <code>hour</code> attribute.
     *
     * @return the value of the <code>hour</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getHour()
        throws IllegalStateException
    {
        if(isDefined(hourDef))
        {
            return ((Integer)get(hourDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("attribute value is undefined");
        }
    }

    /**
     * Returns the value of the <code>hour</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>hour</code> attribute.
     */
    public int getHour(int defaultValue)
    {
        if(isDefined(hourDef))
        {
            return ((Integer)get(hourDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>hour</code> attribute.
     *
     * @param value the value of the <code>hour</code> attribute.
     */
    public void setHour(int value)
    {
        try
        {
            set(hourDef, new Integer(value));
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
     * Removes the value of the <code>hour</code> attribute.
     */
    public void unsetHour()
    {
        try
        {
            unset(hourDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>hour</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>hour</code> attribute is defined.
	 */
    public boolean isHourDefined()
	{
	    return isDefined(hourDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
}
