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
 
package net.cyklotron.cms.httpfeed;

import java.util.Date;

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
 * An implementation of <code>cms.httpfeed.feed</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class HttpFeedResourceImpl
    extends CmsNodeResourceImpl
    implements HttpFeedResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>contents</code> attribute. */
	private static AttributeDefinition<String> contentsDef;

    /** The AttributeDefinition object for the <code>failedUpdates</code> attribute. */
    private static AttributeDefinition<Integer> failedUpdatesDef;

    /** The AttributeDefinition object for the <code>interval</code> attribute. */
    private static AttributeDefinition<Integer> intervalDef;

    /** The AttributeDefinition object for the <code>lastUpdate</code> attribute. */
	private static AttributeDefinition<Date> lastUpdateDef;

    /** The AttributeDefinition object for the <code>url</code> attribute. */
	private static AttributeDefinition<String> urlDef;

    /** The AttributeDefinition object for the <code>validity</code> attribute. */
    private static AttributeDefinition<Boolean> validityDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.httpfeed.feed</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public HttpFeedResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.httpfeed.feed</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static HttpFeedResource getHttpFeedResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof HttpFeedResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.httpfeed.feed");
        }
        return (HttpFeedResource)res;
    }

    /**
     * Creates a new <code>cms.httpfeed.feed</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new HttpFeedResource instance.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static HttpFeedResource createHttpFeedResource(CoralSession session, String name,
        Resource parent)
        throws InvalidResourceNameException
    {
        try
        {
            ResourceClass<HttpFeedResource> rc = session.getSchema().getResourceClass("cms.httpfeed.feed", HttpFeedResource.class);
		    Resource res = session.getStore().createResource(name, parent, rc,
                java.util.Collections.<AttributeDefinition<?>, Object> emptyMap());			
            if(!(res instanceof HttpFeedResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (HttpFeedResource)res;
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
     * Returns the value of the <code>contents</code> attribute.
     *
     * @return the value of the <code>contents</code> attribute.
     */
    public String getContents()
    {
        return get(contentsDef);
    }
    
    /**
     * Returns the value of the <code>contents</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>contents</code> attribute.
     */
    public String getContents(String defaultValue)
    {
        return get(contentsDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>contents</code> attribute.
     *
     * @param value the value of the <code>contents</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setContents(String value)
    {
        try
        {
            if(value != null)
            {
                set(contentsDef, value);
            }
            else
            {
                unset(contentsDef);
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
	 * Checks if the value of the <code>contents</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>contents</code> attribute is defined.
	 */
    public boolean isContentsDefined()
	{
	    return isDefined(contentsDef);
	}

    /**
     * Returns the value of the <code>failedUpdates</code> attribute.
     *
     * @return the value of the <code>failedUpdates</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getFailedUpdates()
        throws IllegalStateException
    {
	    Integer value = get(failedUpdatesDef);
        if(value != null)
        {
            return value.intValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute failedUpdates is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>failedUpdates</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>failedUpdates</code> attribute.
     */
    public int getFailedUpdates(int defaultValue)
    {
		return get(failedUpdatesDef, Integer.valueOf(defaultValue)).intValue();
	}

    /**
     * Sets the value of the <code>failedUpdates</code> attribute.
     *
     * @param value the value of the <code>failedUpdates</code> attribute.
     */
    public void setFailedUpdates(int value)
    {
        try
        {
            set(failedUpdatesDef, Integer.valueOf(value));
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
     * Removes the value of the <code>failedUpdates</code> attribute.
     */
    public void unsetFailedUpdates()
    {
        try
        {
            unset(failedUpdatesDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>failedUpdates</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>failedUpdates</code> attribute is defined.
	 */
    public boolean isFailedUpdatesDefined()
	{
	    return isDefined(failedUpdatesDef);
	}

    /**
     * Returns the value of the <code>interval</code> attribute.
     *
     * @return the value of the <code>interval</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getInterval()
        throws IllegalStateException
    {
	    Integer value = get(intervalDef);
        if(value != null)
        {
            return value.intValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute interval is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>interval</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>interval</code> attribute.
     */
    public int getInterval(int defaultValue)
    {
		return get(intervalDef, Integer.valueOf(defaultValue)).intValue();
	}

    /**
     * Sets the value of the <code>interval</code> attribute.
     *
     * @param value the value of the <code>interval</code> attribute.
     */
    public void setInterval(int value)
    {
        try
        {
            set(intervalDef, Integer.valueOf(value));
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
     * Removes the value of the <code>interval</code> attribute.
     */
    public void unsetInterval()
    {
        try
        {
            unset(intervalDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>interval</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>interval</code> attribute is defined.
	 */
    public boolean isIntervalDefined()
	{
	    return isDefined(intervalDef);
	}
 
    /**
     * Returns the value of the <code>lastUpdate</code> attribute.
     *
     * @return the value of the <code>lastUpdate</code> attribute.
     */
    public Date getLastUpdate()
    {
        return get(lastUpdateDef);
    }
    
    /**
     * Returns the value of the <code>lastUpdate</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lastUpdate</code> attribute.
     */
    public Date getLastUpdate(Date defaultValue)
    {
        return get(lastUpdateDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>lastUpdate</code> attribute.
     *
     * @param value the value of the <code>lastUpdate</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLastUpdate(Date value)
    {
        try
        {
            if(value != null)
            {
                set(lastUpdateDef, value);
            }
            else
            {
                unset(lastUpdateDef);
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
	 * Checks if the value of the <code>lastUpdate</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lastUpdate</code> attribute is defined.
	 */
    public boolean isLastUpdateDefined()
	{
	    return isDefined(lastUpdateDef);
	}
 
    /**
     * Returns the value of the <code>url</code> attribute.
     *
     * @return the value of the <code>url</code> attribute.
     */
    public String getUrl()
    {
        return get(urlDef);
    }
    
    /**
     * Returns the value of the <code>url</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>url</code> attribute.
     */
    public String getUrl(String defaultValue)
    {
        return get(urlDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>url</code> attribute.
     *
     * @param value the value of the <code>url</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setUrl(String value)
    {
        try
        {
            if(value != null)
            {
                set(urlDef, value);
            }
            else
            {
                unset(urlDef);
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
	 * Checks if the value of the <code>url</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>url</code> attribute is defined.
	 */
    public boolean isUrlDefined()
	{
	    return isDefined(urlDef);
	}

    /**
     * Returns the value of the <code>validity</code> attribute.
     *
     * @return the value of the <code>validity</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getValidity()
        throws IllegalStateException
    {
	    Boolean value = get(validityDef);
        if(value != null)
        {
            return value.booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute validity is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>validity</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>validity</code> attribute.
     */
    public boolean getValidity(boolean defaultValue)
    {
		return get(validityDef, Boolean.valueOf(defaultValue)).booleanValue();
	}

    /**
     * Sets the value of the <code>validity</code> attribute.
     *
     * @param value the value of the <code>validity</code> attribute.
     */
    public void setValidity(boolean value)
    {
        try
        {
            set(validityDef, Boolean.valueOf(value));
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
     * Removes the value of the <code>validity</code> attribute.
     */
    public void unsetValidity()
    {
        try
        {
            unset(validityDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>validity</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>validity</code> attribute is defined.
	 */
    public boolean isValidityDefined()
	{
	    return isDefined(validityDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
}
