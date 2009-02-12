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
 
package net.cyklotron.cms.category;

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
 * An implementation of <code>category.category</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class CategoryResourceImpl
    extends CmsNodeResourceImpl
    implements CategoryResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>hidden</code> attribute. */
    private static AttributeDefinition hiddenDef;

    /** The AttributeDefinition object for the <code>ui_color</code> attribute. */
    private static AttributeDefinition ui_colorDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>category.category</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public CategoryResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>category.category</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static CategoryResource getCategoryResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof CategoryResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not category.category");
        }
        return (CategoryResource)res;
    }

    /**
     * Creates a new <code>category.category</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new CategoryResource instance.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static CategoryResource createCategoryResource(CoralSession session, String name,
        Resource parent)
        throws InvalidResourceNameException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("category.category");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof CategoryResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (CategoryResource)res;
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
     * Returns the value of the <code>hidden</code> attribute.
     *
     * @return the value of the <code>hidden</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getHidden()
        throws IllegalStateException
    {
	    Boolean value = (Boolean)getInternal(hiddenDef, null);
        if(value != null)
        {
            return value.booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute hidden is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>hidden</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>hidden</code> attribute.
     */
    public boolean getHidden(boolean defaultValue)
    {
		return ((Boolean)getInternal(hiddenDef, new Boolean(defaultValue))).booleanValue();
	}

    /**
     * Sets the value of the <code>hidden</code> attribute.
     *
     * @param value the value of the <code>hidden</code> attribute.
     */
    public void setHidden(boolean value)
    {
        try
        {
            set(hiddenDef, new Boolean(value));
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
     * Removes the value of the <code>hidden</code> attribute.
     */
    public void unsetHidden()
    {
        try
        {
            unset(hiddenDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>hidden</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>hidden</code> attribute is defined.
	 */
    public boolean isHiddenDefined()
	{
	    return isDefined(hiddenDef);
	}
 
    /**
     * Returns the value of the <code>ui_color</code> attribute.
     *
     * @return the value of the <code>ui_color</code> attribute.
     */
    public String getUi_color()
    {
        return (String)getInternal(ui_colorDef, null);
    }
    
    /**
     * Returns the value of the <code>ui_color</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>ui_color</code> attribute.
     */
    public String getUi_color(String defaultValue)
    {
        return (String)getInternal(ui_colorDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>ui_color</code> attribute.
     *
     * @param value the value of the <code>ui_color</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setUi_color(String value)
    {
        try
        {
            if(value != null)
            {
                set(ui_colorDef, value);
            }
            else
            {
                unset(ui_colorDef);
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
	 * Checks if the value of the <code>ui_color</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>ui_color</code> attribute is defined.
	 */
    public boolean isUi_colorDefined()
	{
	    return isDefined(ui_colorDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
}
