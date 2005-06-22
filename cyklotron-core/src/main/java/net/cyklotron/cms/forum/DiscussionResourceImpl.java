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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.workflow.StateResource;

/**
 * An implementation of <code>cms.forum.discussion</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class DiscussionResourceImpl
    extends ForumNodeResourceImpl
    implements DiscussionResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>forum</code> attribute. */
    private static AttributeDefinition forumDef;

    /** The AttributeDefinition object for the <code>replyTo</code> attribute. */
    private static AttributeDefinition replyToDef;

    /** The AttributeDefinition object for the <code>state</code> attribute. */
    private static AttributeDefinition stateDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.forum.discussion</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public DiscussionResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.forum.discussion</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static DiscussionResource getDiscussionResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof DiscussionResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.forum.discussion");
        }
        return (DiscussionResource)res;
    }

    /**
     * Creates a new <code>cms.forum.discussion</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param forum the forum attribute
     * @return a new DiscussionResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static DiscussionResource createDiscussionResource(CoralSession session, String name,
        Resource parent, ForumResource forum)
        throws ValueRequiredException, InvalidResourceNameException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.forum.discussion");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("forum"), forum);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof DiscussionResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (DiscussionResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>forum</code> attribute.
     *
     * @return the value of the <code>forum</code> attribute.
     */
    public ForumResource getForum()
    {
        return (ForumResource)get(forumDef);
    }
 
    /**
     * Sets the value of the <code>forum</code> attribute.
     *
     * @param value the value of the <code>forum</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setForum(ForumResource value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(forumDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute forum "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>replyTo</code> attribute.
     *
     * @return the value of the <code>replyTo</code> attribute.
     */
    public String getReplyTo()
    {
        return (String)get(replyToDef);
    }
    
    /**
     * Returns the value of the <code>replyTo</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>replyTo</code> attribute.
     */
    public String getReplyTo(String defaultValue)
    {
        if(isDefined(replyToDef))
        {
            return (String)get(replyToDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>replyTo</code> attribute.
     *
     * @param value the value of the <code>replyTo</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setReplyTo(String value)
    {
        try
        {
            if(value != null)
            {
                set(replyToDef, value);
            }
            else
            {
                unset(replyToDef);
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
	 * Checks if the value of the <code>replyTo</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>replyTo</code> attribute is defined.
	 */
    public boolean isReplyToDefined()
	{
	    return isDefined(replyToDef);
	}
 
    /**
     * Returns the value of the <code>state</code> attribute.
     *
     * @return the value of the <code>state</code> attribute.
     */
    public StateResource getState()
    {
        return (StateResource)get(stateDef);
    }
    
    /**
     * Returns the value of the <code>state</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>state</code> attribute.
     */
    public StateResource getState(StateResource defaultValue)
    {
        if(isDefined(stateDef))
        {
            return (StateResource)get(stateDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>state</code> attribute.
     *
     * @param value the value of the <code>state</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setState(StateResource value)
    {
        try
        {
            if(value != null)
            {
                set(stateDef, value);
            }
            else
            {
                unset(stateDef);
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
	 * Checks if the value of the <code>state</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>state</code> attribute is defined.
	 */
    public boolean isStateDefined()
	{
	    return isDefined(stateDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
    // @import java.util.Date
    // @import org.objectledge.coral.security.Permission
    // @import org.objectledge.coral.security.Subject
    // @import org.objectledge.coral.session.CoralSession
    
    /**
     * Checks if this resource can be viewed at the given time.
     */
    public boolean isValid(Date time)
    {
        return true;
    }
    
	/** the message view permission */
	private Permission viewPermission;
    
	/** the message moderate permission */
	private Permission moderatePermission;

    /**
     * Checks if a given subject can view this resource.
     */
    public boolean canView(CoralSession coralSession, Subject subject)
    {
		if(moderatePermission == null)
		{
			moderatePermission = coralSession.getSecurity().getUniquePermission("cms.forum.moderate");
		}
		if(subject.hasPermission(this, moderatePermission))
		{
			return true;
		}
		if(getState().getName().equals("hidden"))
		{
			return false;
		}
        if(viewPermission == null)
        {
            viewPermission = coralSession.getSecurity().getUniquePermission("cms.forum.view");
        }
        // check view permission
        return subject.hasPermission(this, viewPermission);
    }
    
    /**
     * Checks if the specified subject can view this resource at the given time.
     */
    public boolean canView(CoralSession coralSession, Subject subject, Date time)
    {
        return canView(coralSession, subject);
    }

    // @extends cms.forum.node
}
