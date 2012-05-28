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

import org.objectledge.coral.security.Permission;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.CmsNodeResource;

/**
 * Defines the accessor methods of <code>integration.preference</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface PreferenceResource
    extends Resource, CmsNodeResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the Coral resource class. */    
    public static final String CLASS_NAME = "integration.preference";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>default</code> attribute.
     *
     * @return the value of the the <code>default</code> attribute.
     */
    public String getDefault();
    
    /**
     * Returns the value of the <code>default</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>default</code> attribute.
     */
    public String getDefault(String defaultValue);

    /**
     * Sets the value of the <code>default</code> attribute.
     *
     * @param value the value of the <code>default</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setDefault(String value);   
   
	/**
	 * Checks if the value of the <code>default</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>default</code> attribute is defined.
	 */
    public boolean isDefaultDefined();
 
    /**
     * Returns the value of the <code>modifyPermission</code> attribute.
     *
     * @return the value of the the <code>modifyPermission</code> attribute.
     */
    public Permission getModifyPermission();
    
    /**
     * Returns the value of the <code>modifyPermission</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>modifyPermission</code> attribute.
     */
    public Permission getModifyPermission(Permission defaultValue);

    /**
     * Sets the value of the <code>modifyPermission</code> attribute.
     *
     * @param value the value of the <code>modifyPermission</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setModifyPermission(Permission value);   
   
	/**
	 * Checks if the value of the <code>modifyPermission</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>modifyPermission</code> attribute is defined.
	 */
    public boolean isModifyPermissionDefined();
	
    /**
     * Returns the value of the <code>required</code> attribute.
     *
     * @return the value of the the <code>required</code> attribute.
     */
    public boolean getRequired();

    /**
     * Sets the value of the <code>required</code> attribute.
     *
     * @param value the value of the <code>required</code> attribute.
     */
    public void setRequired(boolean value);
    
    /**
     * Returns the value of the <code>scope</code> attribute.
     *
     * @return the value of the the <code>scope</code> attribute.
     */
    public String getScope();
 
    /**
     * Sets the value of the <code>scope</code> attribute.
     *
     * @param value the value of the <code>scope</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setScope(String value)
        throws ValueRequiredException;
    
    /**
     * Returns the value of the <code>uiHint</code> attribute.
     *
     * @return the value of the the <code>uiHint</code> attribute.
     */
    public String getUiHint();
    
    /**
     * Returns the value of the <code>uiHint</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>uiHint</code> attribute.
     */
    public String getUiHint(String defaultValue);

    /**
     * Sets the value of the <code>uiHint</code> attribute.
     *
     * @param value the value of the <code>uiHint</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setUiHint(String value);   
   
	/**
	 * Checks if the value of the <code>uiHint</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>uiHint</code> attribute is defined.
	 */
    public boolean isUiHintDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
}
