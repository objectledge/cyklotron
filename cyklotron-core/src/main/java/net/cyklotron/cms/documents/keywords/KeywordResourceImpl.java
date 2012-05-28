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
 
package net.cyklotron.cms.documents.keywords;

import java.util.HashMap;
import java.util.Map;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.CmsNodeResourceImpl;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * An implementation of <code>documents.keyword</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class KeywordResourceImpl
    extends CmsNodeResourceImpl
    implements KeywordResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>categories</code> attribute. */
	private static AttributeDefinition<ResourceList> categoriesDef;

    /** The AttributeDefinition object for the <code>external</code> attribute. */
    private static AttributeDefinition<Boolean> externalDef;

    /** The AttributeDefinition object for the <code>hrefExternal</code> attribute. */
	private static AttributeDefinition<String> hrefExternalDef;

    /** The AttributeDefinition object for the <code>hrefInternal</code> attribute. */
	private static AttributeDefinition<NavigationNodeResource> hrefInternalDef;

    /** The AttributeDefinition object for the <code>linkClass</code> attribute. */
	private static AttributeDefinition<String> linkClassDef;

    /** The AttributeDefinition object for the <code>newWindow</code> attribute. */
    private static AttributeDefinition<Boolean> newWindowDef;

    /** The AttributeDefinition object for the <code>pattern</code> attribute. */
	private static AttributeDefinition<String> patternDef;

    /** The AttributeDefinition object for the <code>regexp</code> attribute. */
    private static AttributeDefinition<Boolean> regexpDef;

    /** The AttributeDefinition object for the <code>title</code> attribute. */
	private static AttributeDefinition<String> titleDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>documents.keyword</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public KeywordResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>documents.keyword</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static KeywordResource getKeywordResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof KeywordResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not documents.keyword");
        }
        return (KeywordResource)res;
    }

    /**
     * Creates a new <code>documents.keyword</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param external the external attribute
     * @param newWindow the newWindow attribute
     * @param pattern the pattern attribute
     * @param regexp the regexp attribute
     * @return a new KeywordResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static KeywordResource createKeywordResource(CoralSession session, String name,
        Resource parent, boolean external, boolean newWindow, String pattern, boolean regexp)
        throws ValueRequiredException, InvalidResourceNameException
    {
        try
        {
            ResourceClass<KeywordResource> rc = session.getSchema().getResourceClass("documents.keyword", KeywordResource.class);
			Map<AttributeDefinition<?>, Object> attrs = new HashMap<AttributeDefinition<?>, Object>();
            attrs.put(rc.getAttribute("external"), Boolean.valueOf(external));
            attrs.put(rc.getAttribute("newWindow"), Boolean.valueOf(newWindow));
            attrs.put(rc.getAttribute("pattern"), pattern);
            attrs.put(rc.getAttribute("regexp"), Boolean.valueOf(regexp));
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof KeywordResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (KeywordResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>categories</code> attribute.
     *
     * @return the value of the <code>categories</code> attribute.
     */
    public ResourceList getCategories()
    {
        return get(categoriesDef);
    }
    
    /**
     * Returns the value of the <code>categories</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>categories</code> attribute.
     */
    public ResourceList getCategories(ResourceList defaultValue)
    {
        return get(categoriesDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>categories</code> attribute.
     *
     * @param value the value of the <code>categories</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCategories(ResourceList value)
    {
        try
        {
            if(value != null)
            {
                set(categoriesDef, value);
            }
            else
            {
                unset(categoriesDef);
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
	 * Checks if the value of the <code>categories</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>categories</code> attribute is defined.
	 */
    public boolean isCategoriesDefined()
	{
	    return isDefined(categoriesDef);
	}
 
    /**
     * Returns the value of the <code>external</code> attribute.
     *
     * @return the value of the <code>external</code> attribute.
     */
    public boolean getExternal()
    {
		return get(externalDef).booleanValue();
    }    

    /**
     * Sets the value of the <code>external</code> attribute.
     *
     * @param value the value of the <code>external</code> attribute.
     */
    public void setExternal(boolean value)
    {
        try
        {
            set(externalDef, Boolean.valueOf(value));
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
     * Returns the value of the <code>hrefExternal</code> attribute.
     *
     * @return the value of the <code>hrefExternal</code> attribute.
     */
    public String getHrefExternal()
    {
        return get(hrefExternalDef);
    }
    
    /**
     * Returns the value of the <code>hrefExternal</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>hrefExternal</code> attribute.
     */
    public String getHrefExternal(String defaultValue)
    {
        return get(hrefExternalDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>hrefExternal</code> attribute.
     *
     * @param value the value of the <code>hrefExternal</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setHrefExternal(String value)
    {
        try
        {
            if(value != null)
            {
                set(hrefExternalDef, value);
            }
            else
            {
                unset(hrefExternalDef);
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
	 * Checks if the value of the <code>hrefExternal</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>hrefExternal</code> attribute is defined.
	 */
    public boolean isHrefExternalDefined()
	{
	    return isDefined(hrefExternalDef);
	}
 
    /**
     * Returns the value of the <code>hrefInternal</code> attribute.
     *
     * @return the value of the <code>hrefInternal</code> attribute.
     */
    public NavigationNodeResource getHrefInternal()
    {
        return get(hrefInternalDef);
    }
    
    /**
     * Returns the value of the <code>hrefInternal</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>hrefInternal</code> attribute.
     */
    public NavigationNodeResource getHrefInternal(NavigationNodeResource defaultValue)
    {
        return get(hrefInternalDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>hrefInternal</code> attribute.
     *
     * @param value the value of the <code>hrefInternal</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setHrefInternal(NavigationNodeResource value)
    {
        try
        {
            if(value != null)
            {
                set(hrefInternalDef, value);
            }
            else
            {
                unset(hrefInternalDef);
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
	 * Checks if the value of the <code>hrefInternal</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>hrefInternal</code> attribute is defined.
	 */
    public boolean isHrefInternalDefined()
	{
	    return isDefined(hrefInternalDef);
	}
 
    /**
     * Returns the value of the <code>linkClass</code> attribute.
     *
     * @return the value of the <code>linkClass</code> attribute.
     */
    public String getLinkClass()
    {
        return get(linkClassDef);
    }
    
    /**
     * Returns the value of the <code>linkClass</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>linkClass</code> attribute.
     */
    public String getLinkClass(String defaultValue)
    {
        return get(linkClassDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>linkClass</code> attribute.
     *
     * @param value the value of the <code>linkClass</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLinkClass(String value)
    {
        try
        {
            if(value != null)
            {
                set(linkClassDef, value);
            }
            else
            {
                unset(linkClassDef);
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
	 * Checks if the value of the <code>linkClass</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>linkClass</code> attribute is defined.
	 */
    public boolean isLinkClassDefined()
	{
	    return isDefined(linkClassDef);
	}
 
    /**
     * Returns the value of the <code>newWindow</code> attribute.
     *
     * @return the value of the <code>newWindow</code> attribute.
     */
    public boolean getNewWindow()
    {
		return get(newWindowDef).booleanValue();
    }    

    /**
     * Sets the value of the <code>newWindow</code> attribute.
     *
     * @param value the value of the <code>newWindow</code> attribute.
     */
    public void setNewWindow(boolean value)
    {
        try
        {
            set(newWindowDef, Boolean.valueOf(value));
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
     * Returns the value of the <code>pattern</code> attribute.
     *
     * @return the value of the <code>pattern</code> attribute.
     */
    public String getPattern()
    {
        return get(patternDef);
    }
 
    /**
     * Sets the value of the <code>pattern</code> attribute.
     *
     * @param value the value of the <code>pattern</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setPattern(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(patternDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute pattern "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>regexp</code> attribute.
     *
     * @return the value of the <code>regexp</code> attribute.
     */
    public boolean getRegexp()
    {
		return get(regexpDef).booleanValue();
    }    

    /**
     * Sets the value of the <code>regexp</code> attribute.
     *
     * @param value the value of the <code>regexp</code> attribute.
     */
    public void setRegexp(boolean value)
    {
        try
        {
            set(regexpDef, Boolean.valueOf(value));
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
     * Returns the value of the <code>title</code> attribute.
     *
     * @return the value of the <code>title</code> attribute.
     */
    public String getTitle()
    {
        return get(titleDef);
    }
    
    /**
     * Returns the value of the <code>title</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>title</code> attribute.
     */
    public String getTitle(String defaultValue)
    {
        return get(titleDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>title</code> attribute.
     *
     * @param value the value of the <code>title</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTitle(String value)
    {
        try
        {
            if(value != null)
            {
                set(titleDef, value);
            }
            else
            {
                unset(titleDef);
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
	 * Checks if the value of the <code>title</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>title</code> attribute is defined.
	 */
    public boolean isTitleDefined()
	{
	    return isDefined(titleDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
}
