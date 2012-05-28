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

import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.workflow.StateResource;

/**
 * Defines the accessor methods of <code>cms.forum.forum</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface ForumResource
    extends Resource, ForumNodeResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the Coral resource class. */    
    public static final String CLASS_NAME = "cms.forum.forum";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>forumNode</code> attribute.
     *
     * @return the value of the the <code>forumNode</code> attribute.
     */
    public NavigationNodeResource getForumNode();
    
    /**
     * Returns the value of the <code>forumNode</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>forumNode</code> attribute.
     */
    public NavigationNodeResource getForumNode(NavigationNodeResource defaultValue);

    /**
     * Sets the value of the <code>forumNode</code> attribute.
     *
     * @param value the value of the <code>forumNode</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setForumNode(NavigationNodeResource value);   
   
	/**
	 * Checks if the value of the <code>forumNode</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>forumNode</code> attribute is defined.
	 */
    public boolean isForumNodeDefined();
 
    /**
     * Returns the value of the <code>initialCommentaryState</code> attribute.
     *
     * @return the value of the the <code>initialCommentaryState</code> attribute.
     */
    public StateResource getInitialCommentaryState();
    
    /**
     * Returns the value of the <code>initialCommentaryState</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>initialCommentaryState</code> attribute.
     */
    public StateResource getInitialCommentaryState(StateResource defaultValue);

    /**
     * Sets the value of the <code>initialCommentaryState</code> attribute.
     *
     * @param value the value of the <code>initialCommentaryState</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setInitialCommentaryState(StateResource value);   
   
	/**
	 * Checks if the value of the <code>initialCommentaryState</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>initialCommentaryState</code> attribute is defined.
	 */
    public boolean isInitialCommentaryStateDefined();
 
    /**
     * Returns the value of the <code>rejectMessage</code> attribute.
     *
     * @return the value of the the <code>rejectMessage</code> attribute.
     */
    public String getRejectMessage();
    
    /**
     * Returns the value of the <code>rejectMessage</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>rejectMessage</code> attribute.
     */
    public String getRejectMessage(String defaultValue);

    /**
     * Sets the value of the <code>rejectMessage</code> attribute.
     *
     * @param value the value of the <code>rejectMessage</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRejectMessage(String value);   
   
	/**
	 * Checks if the value of the <code>rejectMessage</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>rejectMessage</code> attribute is defined.
	 */
    public boolean isRejectMessageDefined();
 
    /**
     * Returns the value of the <code>replyTo</code> attribute.
     *
     * @return the value of the the <code>replyTo</code> attribute.
     */
    public String getReplyTo();
    
    /**
     * Returns the value of the <code>replyTo</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>replyTo</code> attribute.
     */
    public String getReplyTo(String defaultValue);

    /**
     * Sets the value of the <code>replyTo</code> attribute.
     *
     * @param value the value of the <code>replyTo</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setReplyTo(String value);   
   
	/**
	 * Checks if the value of the <code>replyTo</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>replyTo</code> attribute is defined.
	 */
    public boolean isReplyToDefined();
 
    /**
     * Returns the value of the <code>site</code> attribute.
     *
     * @return the value of the the <code>site</code> attribute.
     */
    public SiteResource getSite();
 
    /**
     * Sets the value of the <code>site</code> attribute.
     *
     * @param value the value of the <code>site</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setSite(SiteResource value)
        throws ValueRequiredException;
     
    // @custom methods ///////////////////////////////////////////////////////
}
