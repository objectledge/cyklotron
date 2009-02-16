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
 
package net.cyklotron.cms.banner;

import java.util.Date;

import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.CmsNodeResource;
import net.cyklotron.cms.ProtectedResource;
import net.cyklotron.cms.workflow.StatefulResource;

/**
 * Defines the accessor methods of <code>cms.banner.banner</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface BannerResource
    extends Resource, CmsNodeResource, ProtectedResource, StatefulResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "cms.banner.banner";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>altText</code> attribute.
     *
     * @return the value of the the <code>altText</code> attribute.
     */
    public String getAltText();

    /**
     * Sets the value of the <code>altText</code> attribute.
     *
     * @param value the value of the <code>altText</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAltText(String value);   
   
	/**
	 * Checks if the value of the <code>altText</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>altText</code> attribute is defined.
	 */
    public boolean isAltTextDefined();
 
    /**
     * Returns the value of the <code>endDate</code> attribute.
     *
     * @return the value of the the <code>endDate</code> attribute.
     */
    public Date getEndDate();

    /**
     * Sets the value of the <code>endDate</code> attribute.
     *
     * @param value the value of the <code>endDate</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEndDate(Date value);   
   
	/**
	 * Checks if the value of the <code>endDate</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>endDate</code> attribute is defined.
	 */
    public boolean isEndDateDefined();
	
    /**
     * Returns the value of the <code>expositionCounter</code> attribute.
     *
     * @return the value of the the <code>expositionCounter</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getExpositionCounter()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>expositionCounter</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>expositionCounter</code> attribute.
     */
    public int getExpositionCounter(int defaultValue);

    /**
     * Sets the value of the <code>expositionCounter</code> attribute.
     *
     * @param value the value of the <code>expositionCounter</code> attribute.
     */
    public void setExpositionCounter(int value);

	/**
     * Removes the value of the <code>expositionCounter</code> attribute.
     */
    public void unsetExpositionCounter();
   
	/**
	 * Checks if the value of the <code>expositionCounter</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>expositionCounter</code> attribute is defined.
	 */
    public boolean isExpositionCounterDefined();
	
    /**
     * Returns the value of the <code>followedCounter</code> attribute.
     *
     * @return the value of the the <code>followedCounter</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getFollowedCounter()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>followedCounter</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>followedCounter</code> attribute.
     */
    public int getFollowedCounter(int defaultValue);

    /**
     * Sets the value of the <code>followedCounter</code> attribute.
     *
     * @param value the value of the <code>followedCounter</code> attribute.
     */
    public void setFollowedCounter(int value);

	/**
     * Removes the value of the <code>followedCounter</code> attribute.
     */
    public void unsetFollowedCounter();
   
	/**
	 * Checks if the value of the <code>followedCounter</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>followedCounter</code> attribute is defined.
	 */
    public boolean isFollowedCounterDefined();
 
    /**
     * Returns the value of the <code>startDate</code> attribute.
     *
     * @return the value of the the <code>startDate</code> attribute.
     */
    public Date getStartDate();

    /**
     * Sets the value of the <code>startDate</code> attribute.
     *
     * @param value the value of the <code>startDate</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setStartDate(Date value);   
   
	/**
	 * Checks if the value of the <code>startDate</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>startDate</code> attribute is defined.
	 */
    public boolean isStartDateDefined();
 
    /**
     * Returns the value of the <code>target</code> attribute.
     *
     * @return the value of the the <code>target</code> attribute.
     */
    public String getTarget();

    /**
     * Sets the value of the <code>target</code> attribute.
     *
     * @param value the value of the <code>target</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTarget(String value);   
   
	/**
	 * Checks if the value of the <code>target</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>target</code> attribute is defined.
	 */
    public boolean isTargetDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
}
