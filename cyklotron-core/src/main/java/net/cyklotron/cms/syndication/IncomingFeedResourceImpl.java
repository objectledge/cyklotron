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
 
package net.cyklotron.cms.syndication;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
 * An implementation of <code>cms.syndication.incomingfeed</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class IncomingFeedResourceImpl
    extends CmsNodeResourceImpl
    implements IncomingFeedResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>contents</code> attribute. */
    private static AttributeDefinition contentsDef;

    /** The AttributeDefinition object for the <code>failedUpdates</code> attribute. */
    private static AttributeDefinition failedUpdatesDef;

    /** The AttributeDefinition object for the <code>interval</code> attribute. */
    private static AttributeDefinition intervalDef;

    /** The AttributeDefinition object for the <code>lastUpdate</code> attribute. */
    private static AttributeDefinition lastUpdateDef;

    /** The AttributeDefinition object for the <code>transformationTemplate</code> attribute. */
    private static AttributeDefinition transformationTemplateDef;

    /** The AttributeDefinition object for the <code>updateErrorKey</code> attribute. */
    private static AttributeDefinition updateErrorKeyDef;

    /** The AttributeDefinition object for the <code>url</code> attribute. */
    private static AttributeDefinition urlDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.syndication.incomingfeed</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public IncomingFeedResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.syndication.incomingfeed</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static IncomingFeedResource getIncomingFeedResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof IncomingFeedResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.syndication.incomingfeed");
        }
        return (IncomingFeedResource)res;
    }

    /**
     * Creates a new <code>cms.syndication.incomingfeed</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new IncomingFeedResource instance.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static IncomingFeedResource createIncomingFeedResource(CoralSession session, String
        name, Resource parent)
        throws InvalidResourceNameException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.syndication.incomingfeed");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof IncomingFeedResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (IncomingFeedResource)res;
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
        return (String)getInternal(contentsDef, null);
    }
    
    /**
     * Returns the value of the <code>contents</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>contents</code> attribute.
     */
    public String getContents(String defaultValue)
    {
        return (String)getInternal(contentsDef, defaultValue);
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
	    Integer value = (Integer)getInternal(failedUpdatesDef, null);
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
		return ((Integer)getInternal(failedUpdatesDef, new Integer(defaultValue))).intValue();
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
            set(failedUpdatesDef, new Integer(value));
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
	    Integer value = (Integer)getInternal(intervalDef, null);
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
		return ((Integer)getInternal(intervalDef, new Integer(defaultValue))).intValue();
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
            set(intervalDef, new Integer(value));
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
        return (Date)getInternal(lastUpdateDef, null);
    }
    
    /**
     * Returns the value of the <code>lastUpdate</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lastUpdate</code> attribute.
     */
    public Date getLastUpdate(Date defaultValue)
    {
        return (Date)getInternal(lastUpdateDef, defaultValue);
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
     * Returns the value of the <code>transformationTemplate</code> attribute.
     *
     * @return the value of the <code>transformationTemplate</code> attribute.
     */
    public String getTransformationTemplate()
    {
        return (String)getInternal(transformationTemplateDef, null);
    }
    
    /**
     * Returns the value of the <code>transformationTemplate</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>transformationTemplate</code> attribute.
     */
    public String getTransformationTemplate(String defaultValue)
    {
        return (String)getInternal(transformationTemplateDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>transformationTemplate</code> attribute.
     *
     * @param value the value of the <code>transformationTemplate</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTransformationTemplate(String value)
    {
        try
        {
            if(value != null)
            {
                set(transformationTemplateDef, value);
            }
            else
            {
                unset(transformationTemplateDef);
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
	 * Checks if the value of the <code>transformationTemplate</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>transformationTemplate</code> attribute is defined.
	 */
    public boolean isTransformationTemplateDefined()
	{
	    return isDefined(transformationTemplateDef);
	}
 
    /**
     * Returns the value of the <code>updateErrorKey</code> attribute.
     *
     * @return the value of the <code>updateErrorKey</code> attribute.
     */
    public String getUpdateErrorKey()
    {
        return (String)getInternal(updateErrorKeyDef, null);
    }
    
    /**
     * Returns the value of the <code>updateErrorKey</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>updateErrorKey</code> attribute.
     */
    public String getUpdateErrorKey(String defaultValue)
    {
        return (String)getInternal(updateErrorKeyDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>updateErrorKey</code> attribute.
     *
     * @param value the value of the <code>updateErrorKey</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setUpdateErrorKey(String value)
    {
        try
        {
            if(value != null)
            {
                set(updateErrorKeyDef, value);
            }
            else
            {
                unset(updateErrorKeyDef);
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
	 * Checks if the value of the <code>updateErrorKey</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>updateErrorKey</code> attribute is defined.
	 */
    public boolean isUpdateErrorKeyDefined()
	{
	    return isDefined(updateErrorKeyDef);
	}
 
    /**
     * Returns the value of the <code>url</code> attribute.
     *
     * @return the value of the <code>url</code> attribute.
     */
    public String getUrl()
    {
        return (String)getInternal(urlDef, null);
    }
    
    /**
     * Returns the value of the <code>url</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>url</code> attribute.
     */
    public String getUrl(String defaultValue)
    {
        return (String)getInternal(urlDef, defaultValue);
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
  
    // @custom methods ///////////////////////////////////////////////////////
}
