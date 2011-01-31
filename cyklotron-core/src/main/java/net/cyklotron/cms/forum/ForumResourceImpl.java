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
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.workflow.StateResource;

/**
 * An implementation of <code>cms.forum.forum</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class ForumResourceImpl
    extends ForumNodeResourceImpl
    implements ForumResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>forumNode</code> attribute. */
    private static AttributeDefinition forumNodeDef;

    /** The AttributeDefinition object for the <code>initialCommentaryState</code> attribute. */
    private static AttributeDefinition initialCommentaryStateDef;

    /** The AttributeDefinition object for the <code>rejectMessage</code> attribute. */
    private static AttributeDefinition rejectMessageDef;

    /** The AttributeDefinition object for the <code>replyTo</code> attribute. */
    private static AttributeDefinition replyToDef;

    /** The AttributeDefinition object for the <code>site</code> attribute. */
    private static AttributeDefinition siteDef;
    
    /** The AttributeDefinition object for the <code>captchaEnabled</code> attribute. */
    private static AttributeDefinition captchaEnabledDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.forum.forum</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public ForumResourceImpl()
    {
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
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static ForumResource createForumResource(CoralSession session, String name, Resource
        parent, SiteResource site)
        throws ValueRequiredException, InvalidResourceNameException
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
     * Returns the value of the <code>forumNode</code> attribute.
     *
     * @return the value of the <code>forumNode</code> attribute.
     */
    public NavigationNodeResource getForumNode()
    {
        return (NavigationNodeResource)getInternal(forumNodeDef, null);
    }
    
    /**
     * Returns the value of the <code>forumNode</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>forumNode</code> attribute.
     */
    public NavigationNodeResource getForumNode(NavigationNodeResource defaultValue)
    {
        return (NavigationNodeResource)getInternal(forumNodeDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>forumNode</code> attribute.
     *
     * @param value the value of the <code>forumNode</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setForumNode(NavigationNodeResource value)
    {
        try
        {
            if(value != null)
            {
                set(forumNodeDef, value);
            }
            else
            {
                unset(forumNodeDef);
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
	 * Checks if the value of the <code>forumNode</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>forumNode</code> attribute is defined.
	 */
    public boolean isForumNodeDefined()
	{
	    return isDefined(forumNodeDef);
	}
 
    /**
     * Returns the value of the <code>initialCommentaryState</code> attribute.
     *
     * @return the value of the <code>initialCommentaryState</code> attribute.
     */
    public StateResource getInitialCommentaryState()
    {
        return (StateResource)getInternal(initialCommentaryStateDef, null);
    }
    
    /**
     * Returns the value of the <code>initialCommentaryState</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>initialCommentaryState</code> attribute.
     */
    public StateResource getInitialCommentaryState(StateResource defaultValue)
    {
        return (StateResource)getInternal(initialCommentaryStateDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>initialCommentaryState</code> attribute.
     *
     * @param value the value of the <code>initialCommentaryState</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setInitialCommentaryState(StateResource value)
    {
        try
        {
            if(value != null)
            {
                set(initialCommentaryStateDef, value);
            }
            else
            {
                unset(initialCommentaryStateDef);
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
	 * Checks if the value of the <code>initialCommentaryState</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>initialCommentaryState</code> attribute is defined.
	 */
    public boolean isInitialCommentaryStateDefined()
	{
	    return isDefined(initialCommentaryStateDef);
	}
 
    /**
     * Returns the value of the <code>rejectMessage</code> attribute.
     *
     * @return the value of the <code>rejectMessage</code> attribute.
     */
    public String getRejectMessage()
    {
        return (String)getInternal(rejectMessageDef, null);
    }
    
    /**
     * Returns the value of the <code>rejectMessage</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>rejectMessage</code> attribute.
     */
    public String getRejectMessage(String defaultValue)
    {
        return (String)getInternal(rejectMessageDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>rejectMessage</code> attribute.
     *
     * @param value the value of the <code>rejectMessage</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRejectMessage(String value)
    {
        try
        {
            if(value != null)
            {
                set(rejectMessageDef, value);
            }
            else
            {
                unset(rejectMessageDef);
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
	 * Checks if the value of the <code>rejectMessage</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>rejectMessage</code> attribute is defined.
	 */
    public boolean isRejectMessageDefined()
	{
	    return isDefined(rejectMessageDef);
	}
 
    /**
     * Returns the value of the <code>replyTo</code> attribute.
     *
     * @return the value of the <code>replyTo</code> attribute.
     */
    public String getReplyTo()
    {
        return (String)getInternal(replyToDef, null);
    }
    
    /**
     * Returns the value of the <code>replyTo</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>replyTo</code> attribute.
     */
    public String getReplyTo(String defaultValue)
    {
        return (String)getInternal(replyToDef, defaultValue);
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
     * Returns the value of the <code>site</code> attribute.
     *
     * @return the value of the <code>site</code> attribute.
     */
    public SiteResource getSite()
    {
        return (SiteResource)getInternal(siteDef, null);
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

    @Override
    public boolean getCaptchaEnabled()
    {
        return (Boolean) getInternal(captchaEnabledDef, false);
    }

    @Override
    public boolean isCaptchaEnabledDefined()
    {
        return isDefined(captchaEnabledDef);
    }

    @Override
    public void setCaptchaEnabled(boolean value)
    {
        try
        {
            set(captchaEnabledDef, value);
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
     
    // @custom methods ///////////////////////////////////////////////////////
}
