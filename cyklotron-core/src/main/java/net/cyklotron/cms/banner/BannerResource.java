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

import org.objectledge.coral.datatypes.Node;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.ProtectedResource;
import net.cyklotron.cms.workflow.StatefulResource;

/**
 * Defines the accessor methods of <code>cms.banner.banner</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface BannerResource
    extends Resource, Node, ProtectedResource, StatefulResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "cms.banner.banner";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>alt_text</code> attribute.
     *
     * @return the value of the the <code>alt_text</code> attribute.
     */
    public String getAlt_text();

    /**
     * Sets the value of the <code>alt_text</code> attribute.
     *
     * @param value the value of the <code>alt_text</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAlt_text(String value);   
   
	/**
	 * Checks if the value of the <code>alt_text</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>alt_text</code> attribute is defined.
	 */
    public boolean isAlt_textDefined();
 
    /**
     * Returns the value of the <code>end_date</code> attribute.
     *
     * @return the value of the the <code>end_date</code> attribute.
     */
    public Date getEnd_date();

    /**
     * Sets the value of the <code>end_date</code> attribute.
     *
     * @param value the value of the <code>end_date</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEnd_date(Date value);   
   
	/**
	 * Checks if the value of the <code>end_date</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>end_date</code> attribute is defined.
	 */
    public boolean isEnd_dateDefined();
	
    /**
     * Returns the value of the <code>exposition_counter</code> attribute.
     *
     * @return the value of the the <code>exposition_counter</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getExposition_counter()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>exposition_counter</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>exposition_counter</code> attribute.
     */
    public int getExposition_counter(int defaultValue);

    /**
     * Sets the value of the <code>exposition_counter</code> attribute.
     *
     * @param value the value of the <code>exposition_counter</code> attribute.
     */
    public void setExposition_counter(int value);

	/**
     * Removes the value of the <code>exposition_counter</code> attribute.
     */
    public void unsetExposition_counter();
   
	/**
	 * Checks if the value of the <code>exposition_counter</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>exposition_counter</code> attribute is defined.
	 */
    public boolean isExposition_counterDefined();
	
    /**
     * Returns the value of the <code>followed_counter</code> attribute.
     *
     * @return the value of the the <code>followed_counter</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getFollowed_counter()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>followed_counter</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>followed_counter</code> attribute.
     */
    public int getFollowed_counter(int defaultValue);

    /**
     * Sets the value of the <code>followed_counter</code> attribute.
     *
     * @param value the value of the <code>followed_counter</code> attribute.
     */
    public void setFollowed_counter(int value);

	/**
     * Removes the value of the <code>followed_counter</code> attribute.
     */
    public void unsetFollowed_counter();
   
	/**
	 * Checks if the value of the <code>followed_counter</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>followed_counter</code> attribute is defined.
	 */
    public boolean isFollowed_counterDefined();
 
    /**
     * Returns the value of the <code>start_date</code> attribute.
     *
     * @return the value of the the <code>start_date</code> attribute.
     */
    public Date getStart_date();

    /**
     * Sets the value of the <code>start_date</code> attribute.
     *
     * @param value the value of the <code>start_date</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setStart_date(Date value);   
   
	/**
	 * Checks if the value of the <code>start_date</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>start_date</code> attribute is defined.
	 */
    public boolean isStart_dateDefined();
 
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
