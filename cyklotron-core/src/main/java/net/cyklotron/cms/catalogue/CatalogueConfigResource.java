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
 
package net.cyklotron.cms.catalogue;

import java.util.Set;

import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.CmsNodeResource;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.search.PoolResource;

/**
 * Defines the accessor methods of <code>cms.catalogue.config</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface CatalogueConfigResource
    extends Resource, CmsNodeResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the Coral resource class. */    
    public static final String CLASS_NAME = "cms.catalogue.config";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>category</code> attribute.
     *
     * @return the value of the the <code>category</code> attribute.
     */
    public CategoryResource getCategory();
    
    /**
     * Returns the value of the <code>category</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>category</code> attribute.
     */
    public CategoryResource getCategory(CategoryResource defaultValue);

    /**
     * Sets the value of the <code>category</code> attribute.
     *
     * @param value the value of the <code>category</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCategory(CategoryResource value);   
   
	/**
	 * Checks if the value of the <code>category</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>category</code> attribute is defined.
	 */
    public boolean isCategoryDefined();
 
    /**
     * Returns the value of the <code>requiredPropertyNames</code> attribute.
     *
     * @return the value of the the <code>requiredPropertyNames</code> attribute.
     */
    public String getRequiredPropertyNames();
    
    /**
     * Returns the value of the <code>requiredPropertyNames</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>requiredPropertyNames</code> attribute.
     */
    public String getRequiredPropertyNames(String defaultValue);

    /**
     * Sets the value of the <code>requiredPropertyNames</code> attribute.
     *
     * @param value the value of the <code>requiredPropertyNames</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRequiredPropertyNames(String value);   
   
	/**
	 * Checks if the value of the <code>requiredPropertyNames</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>requiredPropertyNames</code> attribute is defined.
	 */
    public boolean isRequiredPropertyNamesDefined();
 
    /**
     * Returns the value of the <code>searchPool</code> attribute.
     *
     * @return the value of the the <code>searchPool</code> attribute.
     */
    public PoolResource getSearchPool();
    
    /**
     * Returns the value of the <code>searchPool</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>searchPool</code> attribute.
     */
    public PoolResource getSearchPool(PoolResource defaultValue);

    /**
     * Sets the value of the <code>searchPool</code> attribute.
     *
     * @param value the value of the <code>searchPool</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSearchPool(PoolResource value);   
   
	/**
	 * Checks if the value of the <code>searchPool</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>searchPool</code> attribute is defined.
	 */
    public boolean isSearchPoolDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
    // @import java.util.Set
    
    public Set<IndexCard.Property> getRequiredProperties();
    
    public void setRequiredProperties(Set<IndexCard.Property> properties);
}
