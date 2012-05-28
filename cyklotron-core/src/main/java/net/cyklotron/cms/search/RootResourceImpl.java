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
 
package net.cyklotron.cms.search;

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
import org.objectledge.parameters.Parameters;

import net.cyklotron.cms.CmsNodeResourceImpl;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * An implementation of <code>search.root</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class RootResourceImpl
    extends CmsNodeResourceImpl
    implements RootResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>preferences</code> attribute. */
	private static AttributeDefinition<Parameters> preferencesDef;

    /** The AttributeDefinition object for the <code>searchNode</code> attribute. */
	private static AttributeDefinition<NavigationNodeResource> searchNodeDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>search.root</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public RootResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>search.root</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static RootResource getRootResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof RootResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not search.root");
        }
        return (RootResource)res;
    }

    /**
     * Creates a new <code>search.root</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param preferences the preferences attribute
     * @return a new RootResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static RootResource createRootResource(CoralSession session, String name, Resource
        parent, Parameters preferences)
        throws ValueRequiredException, InvalidResourceNameException
    {
        try
        {
            ResourceClass<RootResource> rc = session.getSchema().getResourceClass("search.root", RootResource.class);
			Map<AttributeDefinition<?>, Object> attrs = new HashMap<AttributeDefinition<?>, Object>();
            attrs.put(rc.getAttribute("preferences"), preferences);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof RootResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (RootResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>preferences</code> attribute.
     *
     * @return the value of the <code>preferences</code> attribute.
     */
    public Parameters getPreferences()
    {
        return get(preferencesDef);
    }
   
    /**
     * Returns the value of the <code>searchNode</code> attribute.
     *
     * @return the value of the <code>searchNode</code> attribute.
     */
    public NavigationNodeResource getSearchNode()
    {
        return get(searchNodeDef);
    }
    
    /**
     * Returns the value of the <code>searchNode</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>searchNode</code> attribute.
     */
    public NavigationNodeResource getSearchNode(NavigationNodeResource defaultValue)
    {
        return get(searchNodeDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>searchNode</code> attribute.
     *
     * @param value the value of the <code>searchNode</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSearchNode(NavigationNodeResource value)
    {
        try
        {
            if(value != null)
            {
                set(searchNodeDef, value);
            }
            else
            {
                unset(searchNodeDef);
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
	 * Checks if the value of the <code>searchNode</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>searchNode</code> attribute is defined.
	 */
    public boolean isSearchNodeDefined()
	{
	    return isDefined(searchNodeDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
}
