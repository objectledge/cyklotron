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

import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.CmsConstants;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsNodeResourceImpl;
import net.cyklotron.cms.workflow.StateResource;

/**
 * An implementation of <code>cms.banner.banner</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class BannerResourceImpl
    extends CmsNodeResourceImpl
    implements BannerResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
	@SuppressWarnings("unused")
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>altText</code> attribute. */
	private static AttributeDefinition<String> altTextDef;

    /** The AttributeDefinition object for the <code>endDate</code> attribute. */
	private static AttributeDefinition<Date> endDateDef;

    /** The AttributeDefinition object for the <code>expositionCounter</code> attribute. */
    private static AttributeDefinition<Integer> expositionCounterDef;

    /** The AttributeDefinition object for the <code>followedCounter</code> attribute. */
    private static AttributeDefinition<Integer> followedCounterDef;

    /** The AttributeDefinition object for the <code>startDate</code> attribute. */
	private static AttributeDefinition<Date> startDateDef;

    /** The AttributeDefinition object for the <code>state</code> attribute. */
	private static AttributeDefinition<StateResource> stateDef;

    /** The AttributeDefinition object for the <code>target</code> attribute. */
	private static AttributeDefinition<String> targetDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.banner.banner</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public BannerResourceImpl()
    {
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
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static BannerResource createBannerResource(CoralSession session, String name,
        Resource parent)
        throws InvalidResourceNameException
    {
        try
        {
            ResourceClass<BannerResource> rc = session.getSchema().getResourceClass("cms.banner.banner", BannerResource.class);
		    Resource res = session.getStore().createResource(name, parent, rc,
                java.util.Collections.<AttributeDefinition<?>, Object> emptyMap());			
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
     * Returns the value of the <code>altText</code> attribute.
     *
     * @return the value of the <code>altText</code> attribute.
     */
    public String getAltText()
    {
        return get(altTextDef);
    }
    
    /**
     * Returns the value of the <code>altText</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>altText</code> attribute.
     */
    public String getAltText(String defaultValue)
    {
        return get(altTextDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>altText</code> attribute.
     *
     * @param value the value of the <code>altText</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAltText(String value)
    {
        try
        {
            if(value != null)
            {
                set(altTextDef, value);
            }
            else
            {
                unset(altTextDef);
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
	 * Checks if the value of the <code>altText</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>altText</code> attribute is defined.
	 */
    public boolean isAltTextDefined()
	{
	    return isDefined(altTextDef);
	}
 
    /**
     * Returns the value of the <code>endDate</code> attribute.
     *
     * @return the value of the <code>endDate</code> attribute.
     */
    public Date getEndDate()
    {
        return get(endDateDef);
    }
    
    /**
     * Returns the value of the <code>endDate</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>endDate</code> attribute.
     */
    public Date getEndDate(Date defaultValue)
    {
        return get(endDateDef, defaultValue);
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
     * Returns the value of the <code>expositionCounter</code> attribute.
     *
     * @return the value of the <code>expositionCounter</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getExpositionCounter()
        throws IllegalStateException
    {
	    Integer value = get(expositionCounterDef);
        if(value != null)
        {
            return value.intValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute expositionCounter is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>expositionCounter</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>expositionCounter</code> attribute.
     */
    public int getExpositionCounter(int defaultValue)
    {
		return get(expositionCounterDef, Integer.valueOf(defaultValue)).intValue();
	}

    /**
     * Sets the value of the <code>expositionCounter</code> attribute.
     *
     * @param value the value of the <code>expositionCounter</code> attribute.
     */
    public void setExpositionCounter(int value)
    {
        try
        {
            set(expositionCounterDef, Integer.valueOf(value));
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
     * Removes the value of the <code>expositionCounter</code> attribute.
     */
    public void unsetExpositionCounter()
    {
        try
        {
            unset(expositionCounterDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>expositionCounter</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>expositionCounter</code> attribute is defined.
	 */
    public boolean isExpositionCounterDefined()
	{
	    return isDefined(expositionCounterDef);
	}

    /**
     * Returns the value of the <code>followedCounter</code> attribute.
     *
     * @return the value of the <code>followedCounter</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getFollowedCounter()
        throws IllegalStateException
    {
	    Integer value = get(followedCounterDef);
        if(value != null)
        {
            return value.intValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute followedCounter is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>followedCounter</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>followedCounter</code> attribute.
     */
    public int getFollowedCounter(int defaultValue)
    {
		return get(followedCounterDef, Integer.valueOf(defaultValue)).intValue();
	}

    /**
     * Sets the value of the <code>followedCounter</code> attribute.
     *
     * @param value the value of the <code>followedCounter</code> attribute.
     */
    public void setFollowedCounter(int value)
    {
        try
        {
            set(followedCounterDef, Integer.valueOf(value));
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
     * Removes the value of the <code>followedCounter</code> attribute.
     */
    public void unsetFollowedCounter()
    {
        try
        {
            unset(followedCounterDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>followedCounter</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>followedCounter</code> attribute is defined.
	 */
    public boolean isFollowedCounterDefined()
	{
	    return isDefined(followedCounterDef);
	}
 
    /**
     * Returns the value of the <code>startDate</code> attribute.
     *
     * @return the value of the <code>startDate</code> attribute.
     */
    public Date getStartDate()
    {
        return get(startDateDef);
    }
    
    /**
     * Returns the value of the <code>startDate</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>startDate</code> attribute.
     */
    public Date getStartDate(Date defaultValue)
    {
        return get(startDateDef, defaultValue);
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
        return get(stateDef);
    }
    
    /**
     * Returns the value of the <code>state</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>state</code> attribute.
     */
    public StateResource getState(StateResource defaultValue)
    {
        return get(stateDef, defaultValue);
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
        return get(targetDef);
    }
    
    /**
     * Returns the value of the <code>target</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>target</code> attribute.
     */
    public String getTarget(String defaultValue)
    {
        return get(targetDef, defaultValue);
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

    // @extends node
    // @import net.cyklotron.cms.CmsData
    // @import org.objectledge.coral.security.Subject
    // @import org.objectledge.coral.session.CoralSession
    // @import net.cyklotron.cms.CmsConstants
    
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

    public boolean canView(CoralSession coralSession, Subject subject)
    {
        return true;
    }

    /**
     * Checks if the specified subject can modify this resource.
     */
    public boolean canModify(CoralSession coralSession, Subject subject)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the specified subject can remove this resource.
     */
    public boolean canRemove(CoralSession coralSession, Subject subject)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the specified subject can add children to this resource.
     */
    public boolean canAddChild(CoralSession coralSession, Subject subject)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the specified subject can view this resource at the given time.
     */
    public boolean canView(CoralSession coralSession, Subject subject, Date time)
    {
        if(!canView(coralSession, subject))
        {
            return false;
        }
        return isValid(time);
    }

    /**
     * Checks if the specified subject can view this resource
     */
    public boolean canView(CoralSession coralSession, CmsData data, Subject subject)
    {
        if(data.getBrowseMode().equals(CmsConstants.BROWSE_MODE_ADMINISTER))
        {
            return canView(coralSession, subject);
        }
        else
        {
            return canView(coralSession, subject, data.getDate());
        }
    }
}
