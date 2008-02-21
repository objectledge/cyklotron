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

import java.util.ArrayList;
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

import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.site.SiteResource;

/**
 * An implementation of <code>cms.periodicals.periodical</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class PeriodicalResourceImpl
    extends PeriodicalsNodeResourceImpl
    implements PeriodicalResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>categoryQuerySet</code> attribute. */
    private static AttributeDefinition categoryQuerySetDef;

    /** The AttributeDefinition object for the <code>encoding</code> attribute. */
    private static AttributeDefinition encodingDef;

    /** The AttributeDefinition object for the <code>lastPublished</code> attribute. */
    private static AttributeDefinition lastPublishedDef;

    /** The AttributeDefinition object for the <code>locale</code> attribute. */
    private static AttributeDefinition localeDef;

    /** The AttributeDefinition object for the <code>publishAfter</code> attribute. */
    private static AttributeDefinition publishAfterDef;

    /** The AttributeDefinition object for the <code>renderer</code> attribute. */
    private static AttributeDefinition rendererDef;

    /** The AttributeDefinition object for the <code>sortDirection</code> attribute. */
    private static AttributeDefinition sortDirectionDef;

    /** The AttributeDefinition object for the <code>sortOrder</code> attribute. */
    private static AttributeDefinition sortOrderDef;

    /** The AttributeDefinition object for the <code>storePlace</code> attribute. */
    private static AttributeDefinition storePlaceDef;

    /** The AttributeDefinition object for the <code>template</code> attribute. */
    private static AttributeDefinition templateDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.periodicals.periodical</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public PeriodicalResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.periodicals.periodical</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static PeriodicalResource getPeriodicalResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof PeriodicalResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.periodicals.periodical");
        }
        return (PeriodicalResource)res;
    }

    /**
     * Creates a new <code>cms.periodicals.periodical</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new PeriodicalResource instance.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static PeriodicalResource createPeriodicalResource(CoralSession session, String name,
        Resource parent)
        throws InvalidResourceNameException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.periodicals.periodical");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof PeriodicalResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (PeriodicalResource)res;
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
     * Returns the value of the <code>categoryQuerySet</code> attribute.
     *
     * @return the value of the <code>categoryQuerySet</code> attribute.
     */
    public CategoryQueryPoolResource getCategoryQuerySet()
    {
        return (CategoryQueryPoolResource)getInternal(categoryQuerySetDef, null);
    }
    
    /**
     * Returns the value of the <code>categoryQuerySet</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>categoryQuerySet</code> attribute.
     */
    public CategoryQueryPoolResource getCategoryQuerySet(CategoryQueryPoolResource defaultValue)
    {
        return (CategoryQueryPoolResource)getInternal(categoryQuerySetDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>categoryQuerySet</code> attribute.
     *
     * @param value the value of the <code>categoryQuerySet</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCategoryQuerySet(CategoryQueryPoolResource value)
    {
        try
        {
            if(value != null)
            {
                set(categoryQuerySetDef, value);
            }
            else
            {
                unset(categoryQuerySetDef);
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
	 * Checks if the value of the <code>categoryQuerySet</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>categoryQuerySet</code> attribute is defined.
	 */
    public boolean isCategoryQuerySetDefined()
	{
	    return isDefined(categoryQuerySetDef);
	}
 
    /**
     * Returns the value of the <code>encoding</code> attribute.
     *
     * @return the value of the <code>encoding</code> attribute.
     */
    public String getEncoding()
    {
        return (String)getInternal(encodingDef, null);
    }
    
    /**
     * Returns the value of the <code>encoding</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>encoding</code> attribute.
     */
    public String getEncoding(String defaultValue)
    {
        return (String)getInternal(encodingDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>encoding</code> attribute.
     *
     * @param value the value of the <code>encoding</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEncoding(String value)
    {
        try
        {
            if(value != null)
            {
                set(encodingDef, value);
            }
            else
            {
                unset(encodingDef);
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
	 * Checks if the value of the <code>encoding</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>encoding</code> attribute is defined.
	 */
    public boolean isEncodingDefined()
	{
	    return isDefined(encodingDef);
	}
 
    /**
     * Returns the value of the <code>lastPublished</code> attribute.
     *
     * @return the value of the <code>lastPublished</code> attribute.
     */
    public Date getLastPublished()
    {
        return (Date)getInternal(lastPublishedDef, null);
    }
    
    /**
     * Returns the value of the <code>lastPublished</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lastPublished</code> attribute.
     */
    public Date getLastPublished(Date defaultValue)
    {
        return (Date)getInternal(lastPublishedDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>lastPublished</code> attribute.
     *
     * @param value the value of the <code>lastPublished</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLastPublished(Date value)
    {
        try
        {
            if(value != null)
            {
                set(lastPublishedDef, value);
            }
            else
            {
                unset(lastPublishedDef);
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
	 * Checks if the value of the <code>lastPublished</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lastPublished</code> attribute is defined.
	 */
    public boolean isLastPublishedDefined()
	{
	    return isDefined(lastPublishedDef);
	}
 
    /**
     * Returns the value of the <code>locale</code> attribute.
     *
     * @return the value of the <code>locale</code> attribute.
     */
    public String getLocale()
    {
        return (String)getInternal(localeDef, null);
    }
    
    /**
     * Returns the value of the <code>locale</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>locale</code> attribute.
     */
    public String getLocale(String defaultValue)
    {
        return (String)getInternal(localeDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>locale</code> attribute.
     *
     * @param value the value of the <code>locale</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLocale(String value)
    {
        try
        {
            if(value != null)
            {
                set(localeDef, value);
            }
            else
            {
                unset(localeDef);
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
	 * Checks if the value of the <code>locale</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>locale</code> attribute is defined.
	 */
    public boolean isLocaleDefined()
	{
	    return isDefined(localeDef);
	}
 
    /**
     * Returns the value of the <code>publishAfter</code> attribute.
     *
     * @return the value of the <code>publishAfter</code> attribute.
     */
    public Date getPublishAfter()
    {
        return (Date)getInternal(publishAfterDef, null);
    }
    
    /**
     * Returns the value of the <code>publishAfter</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>publishAfter</code> attribute.
     */
    public Date getPublishAfter(Date defaultValue)
    {
        return (Date)getInternal(publishAfterDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>publishAfter</code> attribute.
     *
     * @param value the value of the <code>publishAfter</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setPublishAfter(Date value)
    {
        try
        {
            if(value != null)
            {
                set(publishAfterDef, value);
            }
            else
            {
                unset(publishAfterDef);
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
	 * Checks if the value of the <code>publishAfter</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>publishAfter</code> attribute is defined.
	 */
    public boolean isPublishAfterDefined()
	{
	    return isDefined(publishAfterDef);
	}
 
    /**
     * Returns the value of the <code>renderer</code> attribute.
     *
     * @return the value of the <code>renderer</code> attribute.
     */
    public String getRenderer()
    {
        return (String)getInternal(rendererDef, null);
    }
    
    /**
     * Returns the value of the <code>renderer</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>renderer</code> attribute.
     */
    public String getRenderer(String defaultValue)
    {
        return (String)getInternal(rendererDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>renderer</code> attribute.
     *
     * @param value the value of the <code>renderer</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRenderer(String value)
    {
        try
        {
            if(value != null)
            {
                set(rendererDef, value);
            }
            else
            {
                unset(rendererDef);
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
	 * Checks if the value of the <code>renderer</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>renderer</code> attribute is defined.
	 */
    public boolean isRendererDefined()
	{
	    return isDefined(rendererDef);
	}
 
    /**
     * Returns the value of the <code>sortDirection</code> attribute.
     *
     * @return the value of the <code>sortDirection</code> attribute.
     */
    public String getSortDirection()
    {
        return (String)getInternal(sortDirectionDef, null);
    }
    
    /**
     * Returns the value of the <code>sortDirection</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>sortDirection</code> attribute.
     */
    public String getSortDirection(String defaultValue)
    {
        return (String)getInternal(sortDirectionDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>sortDirection</code> attribute.
     *
     * @param value the value of the <code>sortDirection</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSortDirection(String value)
    {
        try
        {
            if(value != null)
            {
                set(sortDirectionDef, value);
            }
            else
            {
                unset(sortDirectionDef);
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
	 * Checks if the value of the <code>sortDirection</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>sortDirection</code> attribute is defined.
	 */
    public boolean isSortDirectionDefined()
	{
	    return isDefined(sortDirectionDef);
	}
 
    /**
     * Returns the value of the <code>sortOrder</code> attribute.
     *
     * @return the value of the <code>sortOrder</code> attribute.
     */
    public String getSortOrder()
    {
        return (String)getInternal(sortOrderDef, null);
    }
    
    /**
     * Returns the value of the <code>sortOrder</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>sortOrder</code> attribute.
     */
    public String getSortOrder(String defaultValue)
    {
        return (String)getInternal(sortOrderDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>sortOrder</code> attribute.
     *
     * @param value the value of the <code>sortOrder</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSortOrder(String value)
    {
        try
        {
            if(value != null)
            {
                set(sortOrderDef, value);
            }
            else
            {
                unset(sortOrderDef);
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
	 * Checks if the value of the <code>sortOrder</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>sortOrder</code> attribute is defined.
	 */
    public boolean isSortOrderDefined()
	{
	    return isDefined(sortOrderDef);
	}
 
    /**
     * Returns the value of the <code>storePlace</code> attribute.
     *
     * @return the value of the <code>storePlace</code> attribute.
     */
    public DirectoryResource getStorePlace()
    {
        return (DirectoryResource)getInternal(storePlaceDef, null);
    }
    
    /**
     * Returns the value of the <code>storePlace</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>storePlace</code> attribute.
     */
    public DirectoryResource getStorePlace(DirectoryResource defaultValue)
    {
        return (DirectoryResource)getInternal(storePlaceDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>storePlace</code> attribute.
     *
     * @param value the value of the <code>storePlace</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setStorePlace(DirectoryResource value)
    {
        try
        {
            if(value != null)
            {
                set(storePlaceDef, value);
            }
            else
            {
                unset(storePlaceDef);
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
	 * Checks if the value of the <code>storePlace</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>storePlace</code> attribute is defined.
	 */
    public boolean isStorePlaceDefined()
	{
	    return isDefined(storePlaceDef);
	}
 
    /**
     * Returns the value of the <code>template</code> attribute.
     *
     * @return the value of the <code>template</code> attribute.
     */
    public String getTemplate()
    {
        return (String)getInternal(templateDef, null);
    }
    
    /**
     * Returns the value of the <code>template</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>template</code> attribute.
     */
    public String getTemplate(String defaultValue)
    {
        return (String)getInternal(templateDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>template</code> attribute.
     *
     * @param value the value of the <code>template</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTemplate(String value)
    {
        try
        {
            if(value != null)
            {
                set(templateDef, value);
            }
            else
            {
                unset(templateDef);
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
	 * Checks if the value of the <code>template</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>template</code> attribute is defined.
	 */
    public boolean isTemplateDefined()
	{
	    return isDefined(templateDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////

    // @import java.util.ArrayList
    // @import net.cyklotron.cms.site.SiteResource
    // @import org.objectledge.coral.session.CoralSession
    
    /**
     * Returns the selected publication times of the periodical.
     *
     * @return an array of PublicationTimeResource objects.     
     */
    public PublicationTimeResource[] getPublicationTimes(CoralSession coralSession)
    {
        Resource[] children = coralSession.getStore().getResource(this);
        ArrayList temp = new ArrayList();
        for (int i = 0; i < children.length; i++)
        {
            Resource resource = children[i];
            if(resource instanceof PublicationTimeResource)
            {
                temp.add(resource);
            }
        }
        PublicationTimeResource[] result = new PublicationTimeResource[temp.size()];
        temp.toArray(result);
        return result;
    }
    
    /**
     * Returns the site this periodical belongs to.
     * 
     * @return the site this periodical belongs to.
     */
    public SiteResource getSite()
    {
        return (SiteResource)getParent().getParent().getParent().getParent();
    }
}
