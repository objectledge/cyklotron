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
 
package net.cyklotron.cms.category.query;

import org.objectledge.coral.datatypes.Node;
import org.objectledge.coral.store.Resource;

/**
 * Defines the accessor methods of <code>category.query</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface CategoryQueryResource
    extends Resource, Node
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "category.query";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>acceptedResourceClasses</code> attribute.
     *
     * @return the value of the the <code>acceptedResourceClasses</code> attribute.
     */
    public String getAcceptedResourceClasses();

    /**
     * Sets the value of the <code>acceptedResourceClasses</code> attribute.
     *
     * @param value the value of the <code>acceptedResourceClasses</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAcceptedResourceClasses(String value);   
   
	/**
	 * Checks if the value of the <code>acceptedResourceClasses</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>acceptedResourceClasses</code> attribute is defined.
	 */
    public boolean isAcceptedResourceClassesDefined();
 
    /**
     * Returns the value of the <code>acceptedSites</code> attribute.
     *
     * @return the value of the the <code>acceptedSites</code> attribute.
     */
    public String getAcceptedSites();

    /**
     * Sets the value of the <code>acceptedSites</code> attribute.
     *
     * @param value the value of the <code>acceptedSites</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAcceptedSites(String value);   
   
	/**
	 * Checks if the value of the <code>acceptedSites</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>acceptedSites</code> attribute is defined.
	 */
    public boolean isAcceptedSitesDefined();
 
    /**
     * Returns the value of the <code>optionalCategoryPaths</code> attribute.
     *
     * @return the value of the the <code>optionalCategoryPaths</code> attribute.
     */
    public String getOptionalCategoryPaths();

    /**
     * Sets the value of the <code>optionalCategoryPaths</code> attribute.
     *
     * @param value the value of the <code>optionalCategoryPaths</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setOptionalCategoryPaths(String value);   
   
	/**
	 * Checks if the value of the <code>optionalCategoryPaths</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>optionalCategoryPaths</code> attribute is defined.
	 */
    public boolean isOptionalCategoryPathsDefined();
 
    /**
     * Returns the value of the <code>query</code> attribute.
     *
     * @return the value of the the <code>query</code> attribute.
     */
    public String getQuery();

    /**
     * Sets the value of the <code>query</code> attribute.
     *
     * @param value the value of the <code>query</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setQuery(String value);   
   
	/**
	 * Checks if the value of the <code>query</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>query</code> attribute is defined.
	 */
    public boolean isQueryDefined();
 
    /**
     * Returns the value of the <code>requiredCategoryPaths</code> attribute.
     *
     * @return the value of the the <code>requiredCategoryPaths</code> attribute.
     */
    public String getRequiredCategoryPaths();

    /**
     * Sets the value of the <code>requiredCategoryPaths</code> attribute.
     *
     * @param value the value of the <code>requiredCategoryPaths</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRequiredCategoryPaths(String value);   
   
	/**
	 * Checks if the value of the <code>requiredCategoryPaths</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>requiredCategoryPaths</code> attribute is defined.
	 */
    public boolean isRequiredCategoryPathsDefined();
	
    /**
     * Returns the value of the <code>simpleQuery</code> attribute.
     *
     * @return the value of the the <code>simpleQuery</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getSimpleQuery()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>simpleQuery</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>simpleQuery</code> attribute.
     */
    public boolean getSimpleQuery(boolean defaultValue);

    /**
     * Sets the value of the <code>simpleQuery</code> attribute.
     *
     * @param value the value of the <code>simpleQuery</code> attribute.
     */
    public void setSimpleQuery(boolean value);

	/**
     * Removes the value of the <code>simpleQuery</code> attribute.
     */
    public void unsetSimpleQuery();
   
	/**
	 * Checks if the value of the <code>simpleQuery</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>simpleQuery</code> attribute is defined.
	 */
    public boolean isSimpleQueryDefined();
	
    /**
     * Returns the value of the <code>useIdsAsIdentifiers</code> attribute.
     *
     * @return the value of the the <code>useIdsAsIdentifiers</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getUseIdsAsIdentifiers()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>useIdsAsIdentifiers</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>useIdsAsIdentifiers</code> attribute.
     */
    public boolean getUseIdsAsIdentifiers(boolean defaultValue);

    /**
     * Sets the value of the <code>useIdsAsIdentifiers</code> attribute.
     *
     * @param value the value of the <code>useIdsAsIdentifiers</code> attribute.
     */
    public void setUseIdsAsIdentifiers(boolean value);

	/**
     * Removes the value of the <code>useIdsAsIdentifiers</code> attribute.
     */
    public void unsetUseIdsAsIdentifiers();
   
	/**
	 * Checks if the value of the <code>useIdsAsIdentifiers</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>useIdsAsIdentifiers</code> attribute is defined.
	 */
    public boolean isUseIdsAsIdentifiersDefined();
  
    // @custom methods ///////////////////////////////////////////////////////

    /**
     * Returns the value of the <code>acceptedResourceClasses</code> attribute as a String table.
     */
    public String[] getAcceptedResourceClassNames();

	/**
	 * Returns the value of the <code>acceptedSites</code> attribute as a String table.
	 */
	public String[] getAcceptedSiteNames();
	
	/**
	 * Sets the value of the <code>acceptedSites</code> attribute as a String table.
	 */
	public void setAcceptedSiteNames(String[] names);

    public boolean getUseIdsAsIdentifiers(boolean defaultValue);
    
    public boolean getSimpleQuery(boolean defaultValue);
}
