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

import org.jcontainer.dna.Logger;
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
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>categoryQuerySet</code> attribute. */
    private AttributeDefinition categoryQuerySetDef;

    /** The AttributeDefinition object for the <code>encoding</code> attribute. */
    private AttributeDefinition encodingDef;

    /** The AttributeDefinition object for the <code>lastPublished</code> attribute. */
    private AttributeDefinition lastPublishedDef;

    /** The AttributeDefinition object for the <code>locale</code> attribute. */
    private AttributeDefinition localeDef;

    /** The AttributeDefinition object for the <code>renderer</code> attribute. */
    private AttributeDefinition rendererDef;

    /** The AttributeDefinition object for the <code>storePlace</code> attribute. */
    private AttributeDefinition storePlaceDef;

    /** The AttributeDefinition object for the <code>template</code> attribute. */
    private AttributeDefinition templateDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.periodicals.periodical</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public PeriodicalResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("cms.periodicals.periodical");
            categoryQuerySetDef = rc.getAttribute("categoryQuerySet");
            encodingDef = rc.getAttribute("encoding");
            lastPublishedDef = rc.getAttribute("lastPublished");
            localeDef = rc.getAttribute("locale");
            rendererDef = rc.getAttribute("renderer");
            storePlaceDef = rc.getAttribute("storePlace");
            templateDef = rc.getAttribute("template");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
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
     */
    public static PeriodicalResource createPeriodicalResource(CoralSession session, String name,
        Resource parent)
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
        return (CategoryQueryPoolResource)get(categoryQuerySetDef);
    }
    
    /**
     * Returns the value of the <code>categoryQuerySet</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>categoryQuerySet</code> attribute.
     */
    public CategoryQueryPoolResource getCategoryQuerySet(CategoryQueryPoolResource defaultValue)
    {
        if(isDefined(categoryQuerySetDef))
        {
            return (CategoryQueryPoolResource)get(categoryQuerySetDef);
        }
        else
        {
            return defaultValue;
        }
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
        return (String)get(encodingDef);
    }
    
    /**
     * Returns the value of the <code>encoding</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>encoding</code> attribute.
     */
    public String getEncoding(String defaultValue)
    {
        if(isDefined(encodingDef))
        {
            return (String)get(encodingDef);
        }
        else
        {
            return defaultValue;
        }
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
        return (Date)get(lastPublishedDef);
    }
    
    /**
     * Returns the value of the <code>lastPublished</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lastPublished</code> attribute.
     */
    public Date getLastPublished(Date defaultValue)
    {
        if(isDefined(lastPublishedDef))
        {
            return (Date)get(lastPublishedDef);
        }
        else
        {
            return defaultValue;
        }
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
        return (String)get(localeDef);
    }
    
    /**
     * Returns the value of the <code>locale</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>locale</code> attribute.
     */
    public String getLocale(String defaultValue)
    {
        if(isDefined(localeDef))
        {
            return (String)get(localeDef);
        }
        else
        {
            return defaultValue;
        }
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
     * Returns the value of the <code>renderer</code> attribute.
     *
     * @return the value of the <code>renderer</code> attribute.
     */
    public String getRenderer()
    {
        return (String)get(rendererDef);
    }
    
    /**
     * Returns the value of the <code>renderer</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>renderer</code> attribute.
     */
    public String getRenderer(String defaultValue)
    {
        if(isDefined(rendererDef))
        {
            return (String)get(rendererDef);
        }
        else
        {
            return defaultValue;
        }
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
     * Returns the value of the <code>storePlace</code> attribute.
     *
     * @return the value of the <code>storePlace</code> attribute.
     */
    public DirectoryResource getStorePlace()
    {
        return (DirectoryResource)get(storePlaceDef);
    }
    
    /**
     * Returns the value of the <code>storePlace</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>storePlace</code> attribute.
     */
    public DirectoryResource getStorePlace(DirectoryResource defaultValue)
    {
        if(isDefined(storePlaceDef))
        {
            return (DirectoryResource)get(storePlaceDef);
        }
        else
        {
            return defaultValue;
        }
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
        return (String)get(templateDef);
    }
    
    /**
     * Returns the value of the <code>template</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>template</code> attribute.
     */
    public String getTemplate(String defaultValue)
    {
        if(isDefined(templateDef))
        {
            return (String)get(templateDef);
        }
        else
        {
            return defaultValue;
        }
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
