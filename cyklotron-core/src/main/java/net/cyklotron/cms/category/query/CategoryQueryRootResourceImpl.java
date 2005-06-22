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
 
package net.cyklotron.cms.category.query;

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
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * An implementation of <code>category.query.root</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class CategoryQueryRootResourceImpl
    extends CmsNodeResourceImpl
    implements CategoryQueryRootResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>defaultQuery</code> attribute. */
    private static AttributeDefinition defaultQueryDef;

    /** The AttributeDefinition object for the <code>resultsNode</code> attribute. */
    private static AttributeDefinition resultsNodeDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>category.query.root</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public CategoryQueryRootResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>category.query.root</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static CategoryQueryRootResource getCategoryQueryRootResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof CategoryQueryRootResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not category.query.root");
        }
        return (CategoryQueryRootResource)res;
    }

    /**
     * Creates a new <code>category.query.root</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new CategoryQueryRootResource instance.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static CategoryQueryRootResource createCategoryQueryRootResource(CoralSession
        session, String name, Resource parent)
        throws InvalidResourceNameException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("category.query.root");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof CategoryQueryRootResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (CategoryQueryRootResource)res;
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
     * Returns the value of the <code>defaultQuery</code> attribute.
     *
     * @return the value of the <code>defaultQuery</code> attribute.
     */
    public CategoryQueryResource getDefaultQuery()
    {
        return (CategoryQueryResource)get(defaultQueryDef);
    }
    
    /**
     * Returns the value of the <code>defaultQuery</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>defaultQuery</code> attribute.
     */
    public CategoryQueryResource getDefaultQuery(CategoryQueryResource defaultValue)
    {
        if(isDefined(defaultQueryDef))
        {
            return (CategoryQueryResource)get(defaultQueryDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>defaultQuery</code> attribute.
     *
     * @param value the value of the <code>defaultQuery</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setDefaultQuery(CategoryQueryResource value)
    {
        try
        {
            if(value != null)
            {
                set(defaultQueryDef, value);
            }
            else
            {
                unset(defaultQueryDef);
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
	 * Checks if the value of the <code>defaultQuery</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>defaultQuery</code> attribute is defined.
	 */
    public boolean isDefaultQueryDefined()
	{
	    return isDefined(defaultQueryDef);
	}
 
    /**
     * Returns the value of the <code>resultsNode</code> attribute.
     *
     * @return the value of the <code>resultsNode</code> attribute.
     */
    public NavigationNodeResource getResultsNode()
    {
        return (NavigationNodeResource)get(resultsNodeDef);
    }
    
    /**
     * Returns the value of the <code>resultsNode</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>resultsNode</code> attribute.
     */
    public NavigationNodeResource getResultsNode(NavigationNodeResource defaultValue)
    {
        if(isDefined(resultsNodeDef))
        {
            return (NavigationNodeResource)get(resultsNodeDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>resultsNode</code> attribute.
     *
     * @param value the value of the <code>resultsNode</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setResultsNode(NavigationNodeResource value)
    {
        try
        {
            if(value != null)
            {
                set(resultsNodeDef, value);
            }
            else
            {
                unset(resultsNodeDef);
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
	 * Checks if the value of the <code>resultsNode</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>resultsNode</code> attribute is defined.
	 */
    public boolean isResultsNodeDefined()
	{
	    return isDefined(resultsNodeDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
}
