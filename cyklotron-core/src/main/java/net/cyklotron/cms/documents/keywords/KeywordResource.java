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
 
package net.cyklotron.cms.documents.keywords;

import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.CmsNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * Defines the accessor methods of <code>documents.keyword</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface KeywordResource
    extends Resource, CmsNodeResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the Coral resource class. */    
    public static final String CLASS_NAME = "documents.keyword";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>categories</code> attribute.
     *
     * @return the value of the the <code>categories</code> attribute.
     */
    public ResourceList getCategories();
    
    /**
     * Returns the value of the <code>categories</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>categories</code> attribute.
     */
    public ResourceList getCategories(ResourceList defaultValue);

    /**
     * Sets the value of the <code>categories</code> attribute.
     *
     * @param value the value of the <code>categories</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCategories(ResourceList value);   
   
	/**
	 * Checks if the value of the <code>categories</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>categories</code> attribute is defined.
	 */
    public boolean isCategoriesDefined();
	
    /**
     * Returns the value of the <code>external</code> attribute.
     *
     * @return the value of the the <code>external</code> attribute.
     */
    public boolean getExternal();

    /**
     * Sets the value of the <code>external</code> attribute.
     *
     * @param value the value of the <code>external</code> attribute.
     */
    public void setExternal(boolean value);
    
    /**
     * Returns the value of the <code>hrefExternal</code> attribute.
     *
     * @return the value of the the <code>hrefExternal</code> attribute.
     */
    public String getHrefExternal();
    
    /**
     * Returns the value of the <code>hrefExternal</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>hrefExternal</code> attribute.
     */
    public String getHrefExternal(String defaultValue);

    /**
     * Sets the value of the <code>hrefExternal</code> attribute.
     *
     * @param value the value of the <code>hrefExternal</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setHrefExternal(String value);   
   
	/**
	 * Checks if the value of the <code>hrefExternal</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>hrefExternal</code> attribute is defined.
	 */
    public boolean isHrefExternalDefined();
 
    /**
     * Returns the value of the <code>hrefInternal</code> attribute.
     *
     * @return the value of the the <code>hrefInternal</code> attribute.
     */
    public NavigationNodeResource getHrefInternal();
    
    /**
     * Returns the value of the <code>hrefInternal</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>hrefInternal</code> attribute.
     */
    public NavigationNodeResource getHrefInternal(NavigationNodeResource defaultValue);

    /**
     * Sets the value of the <code>hrefInternal</code> attribute.
     *
     * @param value the value of the <code>hrefInternal</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setHrefInternal(NavigationNodeResource value);   
   
	/**
	 * Checks if the value of the <code>hrefInternal</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>hrefInternal</code> attribute is defined.
	 */
    public boolean isHrefInternalDefined();
 
    /**
     * Returns the value of the <code>linkClass</code> attribute.
     *
     * @return the value of the the <code>linkClass</code> attribute.
     */
    public String getLinkClass();
    
    /**
     * Returns the value of the <code>linkClass</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>linkClass</code> attribute.
     */
    public String getLinkClass(String defaultValue);

    /**
     * Sets the value of the <code>linkClass</code> attribute.
     *
     * @param value the value of the <code>linkClass</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLinkClass(String value);   
   
	/**
	 * Checks if the value of the <code>linkClass</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>linkClass</code> attribute is defined.
	 */
    public boolean isLinkClassDefined();
	
    /**
     * Returns the value of the <code>newWindow</code> attribute.
     *
     * @return the value of the the <code>newWindow</code> attribute.
     */
    public boolean getNewWindow();

    /**
     * Sets the value of the <code>newWindow</code> attribute.
     *
     * @param value the value of the <code>newWindow</code> attribute.
     */
    public void setNewWindow(boolean value);
    
    /**
     * Returns the value of the <code>pattern</code> attribute.
     *
     * @return the value of the the <code>pattern</code> attribute.
     */
    public String getPattern();
 
    /**
     * Sets the value of the <code>pattern</code> attribute.
     *
     * @param value the value of the <code>pattern</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setPattern(String value)
        throws ValueRequiredException;
   	
    /**
     * Returns the value of the <code>regexp</code> attribute.
     *
     * @return the value of the the <code>regexp</code> attribute.
     */
    public boolean getRegexp();

    /**
     * Sets the value of the <code>regexp</code> attribute.
     *
     * @param value the value of the <code>regexp</code> attribute.
     */
    public void setRegexp(boolean value);
    
    /**
     * Returns the value of the <code>title</code> attribute.
     *
     * @return the value of the the <code>title</code> attribute.
     */
    public String getTitle();
    
    /**
     * Returns the value of the <code>title</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>title</code> attribute.
     */
    public String getTitle(String defaultValue);

    /**
     * Sets the value of the <code>title</code> attribute.
     *
     * @param value the value of the <code>title</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTitle(String value);   
   
	/**
	 * Checks if the value of the <code>title</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>title</code> attribute is defined.
	 */
    public boolean isTitleDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
}
