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
 
package net.cyklotron.cms.accesslimits;

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
 * An implementation of <code>cms.accesslimits.list</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class AccessListResourceImpl
    extends CmsNodeResourceImpl
    implements AccessListResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
	@SuppressWarnings("unused")
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>contents</code> attribute. */
	private static AttributeDefinition<String> contentsDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.accesslimits.list</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public AccessListResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.accesslimits.list</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static AccessListResource getAccessListResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof AccessListResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.accesslimits.list");
        }
        return (AccessListResource)res;
    }

    /**
     * Creates a new <code>cms.accesslimits.list</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param contents the contents attribute
     * @return a new AccessListResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static AccessListResource createAccessListResource(CoralSession session, String name,
        Resource parent, String contents)
        throws ValueRequiredException, InvalidResourceNameException
    {
        try
        {
            ResourceClass<AccessListResource> rc = session.getSchema().getResourceClass("cms.accesslimits.list", AccessListResource.class);
			Map<AttributeDefinition<?>, Object> attrs = new HashMap<AttributeDefinition<?>, Object>();
            attrs.put(rc.getAttribute("contents"), contents);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof AccessListResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (AccessListResource)res;
        }
        catch(EntityDoesNotExistException e)
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
     * Sets the value of the <code>contents</code> attribute.
     *
     * @param value the value of the <code>contents</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setContents(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(contentsDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute contents "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
     
    // @custom methods ///////////////////////////////////////////////////////
}
