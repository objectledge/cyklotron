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
 
package net.cyklotron.cms.forum;

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
 * An implementation of <code>cms.forum.commentary</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class CommentaryResourceImpl
    extends DiscussionResourceImpl
    implements CommentaryResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>document_title</code> attribute. */
    private AttributeDefinition document_titleDef;

    /** The AttributeDefinition object for the <code>resource_id</code> attribute. */
    private AttributeDefinition resource_idDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.forum.commentary</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public CommentaryResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("cms.forum.commentary");
            document_titleDef = rc.getAttribute("document_title");
            resource_idDef = rc.getAttribute("resource_id");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.forum.commentary</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static CommentaryResource getCommentaryResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof CommentaryResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.forum.commentary");
        }
        return (CommentaryResource)res;
    }

    /**
     * Creates a new <code>cms.forum.commentary</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param forum the forum attribute
     * @return a new CommentaryResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     */
    public static CommentaryResource createCommentaryResource(CoralSession session, String name,
        Resource parent, ForumResource forum)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.forum.commentary");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("forum"), forum);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof CommentaryResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (CommentaryResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>document_title</code> attribute.
     *
     * @return the value of the <code>document_title</code> attribute.
     */
    public String getDocument_title()
    {
        return (String)get(document_titleDef);
    }
    
    /**
     * Returns the value of the <code>document_title</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>document_title</code> attribute.
     */
    public String getDocument_title(String defaultValue)
    {
        if(isDefined(document_titleDef))
        {
            return (String)get(document_titleDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>document_title</code> attribute.
     *
     * @param value the value of the <code>document_title</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setDocument_title(String value)
    {
        try
        {
            if(value != null)
            {
                set(document_titleDef, value);
            }
            else
            {
                unset(document_titleDef);
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
	 * Checks if the value of the <code>document_title</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>document_title</code> attribute is defined.
	 */
    public boolean isDocument_titleDefined()
	{
	    return isDefined(document_titleDef);
	}

    /**
     * Returns the value of the <code>resource_id</code> attribute.
     *
     * @return the value of the <code>resource_id</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public long getResource_id()
        throws IllegalStateException
    {
        if(isDefined(resource_idDef))
        {
            return ((Long)get(resource_idDef)).longValue();
        }
        else
        {
            throw new IllegalStateException("attribute value is undefined");
        }
    }

    /**
     * Returns the value of the <code>resource_id</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>resource_id</code> attribute.
     */
    public long getResource_id(long defaultValue)
    {
        if(isDefined(resource_idDef))
        {
            return ((Long)get(resource_idDef)).longValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>resource_id</code> attribute.
     *
     * @param value the value of the <code>resource_id</code> attribute.
     */
    public void setResource_id(long value)
    {
        try
        {
            set(resource_idDef, new Long(value));
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
     * Removes the value of the <code>resource_id</code> attribute.
     */
    public void unsetResource_id()
    {
        try
        {
            unset(resource_idDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>resource_id</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>resource_id</code> attribute is defined.
	 */
    public boolean isResource_idDefined()
	{
	    return isDefined(resource_idDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////

    /**
     * Returns the commented resource, or null if deleted.
     */
    public Resource getResource()
    {
        if(!isDefined(resourceIdDef))
        {
            return null;
        }
        try
        {
            return rs.getStore().getResource(getResourceId());
        }
        catch(EntityDoesNotExistException e)
        {
            return null;
        }
        catch(BackendException e)
        {
            if(e.getRootCause() instanceof EntityDoesNotExistException)
            {
                return null;
            }
            else
            {
                throw e;
            }
        }
    }
}
