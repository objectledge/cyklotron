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

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.workflow.StateResource;
import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>cms.forum.forum</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class ForumResourceImpl
    extends ForumNodeResourceImpl
    implements ForumResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>forum_node</code> attribute. */
    private AttributeDefinition forum_nodeDef;

    /** The AttributeDefinition object for the <code>initial_commentary_state</code> attribute. */
    private AttributeDefinition initial_commentary_stateDef;

    /** The AttributeDefinition object for the <code>reply_to</code> attribute. */
    private AttributeDefinition reply_toDef;

    /** The AttributeDefinition object for the <code>site</code> attribute. */
    private AttributeDefinition siteDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.forum.forum</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public ForumResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("cms.forum.forum");
            forum_nodeDef = rc.getAttribute("forum_node");
            initial_commentary_stateDef = rc.getAttribute("initial_commentary_state");
            reply_toDef = rc.getAttribute("reply_to");
            siteDef = rc.getAttribute("site");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.forum.forum</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static ForumResource getForumResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof ForumResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.forum.forum");
        }
        return (ForumResource)res;
    }

    /**
     * Creates a new <code>cms.forum.forum</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param site the site attribute
     * @return a new ForumResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     */
    public static ForumResource createForumResource(CoralSession session, String name, Resource
        parent, SiteResource site)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.forum.forum");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("site"), site);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof ForumResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (ForumResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>forum_node</code> attribute.
     *
     * @return the value of the <code>forum_node</code> attribute.
     */
    public NavigationNodeResource getForum_node()
    {
        return (NavigationNodeResource)get(forum_nodeDef);
    }
    
    /**
     * Returns the value of the <code>forum_node</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>forum_node</code> attribute.
     */
    public NavigationNodeResource getForum_node(NavigationNodeResource defaultValue)
    {
        if(isDefined(forum_nodeDef))
        {
            return (NavigationNodeResource)get(forum_nodeDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>forum_node</code> attribute.
     *
     * @param value the value of the <code>forum_node</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setForum_node(NavigationNodeResource value)
    {
        try
        {
            if(value != null)
            {
                set(forum_nodeDef, value);
            }
            else
            {
                unset(forum_nodeDef);
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
	 * Checks if the value of the <code>forum_node</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>forum_node</code> attribute is defined.
	 */
    public boolean isForum_nodeDefined()
	{
	    return isDefined(forum_nodeDef);
	}
 
    /**
     * Returns the value of the <code>initial_commentary_state</code> attribute.
     *
     * @return the value of the <code>initial_commentary_state</code> attribute.
     */
    public StateResource getInitial_commentary_state()
    {
        return (StateResource)get(initial_commentary_stateDef);
    }
    
    /**
     * Returns the value of the <code>initial_commentary_state</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>initial_commentary_state</code> attribute.
     */
    public StateResource getInitial_commentary_state(StateResource defaultValue)
    {
        if(isDefined(initial_commentary_stateDef))
        {
            return (StateResource)get(initial_commentary_stateDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>initial_commentary_state</code> attribute.
     *
     * @param value the value of the <code>initial_commentary_state</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setInitial_commentary_state(StateResource value)
    {
        try
        {
            if(value != null)
            {
                set(initial_commentary_stateDef, value);
            }
            else
            {
                unset(initial_commentary_stateDef);
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
	 * Checks if the value of the <code>initial_commentary_state</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>initial_commentary_state</code> attribute is defined.
	 */
    public boolean isInitial_commentary_stateDefined()
	{
	    return isDefined(initial_commentary_stateDef);
	}
 
    /**
     * Returns the value of the <code>reply_to</code> attribute.
     *
     * @return the value of the <code>reply_to</code> attribute.
     */
    public String getReply_to()
    {
        return (String)get(reply_toDef);
    }
    
    /**
     * Returns the value of the <code>reply_to</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>reply_to</code> attribute.
     */
    public String getReply_to(String defaultValue)
    {
        if(isDefined(reply_toDef))
        {
            return (String)get(reply_toDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>reply_to</code> attribute.
     *
     * @param value the value of the <code>reply_to</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setReply_to(String value)
    {
        try
        {
            if(value != null)
            {
                set(reply_toDef, value);
            }
            else
            {
                unset(reply_toDef);
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
	 * Checks if the value of the <code>reply_to</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>reply_to</code> attribute is defined.
	 */
    public boolean isReply_toDefined()
	{
	    return isDefined(reply_toDef);
	}
 
    /**
     * Returns the value of the <code>site</code> attribute.
     *
     * @return the value of the <code>site</code> attribute.
     */
    public SiteResource getSite()
    {
        return (SiteResource)get(siteDef);
    }
 
    /**
     * Sets the value of the <code>site</code> attribute.
     *
     * @param value the value of the <code>site</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setSite(SiteResource value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(siteDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute site "+
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
