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

import org.objectledge.coral.datatypes.Node;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

/**
 * Defines the accessor methods of <code>integration.preference</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface PreferenceResource
    extends Resource, Node
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "integration.preference";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>default</code> attribute.
     *
     * @return the value of the the <code>default</code> attribute.
     */
    public String getDefault();

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
     * Returns the value of the <code>modify_permission</code> attribute.
     *
     * @return the value of the the <code>modify_permission</code> attribute.
     */
    public Permission getModify_permission();

    /**
     * Sets the value of the <code>modify_permission</code> attribute.
     *
     * @param value the value of the <code>modify_permission</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setModify_permission(Permission value);   
   
	/**
	 * Checks if the value of the <code>modify_permission</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>modify_permission</code> attribute is defined.
	 */
    public boolean isModify_permissionDefined();
	
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
     * Returns the value of the <code>ui_hint</code> attribute.
     *
     * @return the value of the the <code>ui_hint</code> attribute.
     */
    public String getUi_hint();

    /**
     * Sets the value of the <code>ui_hint</code> attribute.
     *
     * @param value the value of the <code>ui_hint</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setUi_hint(String value);   
   
	/**
	 * Checks if the value of the <code>ui_hint</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>ui_hint</code> attribute is defined.
	 */
    public boolean isUi_hintDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
}
