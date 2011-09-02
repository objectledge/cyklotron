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

import org.objectledge.coral.datatypes.WeakResourceList;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.CmsNodeResource;
import net.cyklotron.cms.ProtectedResource;

/**
 * Defines the accessor methods of <code>cms.forum.node</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface ForumNodeResource
    extends Resource, CmsNodeResource, ProtectedResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the Coral resource class. */    
    public static final String CLASS_NAME = "cms.forum.node";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>administrator</code> attribute.
     *
     * @return the value of the the <code>administrator</code> attribute.
     */
    public Role getAdministrator();

    /**
     * Sets the value of the <code>administrator</code> attribute.
     *
     * @param value the value of the <code>administrator</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAdministrator(Role value);   
   
	/**
	 * Checks if the value of the <code>administrator</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>administrator</code> attribute is defined.
	 */
    public boolean isAdministratorDefined();
 
    /**
     * Returns the value of the <code>lastlyAdded</code> attribute.
     *
     * @return the value of the the <code>lastlyAdded</code> attribute.
     */
    public WeakResourceList getLastlyAdded();

    /**
     * Sets the value of the <code>lastlyAdded</code> attribute.
     *
     * @param value the value of the <code>lastlyAdded</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLastlyAdded(WeakResourceList value);   
   
	/**
	 * Checks if the value of the <code>lastlyAdded</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lastlyAdded</code> attribute is defined.
	 */
    public boolean isLastlyAddedDefined();
	
    /**
     * Returns the value of the <code>lastlyAddedSize</code> attribute.
     *
     * @return the value of the the <code>lastlyAddedSize</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getLastlyAddedSize()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>lastlyAddedSize</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lastlyAddedSize</code> attribute.
     */
    public int getLastlyAddedSize(int defaultValue);

    /**
     * Sets the value of the <code>lastlyAddedSize</code> attribute.
     *
     * @param value the value of the <code>lastlyAddedSize</code> attribute.
     */
    public void setLastlyAddedSize(int value);

	/**
     * Removes the value of the <code>lastlyAddedSize</code> attribute.
     */
    public void unsetLastlyAddedSize();
   
	/**
	 * Checks if the value of the <code>lastlyAddedSize</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lastlyAddedSize</code> attribute is defined.
	 */
    public boolean isLastlyAddedSizeDefined();
 
    /**
     * Returns the value of the <code>moderator</code> attribute.
     *
     * @return the value of the the <code>moderator</code> attribute.
     */
    public Role getModerator();

    /**
     * Sets the value of the <code>moderator</code> attribute.
     *
     * @param value the value of the <code>moderator</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setModerator(Role value);   
   
	/**
	 * Checks if the value of the <code>moderator</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>moderator</code> attribute is defined.
	 */
    public boolean isModeratorDefined();
 
    /**
     * Returns the value of the <code>participant</code> attribute.
     *
     * @return the value of the the <code>participant</code> attribute.
     */
    public Role getParticipant();

    /**
     * Sets the value of the <code>participant</code> attribute.
     *
     * @param value the value of the <code>participant</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setParticipant(Role value);   
   
	/**
	 * Checks if the value of the <code>participant</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>participant</code> attribute is defined.
	 */
    public boolean isParticipantDefined();
 
    /**
     * Returns the value of the <code>visitor</code> attribute.
     *
     * @return the value of the the <code>visitor</code> attribute.
     */
    public Role getVisitor();

    /**
     * Sets the value of the <code>visitor</code> attribute.
     *
     * @param value the value of the <code>visitor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setVisitor(Role value);   
   
	/**
	 * Checks if the value of the <code>visitor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>visitor</code> attribute is defined.
	 */
    public boolean isVisitorDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
    
}
