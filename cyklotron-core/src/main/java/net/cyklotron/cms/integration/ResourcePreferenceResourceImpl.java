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
 
package net.cyklotron.cms.integration;

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

import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>integration.resource_preference</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class ResourcePreferenceResourceImpl
    extends PreferenceResourceImpl
    implements ResourcePreferenceResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>multi</code> attribute. */
    private AttributeDefinition multiDef;

    /** The AttributeDefinition object for the <code>relatedClass</code> attribute. */
    private AttributeDefinition relatedClassDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>integration.resource_preference</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public ResourcePreferenceResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("integration.resource_preference");
            multiDef = rc.getAttribute("multi");
            relatedClassDef = rc.getAttribute("relatedClass");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>integration.resource_preference</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static ResourcePreferenceResource getResourcePreferenceResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof ResourcePreferenceResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not integration.resource_preference");
        }
        return (ResourcePreferenceResource)res;
    }

    /**
     * Creates a new <code>integration.resource_preference</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param multi the multi attribute
     * @param relatedClass the relatedClass attribute
     * @param required the required attribute
     * @param scope the scope attribute
     * @return a new ResourcePreferenceResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     */
    public static ResourcePreferenceResource createResourcePreferenceResource(CoralSession
        session, String name, Resource parent, boolean multi, ResourceClass relatedClass, boolean
        required, String scope)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("integration.resource_preference");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("multi"), new Boolean(multi));
            attrs.put(rc.getAttribute("relatedClass"), relatedClass);
            attrs.put(rc.getAttribute("required"), new Boolean(required));
            attrs.put(rc.getAttribute("scope"), scope);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof ResourcePreferenceResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (ResourcePreferenceResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>multi</code> attribute.
     *
     * @return the value of the <code>multi</code> attribute.
     */
    public boolean getMulti()
    {
        if(isDefined(multiDef))
        {
            return ((Boolean)get(multiDef)).booleanValue();
        }
        else
        {
            throw new BackendException("incompatible schema change");
        }
    }    

    /**
     * Sets the value of the <code>multi</code> attribute.
     *
     * @param value the value of the <code>multi</code> attribute.
     */
    public void setMulti(boolean value)
    {
        try
        {
            set(multiDef, new Boolean(value));
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
     * Returns the value of the <code>relatedClass</code> attribute.
     *
     * @return the value of the <code>relatedClass</code> attribute.
     */
    public ResourceClass getRelatedClass()
    {
        return (ResourceClass)get(relatedClassDef);
    }
 
    /**
     * Sets the value of the <code>relatedClass</code> attribute.
     *
     * @param value the value of the <code>relatedClass</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setRelatedClass(ResourceClass value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(relatedClassDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute relatedClass "+
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
