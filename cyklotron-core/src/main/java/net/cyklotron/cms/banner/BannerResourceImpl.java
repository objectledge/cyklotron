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
 
package net.cyklotron.cms.banner;

import java.util.Date;
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

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.workflow.StateResource;
import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>cms.banner.banner</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class BannerResourceImpl
    extends NodeImpl
    implements BannerResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>alt_text</code> attribute. */
    private AttributeDefinition alt_textDef;

    /** The AttributeDefinition object for the <code>end_date</code> attribute. */
    private AttributeDefinition end_dateDef;

    /** The AttributeDefinition object for the <code>exposition_counter</code> attribute. */
    private AttributeDefinition exposition_counterDef;

    /** The AttributeDefinition object for the <code>followed_counter</code> attribute. */
    private AttributeDefinition followed_counterDef;

    /** The AttributeDefinition object for the <code>start_date</code> attribute. */
    private AttributeDefinition start_dateDef;

    /** The AttributeDefinition object for the <code>state</code> attribute. */
    private AttributeDefinition stateDef;

    /** The AttributeDefinition object for the <code>target</code> attribute. */
    private AttributeDefinition targetDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.banner.banner</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public BannerResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("cms.banner.banner");
            alt_textDef = rc.getAttribute("alt_text");
            end_dateDef = rc.getAttribute("end_date");
            exposition_counterDef = rc.getAttribute("exposition_counter");
            followed_counterDef = rc.getAttribute("followed_counter");
            start_dateDef = rc.getAttribute("start_date");
            stateDef = rc.getAttribute("state");
            targetDef = rc.getAttribute("target");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.banner.banner</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static BannerResource getBannerResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof BannerResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.banner.banner");
        }
        return (BannerResource)res;
    }

    /**
     * Creates a new <code>cms.banner.banner</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new BannerResource instance.
     */
    public static BannerResource createBannerResource(CoralSession session, String name,
        Resource parent)
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.banner.banner");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof BannerResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (BannerResource)res;
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
     * Returns the value of the <code>alt_text</code> attribute.
     *
     * @return the value of the <code>alt_text</code> attribute.
     */
    public String getAlt_text()
    {
        return (String)get(alt_textDef);
    }
    
    /**
     * Returns the value of the <code>alt_text</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>alt_text</code> attribute.
     */
    public String getAlt_text(String defaultValue)
    {
        if(isDefined(alt_textDef))
        {
            return (String)get(alt_textDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>alt_text</code> attribute.
     *
     * @param value the value of the <code>alt_text</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAlt_text(String value)
    {
        try
        {
            if(value != null)
            {
                set(alt_textDef, value);
            }
            else
            {
                unset(alt_textDef);
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
	 * Checks if the value of the <code>alt_text</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>alt_text</code> attribute is defined.
	 */
    public boolean isAlt_textDefined()
	{
	    return isDefined(alt_textDef);
	}
 
    /**
     * Returns the value of the <code>end_date</code> attribute.
     *
     * @return the value of the <code>end_date</code> attribute.
     */
    public Date getEnd_date()
    {
        return (Date)get(end_dateDef);
    }
    
    /**
     * Returns the value of the <code>end_date</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>end_date</code> attribute.
     */
    public Date getEnd_date(Date defaultValue)
    {
        if(isDefined(end_dateDef))
        {
            return (Date)get(end_dateDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>end_date</code> attribute.
     *
     * @param value the value of the <code>end_date</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEnd_date(Date value)
    {
        try
        {
            if(value != null)
            {
                set(end_dateDef, value);
            }
            else
            {
                unset(end_dateDef);
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
	 * Checks if the value of the <code>end_date</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>end_date</code> attribute is defined.
	 */
    public boolean isEnd_dateDefined()
	{
	    return isDefined(end_dateDef);
	}

    /**
     * Returns the value of the <code>exposition_counter</code> attribute.
     *
     * @return the value of the <code>exposition_counter</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getExposition_counter()
        throws IllegalStateException
    {
        if(isDefined(exposition_counterDef))
        {
            return ((Integer)get(exposition_counterDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("attribute value is undefined");
        }
    }

    /**
     * Returns the value of the <code>exposition_counter</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>exposition_counter</code> attribute.
     */
    public int getExposition_counter(int defaultValue)
    {
        if(isDefined(exposition_counterDef))
        {
            return ((Integer)get(exposition_counterDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>exposition_counter</code> attribute.
     *
     * @param value the value of the <code>exposition_counter</code> attribute.
     */
    public void setExposition_counter(int value)
    {
        try
        {
            set(exposition_counterDef, new Integer(value));
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
     * Removes the value of the <code>exposition_counter</code> attribute.
     */
    public void unsetExposition_counter()
    {
        try
        {
            unset(exposition_counterDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>exposition_counter</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>exposition_counter</code> attribute is defined.
	 */
    public boolean isExposition_counterDefined()
	{
	    return isDefined(exposition_counterDef);
	}

    /**
     * Returns the value of the <code>followed_counter</code> attribute.
     *
     * @return the value of the <code>followed_counter</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getFollowed_counter()
        throws IllegalStateException
    {
        if(isDefined(followed_counterDef))
        {
            return ((Integer)get(followed_counterDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("attribute value is undefined");
        }
    }

    /**
     * Returns the value of the <code>followed_counter</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>followed_counter</code> attribute.
     */
    public int getFollowed_counter(int defaultValue)
    {
        if(isDefined(followed_counterDef))
        {
            return ((Integer)get(followed_counterDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>followed_counter</code> attribute.
     *
     * @param value the value of the <code>followed_counter</code> attribute.
     */
    public void setFollowed_counter(int value)
    {
        try
        {
            set(followed_counterDef, new Integer(value));
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
     * Removes the value of the <code>followed_counter</code> attribute.
     */
    public void unsetFollowed_counter()
    {
        try
        {
            unset(followed_counterDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>followed_counter</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>followed_counter</code> attribute is defined.
	 */
    public boolean isFollowed_counterDefined()
	{
	    return isDefined(followed_counterDef);
	}
 
    /**
     * Returns the value of the <code>start_date</code> attribute.
     *
     * @return the value of the <code>start_date</code> attribute.
     */
    public Date getStart_date()
    {
        return (Date)get(start_dateDef);
    }
    
    /**
     * Returns the value of the <code>start_date</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>start_date</code> attribute.
     */
    public Date getStart_date(Date defaultValue)
    {
        if(isDefined(start_dateDef))
        {
            return (Date)get(start_dateDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>start_date</code> attribute.
     *
     * @param value the value of the <code>start_date</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setStart_date(Date value)
    {
        try
        {
            if(value != null)
            {
                set(start_dateDef, value);
            }
            else
            {
                unset(start_dateDef);
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
	 * Checks if the value of the <code>start_date</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>start_date</code> attribute is defined.
	 */
    public boolean isStart_dateDefined()
	{
	    return isDefined(start_dateDef);
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
 
    /**
     * Returns the value of the <code>target</code> attribute.
     *
     * @return the value of the <code>target</code> attribute.
     */
    public String getTarget()
    {
        return (String)get(targetDef);
    }
    
    /**
     * Returns the value of the <code>target</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>target</code> attribute.
     */
    public String getTarget(String defaultValue)
    {
        if(isDefined(targetDef))
        {
            return (String)get(targetDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>target</code> attribute.
     *
     * @param value the value of the <code>target</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTarget(String value)
    {
        try
        {
            if(value != null)
            {
                set(targetDef, value);
            }
            else
            {
                unset(targetDef);
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
	 * Checks if the value of the <code>target</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>target</code> attribute is defined.
	 */
    public boolean isTargetDefined()
	{
	    return isDefined(targetDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////

    // @extends coral.Node
    // @import net.cyklotron.cms.CmsData

    /**
     * Checks if this resource can be viewed at the given time.
     */
    public boolean isValid(Date time)
    {
        if(time.before(getStartDate()))
        {
            return false;
        }
        return time.before(getEndDate());
    }

    public boolean canView(Subject subject)
    {
        return true;
    }

    /**
     * Checks if the specified subject can modify this resource.
     */
    public boolean canModify(Subject subject)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the specified subject can remove this resource.
     */
    public boolean canRemove(Subject subject)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the specified subject can add children to this resource.
     */
    public boolean canAddChild(Subject subject)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the specified subject can view this resource at the given time.
     */
    public boolean canView(Subject subject, Date time)
    {
        if(!canView(subject))
        {
            return false;
        }
        return isValid(time);
    }

    /**
     * Checks if the specified subject can view this resource
     */
    public boolean canView(CmsData data, Subject subject)
    {
        if(data.getBrowseMode().equals(CmsData.BROWSE_MODE_ADMINISTER))
        {
            return canView(subject);
        }
        else
        {
            return canView(subject, data.getDate());
        }
    }
}
