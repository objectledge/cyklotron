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
import org.objectledge.coral.store.Resource;

/**
 * Defines the accessor methods of <code>integration.resource_class</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface ResourceClassResource
    extends Resource, Node
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "integration.resource_class";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>aggregationCopyAction</code> attribute.
     *
     * @return the value of the the <code>aggregationCopyAction</code> attribute.
     */
    public String getAggregationCopyAction();

    /**
     * Sets the value of the <code>aggregationCopyAction</code> attribute.
     *
     * @param value the value of the <code>aggregationCopyAction</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregationCopyAction(String value);   
   
	/**
	 * Checks if the value of the <code>aggregationCopyAction</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregationCopyAction</code> attribute is defined.
	 */
    public boolean isAggregationCopyActionDefined();
 
    /**
     * Returns the value of the <code>aggregationParentClasses</code> attribute.
     *
     * @return the value of the the <code>aggregationParentClasses</code> attribute.
     */
    public String getAggregationParentClasses();

    /**
     * Sets the value of the <code>aggregationParentClasses</code> attribute.
     *
     * @param value the value of the <code>aggregationParentClasses</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregationParentClasses(String value);   
   
	/**
	 * Checks if the value of the <code>aggregationParentClasses</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregationParentClasses</code> attribute is defined.
	 */
    public boolean isAggregationParentClassesDefined();
 
    /**
     * Returns the value of the <code>aggregationTargetPaths</code> attribute.
     *
     * @return the value of the the <code>aggregationTargetPaths</code> attribute.
     */
    public String getAggregationTargetPaths();

    /**
     * Sets the value of the <code>aggregationTargetPaths</code> attribute.
     *
     * @param value the value of the <code>aggregationTargetPaths</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregationTargetPaths(String value);   
   
	/**
	 * Checks if the value of the <code>aggregationTargetPaths</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregationTargetPaths</code> attribute is defined.
	 */
    public boolean isAggregationTargetPathsDefined();
 
    /**
     * Returns the value of the <code>aggregationUpdateAction</code> attribute.
     *
     * @return the value of the the <code>aggregationUpdateAction</code> attribute.
     */
    public String getAggregationUpdateAction();

    /**
     * Sets the value of the <code>aggregationUpdateAction</code> attribute.
     *
     * @param value the value of the <code>aggregationUpdateAction</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregationUpdateAction(String value);   
   
	/**
	 * Checks if the value of the <code>aggregationUpdateAction</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregationUpdateAction</code> attribute is defined.
	 */
    public boolean isAggregationUpdateActionDefined();
	
    /**
     * Returns the value of the <code>categorizable</code> attribute.
     *
     * @return the value of the the <code>categorizable</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getCategorizable()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>categorizable</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>categorizable</code> attribute.
     */
    public boolean getCategorizable(boolean defaultValue);

    /**
     * Sets the value of the <code>categorizable</code> attribute.
     *
     * @param value the value of the <code>categorizable</code> attribute.
     */
    public void setCategorizable(boolean value);

	/**
     * Removes the value of the <code>categorizable</code> attribute.
     */
    public void unsetCategorizable();
   
	/**
	 * Checks if the value of the <code>categorizable</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>categorizable</code> attribute is defined.
	 */
    public boolean isCategorizableDefined();
 
    /**
     * Returns the value of the <code>image</code> attribute.
     *
     * @return the value of the the <code>image</code> attribute.
     */
    public String getImage();

    /**
     * Sets the value of the <code>image</code> attribute.
     *
     * @param value the value of the <code>image</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setImage(String value);   
   
	/**
	 * Checks if the value of the <code>image</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>image</code> attribute is defined.
	 */
    public boolean isImageDefined();
 
    /**
     * Returns the value of the <code>indexDescription</code> attribute.
     *
     * @return the value of the the <code>indexDescription</code> attribute.
     */
    public String getIndexDescription();

    /**
     * Sets the value of the <code>indexDescription</code> attribute.
     *
     * @param value the value of the <code>indexDescription</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setIndexDescription(String value);   
   
	/**
	 * Checks if the value of the <code>indexDescription</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>indexDescription</code> attribute is defined.
	 */
    public boolean isIndexDescriptionDefined();
 
    /**
     * Returns the value of the <code>indexTitle</code> attribute.
     *
     * @return the value of the the <code>indexTitle</code> attribute.
     */
    public String getIndexTitle();

    /**
     * Sets the value of the <code>indexTitle</code> attribute.
     *
     * @param value the value of the <code>indexTitle</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setIndexTitle(String value);   
   
	/**
	 * Checks if the value of the <code>indexTitle</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>indexTitle</code> attribute is defined.
	 */
    public boolean isIndexTitleDefined();
 
    /**
     * Returns the value of the <code>indexableFields</code> attribute.
     *
     * @return the value of the the <code>indexableFields</code> attribute.
     */
    public String getIndexableFields();

    /**
     * Sets the value of the <code>indexableFields</code> attribute.
     *
     * @param value the value of the <code>indexableFields</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setIndexableFields(String value);   
   
	/**
	 * Checks if the value of the <code>indexableFields</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>indexableFields</code> attribute is defined.
	 */
    public boolean isIndexableFieldsDefined();
 
    /**
     * Returns the value of the <code>relatedQuickAddView</code> attribute.
     *
     * @return the value of the the <code>relatedQuickAddView</code> attribute.
     */
    public String getRelatedQuickAddView();

    /**
     * Sets the value of the <code>relatedQuickAddView</code> attribute.
     *
     * @param value the value of the <code>relatedQuickAddView</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRelatedQuickAddView(String value);   
   
	/**
	 * Checks if the value of the <code>relatedQuickAddView</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>relatedQuickAddView</code> attribute is defined.
	 */
    public boolean isRelatedQuickAddViewDefined();
	
    /**
     * Returns the value of the <code>relatedSupported</code> attribute.
     *
     * @return the value of the the <code>relatedSupported</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getRelatedSupported()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>relatedSupported</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>relatedSupported</code> attribute.
     */
    public boolean getRelatedSupported(boolean defaultValue);

    /**
     * Sets the value of the <code>relatedSupported</code> attribute.
     *
     * @param value the value of the <code>relatedSupported</code> attribute.
     */
    public void setRelatedSupported(boolean value);

	/**
     * Removes the value of the <code>relatedSupported</code> attribute.
     */
    public void unsetRelatedSupported();
   
	/**
	 * Checks if the value of the <code>relatedSupported</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>relatedSupported</code> attribute is defined.
	 */
    public boolean isRelatedSupportedDefined();
 
    /**
     * Returns the value of the <code>view</code> attribute.
     *
     * @return the value of the the <code>view</code> attribute.
     */
    public String getView();

    /**
     * Sets the value of the <code>view</code> attribute.
     *
     * @param value the value of the <code>view</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setView(String value);   
   
	/**
	 * Checks if the value of the <code>view</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>view</code> attribute is defined.
	 */
    public boolean isViewDefined();
  
    // @custom methods ///////////////////////////////////////////////////////

    public String[] getAggregationParentClassesList();
    
    public String[] getAggregationTargetPathsList();
}
