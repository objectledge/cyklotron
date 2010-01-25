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

import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.CmsNodeResource;

/**
 * Defines the accessor methods of <code>integration.schema_role</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface SchemaRoleResource
    extends Resource, CmsNodeResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the Coral resource class. */    
    public static final String CLASS_NAME = "integration.schema_role";

    // public interface //////////////////////////////////////////////////////
	
    /**
     * Returns the value of the <code>deletable</code> attribute.
     *
     * @return the value of the the <code>deletable</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getDeletable()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>deletable</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>deletable</code> attribute.
     */
    public boolean getDeletable(boolean defaultValue);

    /**
     * Sets the value of the <code>deletable</code> attribute.
     *
     * @param value the value of the <code>deletable</code> attribute.
     */
    public void setDeletable(boolean value);

	/**
     * Removes the value of the <code>deletable</code> attribute.
     */
    public void unsetDeletable();
   
	/**
	 * Checks if the value of the <code>deletable</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>deletable</code> attribute is defined.
	 */
    public boolean isDeletableDefined();
	
    /**
     * Returns the value of the <code>recursive</code> attribute.
     *
     * @return the value of the the <code>recursive</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getRecursive()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>recursive</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>recursive</code> attribute.
     */
    public boolean getRecursive(boolean defaultValue);

    /**
     * Sets the value of the <code>recursive</code> attribute.
     *
     * @param value the value of the <code>recursive</code> attribute.
     */
    public void setRecursive(boolean value);

	/**
     * Removes the value of the <code>recursive</code> attribute.
     */
    public void unsetRecursive();
   
	/**
	 * Checks if the value of the <code>recursive</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>recursive</code> attribute is defined.
	 */
    public boolean isRecursiveDefined();
 
    /**
     * Returns the value of the <code>roleAttributeName</code> attribute.
     *
     * @return the value of the the <code>roleAttributeName</code> attribute.
     */
    public String getRoleAttributeName();

    /**
     * Sets the value of the <code>roleAttributeName</code> attribute.
     *
     * @param value the value of the <code>roleAttributeName</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRoleAttributeName(String value);   
   
	/**
	 * Checks if the value of the <code>roleAttributeName</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>roleAttributeName</code> attribute is defined.
	 */
    public boolean isRoleAttributeNameDefined();
	
    /**
     * Returns the value of the <code>subtreeRole</code> attribute.
     *
     * @return the value of the the <code>subtreeRole</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getSubtreeRole()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>subtreeRole</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>subtreeRole</code> attribute.
     */
    public boolean getSubtreeRole(boolean defaultValue);

    /**
     * Sets the value of the <code>subtreeRole</code> attribute.
     *
     * @param value the value of the <code>subtreeRole</code> attribute.
     */
    public void setSubtreeRole(boolean value);

	/**
     * Removes the value of the <code>subtreeRole</code> attribute.
     */
    public void unsetSubtreeRole();
   
	/**
	 * Checks if the value of the <code>subtreeRole</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>subtreeRole</code> attribute is defined.
	 */
    public boolean isSubtreeRoleDefined();
 
    /**
     * Returns the value of the <code>suffixAttributeName</code> attribute.
     *
     * @return the value of the the <code>suffixAttributeName</code> attribute.
     */
    public String getSuffixAttributeName();

    /**
     * Sets the value of the <code>suffixAttributeName</code> attribute.
     *
     * @param value the value of the <code>suffixAttributeName</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSuffixAttributeName(String value);   
   
	/**
	 * Checks if the value of the <code>suffixAttributeName</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>suffixAttributeName</code> attribute is defined.
	 */
    public boolean isSuffixAttributeNameDefined();
 
    /**
     * Returns the value of the <code>superRole</code> attribute.
     *
     * @return the value of the the <code>superRole</code> attribute.
     */
    public Resource getSuperRole();

    /**
     * Sets the value of the <code>superRole</code> attribute.
     *
     * @param value the value of the <code>superRole</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSuperRole(Resource value);   
   
	/**
	 * Checks if the value of the <code>superRole</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>superRole</code> attribute is defined.
	 */
    public boolean isSuperRoleDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
}
