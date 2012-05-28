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
 
package net.cyklotron.cms.syndication;

import java.util.Date;

import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.CmsNodeResource;
import net.cyklotron.cms.category.query.CategoryQueryResource;

/**
 * Defines the accessor methods of <code>cms.syndication.outgoingfeed</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface OutgoingFeedResource
    extends Resource, CmsNodeResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the Coral resource class. */    
    public static final String CLASS_NAME = "cms.syndication.outgoingfeed";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>category</code> attribute.
     *
     * @return the value of the the <code>category</code> attribute.
     */
    public String getCategory();
    
    /**
     * Returns the value of the <code>category</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>category</code> attribute.
     */
    public String getCategory(String defaultValue);

    /**
     * Sets the value of the <code>category</code> attribute.
     *
     * @param value the value of the <code>category</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCategory(String value);   
   
	/**
	 * Checks if the value of the <code>category</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>category</code> attribute is defined.
	 */
    public boolean isCategoryDefined();
 
    /**
     * Returns the value of the <code>categoryQuery</code> attribute.
     *
     * @return the value of the the <code>categoryQuery</code> attribute.
     */
    public CategoryQueryResource getCategoryQuery();
    
    /**
     * Returns the value of the <code>categoryQuery</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>categoryQuery</code> attribute.
     */
    public CategoryQueryResource getCategoryQuery(CategoryQueryResource defaultValue);

    /**
     * Sets the value of the <code>categoryQuery</code> attribute.
     *
     * @param value the value of the <code>categoryQuery</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCategoryQuery(CategoryQueryResource value);   
   
	/**
	 * Checks if the value of the <code>categoryQuery</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>categoryQuery</code> attribute is defined.
	 */
    public boolean isCategoryQueryDefined();
 
    /**
     * Returns the value of the <code>contents</code> attribute.
     *
     * @return the value of the the <code>contents</code> attribute.
     */
    public String getContents();
    
    /**
     * Returns the value of the <code>contents</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>contents</code> attribute.
     */
    public String getContents(String defaultValue);

    /**
     * Sets the value of the <code>contents</code> attribute.
     *
     * @param value the value of the <code>contents</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setContents(String value);   
   
	/**
	 * Checks if the value of the <code>contents</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>contents</code> attribute is defined.
	 */
    public boolean isContentsDefined();
 
    /**
     * Returns the value of the <code>copyright</code> attribute.
     *
     * @return the value of the the <code>copyright</code> attribute.
     */
    public String getCopyright();
    
    /**
     * Returns the value of the <code>copyright</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>copyright</code> attribute.
     */
    public String getCopyright(String defaultValue);

    /**
     * Sets the value of the <code>copyright</code> attribute.
     *
     * @param value the value of the <code>copyright</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCopyright(String value);   
   
	/**
	 * Checks if the value of the <code>copyright</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>copyright</code> attribute is defined.
	 */
    public boolean isCopyrightDefined();
 
    /**
     * Returns the value of the <code>generationTemplate</code> attribute.
     *
     * @return the value of the the <code>generationTemplate</code> attribute.
     */
    public String getGenerationTemplate();
    
    /**
     * Returns the value of the <code>generationTemplate</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>generationTemplate</code> attribute.
     */
    public String getGenerationTemplate(String defaultValue);

    /**
     * Sets the value of the <code>generationTemplate</code> attribute.
     *
     * @param value the value of the <code>generationTemplate</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setGenerationTemplate(String value);   
   
	/**
	 * Checks if the value of the <code>generationTemplate</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>generationTemplate</code> attribute is defined.
	 */
    public boolean isGenerationTemplateDefined();
	
    /**
     * Returns the value of the <code>interval</code> attribute.
     *
     * @return the value of the the <code>interval</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getInterval()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>interval</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>interval</code> attribute.
     */
    public int getInterval(int defaultValue);

    /**
     * Sets the value of the <code>interval</code> attribute.
     *
     * @param value the value of the <code>interval</code> attribute.
     */
    public void setInterval(int value);

	/**
     * Removes the value of the <code>interval</code> attribute.
     */
    public void unsetInterval();
   
	/**
	 * Checks if the value of the <code>interval</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>interval</code> attribute is defined.
	 */
    public boolean isIntervalDefined();
 
    /**
     * Returns the value of the <code>language</code> attribute.
     *
     * @return the value of the the <code>language</code> attribute.
     */
    public String getLanguage();
    
    /**
     * Returns the value of the <code>language</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>language</code> attribute.
     */
    public String getLanguage(String defaultValue);

    /**
     * Sets the value of the <code>language</code> attribute.
     *
     * @param value the value of the <code>language</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLanguage(String value);   
   
	/**
	 * Checks if the value of the <code>language</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>language</code> attribute is defined.
	 */
    public boolean isLanguageDefined();
 
    /**
     * Returns the value of the <code>lastUpdate</code> attribute.
     *
     * @return the value of the the <code>lastUpdate</code> attribute.
     */
    public Date getLastUpdate();
    
    /**
     * Returns the value of the <code>lastUpdate</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lastUpdate</code> attribute.
     */
    public Date getLastUpdate(Date defaultValue);

    /**
     * Sets the value of the <code>lastUpdate</code> attribute.
     *
     * @param value the value of the <code>lastUpdate</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLastUpdate(Date value);   
   
	/**
	 * Checks if the value of the <code>lastUpdate</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lastUpdate</code> attribute is defined.
	 */
    public boolean isLastUpdateDefined();
	
    /**
     * Returns the value of the <code>limit</code> attribute.
     *
     * @return the value of the the <code>limit</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getLimit()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>limit</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>limit</code> attribute.
     */
    public int getLimit(int defaultValue);

    /**
     * Sets the value of the <code>limit</code> attribute.
     *
     * @param value the value of the <code>limit</code> attribute.
     */
    public void setLimit(int value);

	/**
     * Removes the value of the <code>limit</code> attribute.
     */
    public void unsetLimit();
   
	/**
	 * Checks if the value of the <code>limit</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>limit</code> attribute is defined.
	 */
    public boolean isLimitDefined();
 
    /**
     * Returns the value of the <code>managingEditor</code> attribute.
     *
     * @return the value of the the <code>managingEditor</code> attribute.
     */
    public String getManagingEditor();
    
    /**
     * Returns the value of the <code>managingEditor</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>managingEditor</code> attribute.
     */
    public String getManagingEditor(String defaultValue);

    /**
     * Sets the value of the <code>managingEditor</code> attribute.
     *
     * @param value the value of the <code>managingEditor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setManagingEditor(String value);   
   
	/**
	 * Checks if the value of the <code>managingEditor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>managingEditor</code> attribute is defined.
	 */
    public boolean isManagingEditorDefined();
	
    /**
     * Returns the value of the <code>offset</code> attribute.
     *
     * @return the value of the the <code>offset</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getOffset()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>offset</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>offset</code> attribute.
     */
    public int getOffset(int defaultValue);

    /**
     * Sets the value of the <code>offset</code> attribute.
     *
     * @param value the value of the <code>offset</code> attribute.
     */
    public void setOffset(int value);

	/**
     * Removes the value of the <code>offset</code> attribute.
     */
    public void unsetOffset();
   
	/**
	 * Checks if the value of the <code>offset</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>offset</code> attribute is defined.
	 */
    public boolean isOffsetDefined();
	
    /**
     * Returns the value of the <code>public</code> attribute.
     *
     * @return the value of the the <code>public</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getPublic()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>public</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>public</code> attribute.
     */
    public boolean getPublic(boolean defaultValue);

    /**
     * Sets the value of the <code>public</code> attribute.
     *
     * @param value the value of the <code>public</code> attribute.
     */
    public void setPublic(boolean value);

	/**
     * Removes the value of the <code>public</code> attribute.
     */
    public void unsetPublic();
   
	/**
	 * Checks if the value of the <code>public</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>public</code> attribute is defined.
	 */
    public boolean isPublicDefined();
 
    /**
     * Returns the value of the <code>sortColumn</code> attribute.
     *
     * @return the value of the the <code>sortColumn</code> attribute.
     */
    public String getSortColumn();
    
    /**
     * Returns the value of the <code>sortColumn</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>sortColumn</code> attribute.
     */
    public String getSortColumn(String defaultValue);

    /**
     * Sets the value of the <code>sortColumn</code> attribute.
     *
     * @param value the value of the <code>sortColumn</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSortColumn(String value);   
   
	/**
	 * Checks if the value of the <code>sortColumn</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>sortColumn</code> attribute is defined.
	 */
    public boolean isSortColumnDefined();
	
    /**
     * Returns the value of the <code>sortOrder</code> attribute.
     *
     * @return the value of the the <code>sortOrder</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getSortOrder()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>sortOrder</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>sortOrder</code> attribute.
     */
    public boolean getSortOrder(boolean defaultValue);

    /**
     * Sets the value of the <code>sortOrder</code> attribute.
     *
     * @param value the value of the <code>sortOrder</code> attribute.
     */
    public void setSortOrder(boolean value);

	/**
     * Removes the value of the <code>sortOrder</code> attribute.
     */
    public void unsetSortOrder();
   
	/**
	 * Checks if the value of the <code>sortOrder</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>sortOrder</code> attribute is defined.
	 */
    public boolean isSortOrderDefined();
 
    /**
     * Returns the value of the <code>webMaster</code> attribute.
     *
     * @return the value of the the <code>webMaster</code> attribute.
     */
    public String getWebMaster();
    
    /**
     * Returns the value of the <code>webMaster</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>webMaster</code> attribute.
     */
    public String getWebMaster(String defaultValue);

    /**
     * Sets the value of the <code>webMaster</code> attribute.
     *
     * @param value the value of the <code>webMaster</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setWebMaster(String value);   
   
	/**
	 * Checks if the value of the <code>webMaster</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>webMaster</code> attribute is defined.
	 */
    public boolean isWebMasterDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
}
