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
 
package net.cyklotron.cms.periodicals;

import java.util.Date;

import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.site.SiteResource;

/**
 * Defines the accessor methods of <code>cms.periodicals.periodical</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface PeriodicalResource
    extends Resource, PeriodicalsNodeResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "cms.periodicals.periodical";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>category_query_set</code> attribute.
     *
     * @return the value of the the <code>category_query_set</code> attribute.
     */
    public CategoryQueryPoolResource getCategory_query_set();

    /**
     * Sets the value of the <code>category_query_set</code> attribute.
     *
     * @param value the value of the <code>category_query_set</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCategory_query_set(CategoryQueryPoolResource value);   
   
	/**
	 * Checks if the value of the <code>category_query_set</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>category_query_set</code> attribute is defined.
	 */
    public boolean isCategory_query_setDefined();
 
    /**
     * Returns the value of the <code>encoding</code> attribute.
     *
     * @return the value of the the <code>encoding</code> attribute.
     */
    public String getEncoding();

    /**
     * Sets the value of the <code>encoding</code> attribute.
     *
     * @param value the value of the <code>encoding</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEncoding(String value);   
   
	/**
	 * Checks if the value of the <code>encoding</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>encoding</code> attribute is defined.
	 */
    public boolean isEncodingDefined();
 
    /**
     * Returns the value of the <code>last_published</code> attribute.
     *
     * @return the value of the the <code>last_published</code> attribute.
     */
    public Date getLast_published();

    /**
     * Sets the value of the <code>last_published</code> attribute.
     *
     * @param value the value of the <code>last_published</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLast_published(Date value);   
   
	/**
	 * Checks if the value of the <code>last_published</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>last_published</code> attribute is defined.
	 */
    public boolean isLast_publishedDefined();
 
    /**
     * Returns the value of the <code>locale</code> attribute.
     *
     * @return the value of the the <code>locale</code> attribute.
     */
    public String getLocale();

    /**
     * Sets the value of the <code>locale</code> attribute.
     *
     * @param value the value of the <code>locale</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLocale(String value);   
   
	/**
	 * Checks if the value of the <code>locale</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>locale</code> attribute is defined.
	 */
    public boolean isLocaleDefined();
 
    /**
     * Returns the value of the <code>renderer</code> attribute.
     *
     * @return the value of the the <code>renderer</code> attribute.
     */
    public String getRenderer();

    /**
     * Sets the value of the <code>renderer</code> attribute.
     *
     * @param value the value of the <code>renderer</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRenderer(String value);   
   
	/**
	 * Checks if the value of the <code>renderer</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>renderer</code> attribute is defined.
	 */
    public boolean isRendererDefined();
 
    /**
     * Returns the value of the <code>store_place</code> attribute.
     *
     * @return the value of the the <code>store_place</code> attribute.
     */
    public DirectoryResource getStore_place();

    /**
     * Sets the value of the <code>store_place</code> attribute.
     *
     * @param value the value of the <code>store_place</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setStore_place(DirectoryResource value);   
   
	/**
	 * Checks if the value of the <code>store_place</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>store_place</code> attribute is defined.
	 */
    public boolean isStore_placeDefined();
 
    /**
     * Returns the value of the <code>template</code> attribute.
     *
     * @return the value of the the <code>template</code> attribute.
     */
    public String getTemplate();

    /**
     * Sets the value of the <code>template</code> attribute.
     *
     * @param value the value of the <code>template</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTemplate(String value);   
   
	/**
	 * Checks if the value of the <code>template</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>template</code> attribute is defined.
	 */
    public boolean isTemplateDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
    
    // @import net.cyklotron.cms.site.SiteResource
    
    /**
     * Returns the selected publication times of the periodical.
     *
     * @return an array of PublicationTimeResource objects.     
     */
    public PublicationTimeResource[] getPublicationTimes();
    
    /**
     * Returns the site this periodical belongs to.
     * 
     * @return the site this periodical belongs to.
     */
    public SiteResource getSite();
}
