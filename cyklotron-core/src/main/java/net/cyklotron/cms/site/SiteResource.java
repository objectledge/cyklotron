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
 
package net.cyklotron.cms.site;

import org.objectledge.coral.datatypes.Node;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.store.Resource;

/**
 * Defines the accessor methods of <code>site.site</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface SiteResource
    extends Resource, Node
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "site.site";

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
     * Returns the value of the <code>layoutAdministrator</code> attribute.
     *
     * @return the value of the the <code>layoutAdministrator</code> attribute.
     */
    public Role getLayoutAdministrator();

    /**
     * Sets the value of the <code>layoutAdministrator</code> attribute.
     *
     * @param value the value of the <code>layoutAdministrator</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLayoutAdministrator(Role value);   
   
	/**
	 * Checks if the value of the <code>layoutAdministrator</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>layoutAdministrator</code> attribute is defined.
	 */
    public boolean isLayoutAdministratorDefined();
 
    /**
     * Returns the value of the <code>siteRole</code> attribute.
     *
     * @return the value of the the <code>siteRole</code> attribute.
     */
    public Role getSiteRole();

    /**
     * Sets the value of the <code>siteRole</code> attribute.
     *
     * @param value the value of the <code>siteRole</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSiteRole(Role value);   
   
	/**
	 * Checks if the value of the <code>siteRole</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>siteRole</code> attribute is defined.
	 */
    public boolean isSiteRoleDefined();
 
    /**
     * Returns the value of the <code>teamMember</code> attribute.
     *
     * @return the value of the the <code>teamMember</code> attribute.
     */
    public Role getTeamMember();

    /**
     * Sets the value of the <code>teamMember</code> attribute.
     *
     * @param value the value of the <code>teamMember</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTeamMember(Role value);   
   
	/**
	 * Checks if the value of the <code>teamMember</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>teamMember</code> attribute is defined.
	 */
    public boolean isTeamMemberDefined();
	
    /**
     * Returns the value of the <code>template</code> attribute.
     *
     * @return the value of the the <code>template</code> attribute.
     */
    public boolean getTemplate();

    /**
     * Sets the value of the <code>template</code> attribute.
     *
     * @param value the value of the <code>template</code> attribute.
     */
    public void setTemplate(boolean value);
     
    // @custom methods ///////////////////////////////////////////////////////
}
