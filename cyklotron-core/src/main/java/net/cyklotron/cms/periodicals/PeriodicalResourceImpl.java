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
import org.jcontainer.dna.Logger;

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

    /** The AttributeDefinition object for the <code>category_query_set</code> attribute. */
    private AttributeDefinition category_query_setDef;

    /** The AttributeDefinition object for the <code>encoding</code> attribute. */
    private AttributeDefinition encodingDef;

    /** The AttributeDefinition object for the <code>last_published</code> attribute. */
    private AttributeDefinition last_publishedDef;

    /** The AttributeDefinition object for the <code>locale</code> attribute. */
    private AttributeDefinition localeDef;

    /** The AttributeDefinition object for the <code>renderer</code> attribute. */
    private AttributeDefinition rendererDef;

    /** The AttributeDefinition object for the <code>store_place</code> attribute. */
    private AttributeDefinition store_placeDef;

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
            category_query_setDef = rc.getAttribute("category_query_set");
            encodingDef = rc.getAttribute("encoding");
            last_publishedDef = rc.getAttribute("last_published");
            localeDef = rc.getAttribute("locale");
            rendererDef = rc.getAttribute("renderer");
            store_placeDef = rc.getAttribute("store_place");
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
     * Returns the value of the <code>category_query_set</code> attribute.
     *
     * @return the value of the <code>category_query_set</code> attribute.
     */
    public CategoryQueryPoolResource getCategory_query_set()
    {
        return (CategoryQueryPoolResource)get(category_query_setDef);
    }
    
    /**
     * Returns the value of the <code>category_query_set</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>category_query_set</code> attribute.
     */
    public CategoryQueryPoolResource getCategory_query_set(CategoryQueryPoolResource defaultValue)
    {
        if(isDefined(category_query_setDef))
        {
            return (CategoryQueryPoolResource)get(category_query_setDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>category_query_set</code> attribute.
     *
     * @param value the value of the <code>category_query_set</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCategory_query_set(CategoryQueryPoolResource value)
    {
        try
        {
            if(value != null)
            {
                set(category_query_setDef, value);
            }
            else
            {
                unset(category_query_setDef);
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
	 * Checks if the value of the <code>category_query_set</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>category_query_set</code> attribute is defined.
	 */
    public boolean isCategory_query_setDefined()
	{
	    return isDefined(category_query_setDef);
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
     * Returns the value of the <code>last_published</code> attribute.
     *
     * @return the value of the <code>last_published</code> attribute.
     */
    public Date getLast_published()
    {
        return (Date)get(last_publishedDef);
    }
    
    /**
     * Returns the value of the <code>last_published</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>last_published</code> attribute.
     */
    public Date getLast_published(Date defaultValue)
    {
        if(isDefined(last_publishedDef))
        {
            return (Date)get(last_publishedDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>last_published</code> attribute.
     *
     * @param value the value of the <code>last_published</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLast_published(Date value)
    {
        try
        {
            if(value != null)
            {
                set(last_publishedDef, value);
            }
            else
            {
                unset(last_publishedDef);
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
	 * Checks if the value of the <code>last_published</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>last_published</code> attribute is defined.
	 */
    public boolean isLast_publishedDefined()
	{
	    return isDefined(last_publishedDef);
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
     * Returns the value of the <code>store_place</code> attribute.
     *
     * @return the value of the <code>store_place</code> attribute.
     */
    public DirectoryResource getStore_place()
    {
        return (DirectoryResource)get(store_placeDef);
    }
    
    /**
     * Returns the value of the <code>store_place</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>store_place</code> attribute.
     */
    public DirectoryResource getStore_place(DirectoryResource defaultValue)
    {
        if(isDefined(store_placeDef))
        {
            return (DirectoryResource)get(store_placeDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>store_place</code> attribute.
     *
     * @param value the value of the <code>store_place</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setStore_place(DirectoryResource value)
    {
        try
        {
            if(value != null)
            {
                set(store_placeDef, value);
            }
            else
            {
                unset(store_placeDef);
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
	 * Checks if the value of the <code>store_place</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>store_place</code> attribute is defined.
	 */
    public boolean isStore_placeDefined()
	{
	    return isDefined(store_placeDef);
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
    
    /**
     * Returns the selected publication times of the periodical.
     *
     * @return an array of PublicationTimeResource objects.     
     */
    public PublicationTimeResource[] getPublicationTimes()
    {
        Resource[] children = rs.getStore().getResource(this);
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
