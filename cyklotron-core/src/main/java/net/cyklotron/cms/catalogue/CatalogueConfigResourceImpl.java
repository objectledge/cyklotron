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
 
package net.cyklotron.cms.catalogue;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

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
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.search.PoolResource;

/**
 * An implementation of <code>cms.catalogue.config</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class CatalogueConfigResourceImpl
    extends CmsNodeResourceImpl
    implements CatalogueConfigResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>category</code> attribute. */
	private static AttributeDefinition<CategoryResource> categoryDef;

    /** The AttributeDefinition object for the <code>requiredPropertyNames</code> attribute. */
	private static AttributeDefinition<String> requiredPropertyNamesDef;

    /** The AttributeDefinition object for the <code>searchPool</code> attribute. */
	private static AttributeDefinition<PoolResource> searchPoolDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.catalogue.config</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public CatalogueConfigResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.catalogue.config</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static CatalogueConfigResource getCatalogueConfigResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof CatalogueConfigResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.catalogue.config");
        }
        return (CatalogueConfigResource)res;
    }

    /**
     * Creates a new <code>cms.catalogue.config</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new CatalogueConfigResource instance.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static CatalogueConfigResource createCatalogueConfigResource(CoralSession session,
        String name, Resource parent)
        throws InvalidResourceNameException
    {
        try
        {
            ResourceClass<CatalogueConfigResource> rc = session.getSchema().getResourceClass("cms.catalogue.config", CatalogueConfigResource.class);
		    Resource res = session.getStore().createResource(name, parent, rc,
                java.util.Collections.<AttributeDefinition<?>, Object> emptyMap());			
            if(!(res instanceof CatalogueConfigResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (CatalogueConfigResource)res;
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
     * Returns the value of the <code>category</code> attribute.
     *
     * @return the value of the <code>category</code> attribute.
     */
    public CategoryResource getCategory()
    {
        return get(categoryDef);
    }
    
    /**
     * Returns the value of the <code>category</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>category</code> attribute.
     */
    public CategoryResource getCategory(CategoryResource defaultValue)
    {
        return get(categoryDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>category</code> attribute.
     *
     * @param value the value of the <code>category</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCategory(CategoryResource value)
    {
        try
        {
            if(value != null)
            {
                set(categoryDef, value);
            }
            else
            {
                unset(categoryDef);
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
	 * Checks if the value of the <code>category</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>category</code> attribute is defined.
	 */
    public boolean isCategoryDefined()
	{
	    return isDefined(categoryDef);
	}
 
    /**
     * Returns the value of the <code>requiredPropertyNames</code> attribute.
     *
     * @return the value of the <code>requiredPropertyNames</code> attribute.
     */
    public String getRequiredPropertyNames()
    {
        return get(requiredPropertyNamesDef);
    }
    
    /**
     * Returns the value of the <code>requiredPropertyNames</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>requiredPropertyNames</code> attribute.
     */
    public String getRequiredPropertyNames(String defaultValue)
    {
        return get(requiredPropertyNamesDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>requiredPropertyNames</code> attribute.
     *
     * @param value the value of the <code>requiredPropertyNames</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRequiredPropertyNames(String value)
    {
        try
        {
            if(value != null)
            {
                set(requiredPropertyNamesDef, value);
            }
            else
            {
                unset(requiredPropertyNamesDef);
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
	 * Checks if the value of the <code>requiredPropertyNames</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>requiredPropertyNames</code> attribute is defined.
	 */
    public boolean isRequiredPropertyNamesDefined()
	{
	    return isDefined(requiredPropertyNamesDef);
	}
 
    /**
     * Returns the value of the <code>searchPool</code> attribute.
     *
     * @return the value of the <code>searchPool</code> attribute.
     */
    public PoolResource getSearchPool()
    {
        return get(searchPoolDef);
    }
    
    /**
     * Returns the value of the <code>searchPool</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>searchPool</code> attribute.
     */
    public PoolResource getSearchPool(PoolResource defaultValue)
    {
        return get(searchPoolDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>searchPool</code> attribute.
     *
     * @param value the value of the <code>searchPool</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSearchPool(PoolResource value)
    {
        try
        {
            if(value != null)
            {
                set(searchPoolDef, value);
            }
            else
            {
                unset(searchPoolDef);
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
	 * Checks if the value of the <code>searchPool</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>searchPool</code> attribute is defined.
	 */
    public boolean isSearchPoolDefined()
	{
	    return isDefined(searchPoolDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
    // @import java.util.Set
    // @import java.util.EnumSet
    // @import java.util.Iterator
    
    public Set<IndexCard.Property> getRequiredProperties()
    {
        Set<IndexCard.Property> properties = EnumSet.noneOf(IndexCard.Property.class);
        if(isRequiredPropertyNamesDefined())
        {
            String[] propertyNames = getRequiredPropertyNames().split(",");
            for(String propertyName : propertyNames)
            {
                properties.add(Enum.valueOf(IndexCard.Property.class, propertyName));
            }
        }
        return properties;
    }
    
    public void setRequiredProperties(Set<IndexCard.Property> properties)
    {
        Iterator<IndexCard.Property> i = properties.iterator();
        StringBuilder s = new StringBuilder();
        while(i.hasNext())
        {
            IndexCard.Property p = i.next();
            s.append(p.name());
            if(i.hasNext())
            {
                s.append(',');
            }
        }
        setRequiredPropertyNames(s.toString());
    }
}
