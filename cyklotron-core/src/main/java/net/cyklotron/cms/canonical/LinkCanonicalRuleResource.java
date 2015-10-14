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
 
package net.cyklotron.cms.canonical;

import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.CmsNodeResource;
import net.cyklotron.cms.PrioritizedResource;
import net.cyklotron.cms.site.SiteResource;

/**
 * Defines the accessor methods of <code>cms.canonical.link_canonical_rule</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface LinkCanonicalRuleResource
    extends Resource, CmsNodeResource, PrioritizedResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the Coral resource class. */    
    public static final String CLASS_NAME = "cms.canonical.link_canonical_rule";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>category</code> attribute.
     *
     * @return the value of the the <code>category</code> attribute.
     */
    public Resource getCategory();
 
    /**
     * Sets the value of the <code>category</code> attribute.
     *
     * @param value the value of the <code>category</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setCategory(Resource value)
        throws ValueRequiredException;
    
    /**
     * Returns the value of the <code>linkPattern</code> attribute.
     *
     * @return the value of the the <code>linkPattern</code> attribute.
     */
    public String getLinkPattern();
 
    /**
     * Sets the value of the <code>linkPattern</code> attribute.
     *
     * @param value the value of the <code>linkPattern</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setLinkPattern(String value)
        throws ValueRequiredException;
    
    /**
     * Returns the value of the <code>site</code> attribute.
     *
     * @return the value of the the <code>site</code> attribute.
     */
    public SiteResource getSite();
    
    /**
     * Returns the value of the <code>site</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>site</code> attribute.
     */
    public SiteResource getSite(SiteResource defaultValue);

    /**
     * Sets the value of the <code>site</code> attribute.
     *
     * @param value the value of the <code>site</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSite(SiteResource value);   
   
	/**
	 * Checks if the value of the <code>site</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>site</code> attribute is defined.
	 */
    public boolean isSiteDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
}
