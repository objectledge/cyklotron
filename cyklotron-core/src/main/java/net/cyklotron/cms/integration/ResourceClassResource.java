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
     * Returns the value of the <code>aggregation_copy_action</code> attribute.
     *
     * @return the value of the the <code>aggregation_copy_action</code> attribute.
     */
    public String getAggregation_copy_action();

    /**
     * Sets the value of the <code>aggregation_copy_action</code> attribute.
     *
     * @param value the value of the <code>aggregation_copy_action</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregation_copy_action(String value);   
   
	/**
	 * Checks if the value of the <code>aggregation_copy_action</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregation_copy_action</code> attribute is defined.
	 */
    public boolean isAggregation_copy_actionDefined();
 
    /**
     * Returns the value of the <code>aggregation_parent_classes</code> attribute.
     *
     * @return the value of the the <code>aggregation_parent_classes</code> attribute.
     */
    public String getAggregation_parent_classes();

    /**
     * Sets the value of the <code>aggregation_parent_classes</code> attribute.
     *
     * @param value the value of the <code>aggregation_parent_classes</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregation_parent_classes(String value);   
   
	/**
	 * Checks if the value of the <code>aggregation_parent_classes</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregation_parent_classes</code> attribute is defined.
	 */
    public boolean isAggregation_parent_classesDefined();
 
    /**
     * Returns the value of the <code>aggregation_target_paths</code> attribute.
     *
     * @return the value of the the <code>aggregation_target_paths</code> attribute.
     */
    public String getAggregation_target_paths();

    /**
     * Sets the value of the <code>aggregation_target_paths</code> attribute.
     *
     * @param value the value of the <code>aggregation_target_paths</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregation_target_paths(String value);   
   
	/**
	 * Checks if the value of the <code>aggregation_target_paths</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregation_target_paths</code> attribute is defined.
	 */
    public boolean isAggregation_target_pathsDefined();
 
    /**
     * Returns the value of the <code>aggregation_update_action</code> attribute.
     *
     * @return the value of the the <code>aggregation_update_action</code> attribute.
     */
    public String getAggregation_update_action();

    /**
     * Sets the value of the <code>aggregation_update_action</code> attribute.
     *
     * @param value the value of the <code>aggregation_update_action</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAggregation_update_action(String value);   
   
	/**
	 * Checks if the value of the <code>aggregation_update_action</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>aggregation_update_action</code> attribute is defined.
	 */
    public boolean isAggregation_update_actionDefined();
	
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
     * Returns the value of the <code>index_description</code> attribute.
     *
     * @return the value of the the <code>index_description</code> attribute.
     */
    public String getIndex_description();

    /**
     * Sets the value of the <code>index_description</code> attribute.
     *
     * @param value the value of the <code>index_description</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setIndex_description(String value);   
   
	/**
	 * Checks if the value of the <code>index_description</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>index_description</code> attribute is defined.
	 */
    public boolean isIndex_descriptionDefined();
 
    /**
     * Returns the value of the <code>index_title</code> attribute.
     *
     * @return the value of the the <code>index_title</code> attribute.
     */
    public String getIndex_title();

    /**
     * Sets the value of the <code>index_title</code> attribute.
     *
     * @param value the value of the <code>index_title</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setIndex_title(String value);   
   
	/**
	 * Checks if the value of the <code>index_title</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>index_title</code> attribute is defined.
	 */
    public boolean isIndex_titleDefined();
 
    /**
     * Returns the value of the <code>indexable_fields</code> attribute.
     *
     * @return the value of the the <code>indexable_fields</code> attribute.
     */
    public String getIndexable_fields();

    /**
     * Sets the value of the <code>indexable_fields</code> attribute.
     *
     * @param value the value of the <code>indexable_fields</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setIndexable_fields(String value);   
   
	/**
	 * Checks if the value of the <code>indexable_fields</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>indexable_fields</code> attribute is defined.
	 */
    public boolean isIndexable_fieldsDefined();
 
    /**
     * Returns the value of the <code>related_quick_add_view</code> attribute.
     *
     * @return the value of the the <code>related_quick_add_view</code> attribute.
     */
    public String getRelated_quick_add_view();

    /**
     * Sets the value of the <code>related_quick_add_view</code> attribute.
     *
     * @param value the value of the <code>related_quick_add_view</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRelated_quick_add_view(String value);   
   
	/**
	 * Checks if the value of the <code>related_quick_add_view</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>related_quick_add_view</code> attribute is defined.
	 */
    public boolean isRelated_quick_add_viewDefined();
	
    /**
     * Returns the value of the <code>related_supported</code> attribute.
     *
     * @return the value of the the <code>related_supported</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getRelated_supported()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>related_supported</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>related_supported</code> attribute.
     */
    public boolean getRelated_supported(boolean defaultValue);

    /**
     * Sets the value of the <code>related_supported</code> attribute.
     *
     * @param value the value of the <code>related_supported</code> attribute.
     */
    public void setRelated_supported(boolean value);

	/**
     * Removes the value of the <code>related_supported</code> attribute.
     */
    public void unsetRelated_supported();
   
	/**
	 * Checks if the value of the <code>related_supported</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>related_supported</code> attribute is defined.
	 */
    public boolean isRelated_supportedDefined();
 
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
