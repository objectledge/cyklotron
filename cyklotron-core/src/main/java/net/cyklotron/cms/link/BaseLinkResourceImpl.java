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
 
package net.cyklotron.cms.link;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.objectledge.context.Context;
import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;

import net.cyklotron.cms.CmsConstants;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsNodeResourceImpl;
import net.cyklotron.cms.workflow.StateResource;
import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>cms.link.base_link</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class BaseLinkResourceImpl
    extends CmsNodeResourceImpl
    implements BaseLinkResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>endDate</code> attribute. */
    private AttributeDefinition endDateDef;

    /** The AttributeDefinition object for the <code>eternal</code> attribute. */
    private AttributeDefinition eternalDef;

    /** The AttributeDefinition object for the <code>startDate</code> attribute. */
    private AttributeDefinition startDateDef;

    /** The AttributeDefinition object for the <code>state</code> attribute. */
    private AttributeDefinition stateDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.link.base_link</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public BaseLinkResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("cms.link.base_link");
            endDateDef = rc.getAttribute("endDate");
            eternalDef = rc.getAttribute("eternal");
            startDateDef = rc.getAttribute("startDate");
            stateDef = rc.getAttribute("state");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.link.base_link</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static BaseLinkResource getBaseLinkResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof BaseLinkResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.link.base_link");
        }
        return (BaseLinkResource)res;
    }

    /**
     * Creates a new <code>cms.link.base_link</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new BaseLinkResource instance.
     */
    public static BaseLinkResource createBaseLinkResource(CoralSession session, String name,
        Resource parent)
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.link.base_link");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof BaseLinkResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (BaseLinkResource)res;
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
     * Returns the value of the <code>endDate</code> attribute.
     *
     * @return the value of the <code>endDate</code> attribute.
     */
    public Date getEndDate()
    {
        return (Date)get(endDateDef);
    }
    
    /**
     * Returns the value of the <code>endDate</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>endDate</code> attribute.
     */
    public Date getEndDate(Date defaultValue)
    {
        if(isDefined(endDateDef))
        {
            return (Date)get(endDateDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>endDate</code> attribute.
     *
     * @param value the value of the <code>endDate</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEndDate(Date value)
    {
        try
        {
            if(value != null)
            {
                set(endDateDef, value);
            }
            else
            {
                unset(endDateDef);
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
	 * Checks if the value of the <code>endDate</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>endDate</code> attribute is defined.
	 */
    public boolean isEndDateDefined()
	{
	    return isDefined(endDateDef);
	}

    /**
     * Returns the value of the <code>eternal</code> attribute.
     *
     * @return the value of the <code>eternal</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getEternal()
        throws IllegalStateException
    {
        if(isDefined(eternalDef))
        {
            return ((Boolean)get(eternalDef)).booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute eternal is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>eternal</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>eternal</code> attribute.
     */
    public boolean getEternal(boolean defaultValue)
    {
        if(isDefined(eternalDef))
        {
            return ((Boolean)get(eternalDef)).booleanValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>eternal</code> attribute.
     *
     * @param value the value of the <code>eternal</code> attribute.
     */
    public void setEternal(boolean value)
    {
        try
        {
            set(eternalDef, new Boolean(value));
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
     * Removes the value of the <code>eternal</code> attribute.
     */
    public void unsetEternal()
    {
        try
        {
            unset(eternalDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>eternal</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>eternal</code> attribute is defined.
	 */
    public boolean isEternalDefined()
	{
	    return isDefined(eternalDef);
	}
 
    /**
     * Returns the value of the <code>startDate</code> attribute.
     *
     * @return the value of the <code>startDate</code> attribute.
     */
    public Date getStartDate()
    {
        return (Date)get(startDateDef);
    }
    
    /**
     * Returns the value of the <code>startDate</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>startDate</code> attribute.
     */
    public Date getStartDate(Date defaultValue)
    {
        if(isDefined(startDateDef))
        {
            return (Date)get(startDateDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>startDate</code> attribute.
     *
     * @param value the value of the <code>startDate</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setStartDate(Date value)
    {
        try
        {
            if(value != null)
            {
                set(startDateDef, value);
            }
            else
            {
                unset(startDateDef);
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
	 * Checks if the value of the <code>startDate</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>startDate</code> attribute is defined.
	 */
    public boolean isStartDateDefined()
	{
	    return isDefined(startDateDef);
	}
 
    /**
     * Returns the value of the <code>state</code> attribute.
     *
     * @return the value of the <code>state</code> attribute.
     */
    public StateResource getState()
    {
        return (StateResource)get(stateDef);
    }
    
    /**
     * Returns the value of the <code>state</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>state</code> attribute.
     */
    public StateResource getState(StateResource defaultValue)
    {
        if(isDefined(stateDef))
        {
            return (StateResource)get(stateDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>state</code> attribute.
     *
     * @param value the value of the <code>state</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setState(StateResource value)
    {
        try
        {
            if(value != null)
            {
                set(stateDef, value);
            }
            else
            {
                unset(stateDef);
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
	 * Checks if the value of the <code>state</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>state</code> attribute is defined.
	 */
    public boolean isStateDefined()
	{
	    return isDefined(stateDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
    // @extends node
    // @import net.cyklotron.cms.CmsData
    // @import java.util.Date
    // @import org.objectledge.context.Context
    // @import org.objectledge.coral.security.Subject
    // @import net.cyklotron.cms.CmsConstants

    /**
     * Checks if this resource can be viewed at the given time.
     */
    public boolean isValid(Context context, Date time)
    {
        if(time.before(getStartDate()))
        {
            return false;
        }
        if(getEternal())
        {
            return true;
        }
        return time.before(getEndDate());
    }

    public boolean canView(Context context, Subject subject)
    {
        return true;
    }

    /**
     * Checks if the specified subject can modify this resource.
     */
    public boolean canModify(Context context, Subject subject)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the specified subject can remove this resource.
     */
    public boolean canRemove(Context context, Subject subject)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the specified subject can add children to this resource.
     */
    public boolean canAddChild(Context context, Subject subject)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the specified subject can view this resource at the given time.
     */
    public boolean canView(Context context, Subject subject, Date time)
    {
        if(!canView(context, subject))
        {
            return false;
        }
        return isValid(context, time);
    }

    /**
     * Checks if the specified subject can view this resource
     */
    public boolean canView(Context context, CmsData data, Subject subject)
    {
        if(data.getBrowseMode().equals(CmsConstants.BROWSE_MODE_ADMINISTER))
        {
            return canView(context, subject);
        }
        else
        {
            return canView(context, subject, data.getDate());
        }
    }

    public String getIndexAbbreviation()
    {
        return getDescription();
    }
    
    public String getIndexContent()
    {
        return null;
    }
    
    public String getIndexTitle()
    {
        return getName();
    }
    
    public Object getFieldValue(String fieldName)
    {
        return null;
    }

	/**
	 * Returns the store flag of the field.
	 *
	 * @return the store flag.
	 */
	public boolean isStored(String fieldName)
	{
		return false;
	}
	
	/**
	 * Returns the indexed flag of the field.
	 *
	 * @return the indexed flag.
	 */
	public boolean isIndexed(String fieldName)
	{
		return false;
	}
		
	/**
	 * Returns the tokenized flag of the field.
	 *
	 * @return the tokenized flag.
	 */
	public boolean isTokenized(String fieldName)
	{
		return false;
	}
}
