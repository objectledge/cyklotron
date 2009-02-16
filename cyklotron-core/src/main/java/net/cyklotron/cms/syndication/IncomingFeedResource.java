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

/**
 * Defines the accessor methods of <code>cms.syndication.incomingfeed</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface IncomingFeedResource
    extends Resource, CmsNodeResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "cms.syndication.incomingfeed";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>contents</code> attribute.
     *
     * @return the value of the the <code>contents</code> attribute.
     */
    public String getContents();

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
     * Returns the value of the <code>failedUpdates</code> attribute.
     *
     * @return the value of the the <code>failedUpdates</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getFailedUpdates()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>failedUpdates</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>failedUpdates</code> attribute.
     */
    public int getFailedUpdates(int defaultValue);

    /**
     * Sets the value of the <code>failedUpdates</code> attribute.
     *
     * @param value the value of the <code>failedUpdates</code> attribute.
     */
    public void setFailedUpdates(int value);

	/**
     * Removes the value of the <code>failedUpdates</code> attribute.
     */
    public void unsetFailedUpdates();
   
	/**
	 * Checks if the value of the <code>failedUpdates</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>failedUpdates</code> attribute is defined.
	 */
    public boolean isFailedUpdatesDefined();
	
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
     * Returns the value of the <code>lastUpdate</code> attribute.
     *
     * @return the value of the the <code>lastUpdate</code> attribute.
     */
    public Date getLastUpdate();

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
     * Returns the value of the <code>transformationTemplate</code> attribute.
     *
     * @return the value of the the <code>transformationTemplate</code> attribute.
     */
    public String getTransformationTemplate();

    /**
     * Sets the value of the <code>transformationTemplate</code> attribute.
     *
     * @param value the value of the <code>transformationTemplate</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTransformationTemplate(String value);   
   
	/**
	 * Checks if the value of the <code>transformationTemplate</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>transformationTemplate</code> attribute is defined.
	 */
    public boolean isTransformationTemplateDefined();
 
    /**
     * Returns the value of the <code>updateErrorKey</code> attribute.
     *
     * @return the value of the the <code>updateErrorKey</code> attribute.
     */
    public String getUpdateErrorKey();

    /**
     * Sets the value of the <code>updateErrorKey</code> attribute.
     *
     * @param value the value of the <code>updateErrorKey</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setUpdateErrorKey(String value);   
   
	/**
	 * Checks if the value of the <code>updateErrorKey</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>updateErrorKey</code> attribute is defined.
	 */
    public boolean isUpdateErrorKeyDefined();
 
    /**
     * Returns the value of the <code>url</code> attribute.
     *
     * @return the value of the the <code>url</code> attribute.
     */
    public String getUrl();

    /**
     * Sets the value of the <code>url</code> attribute.
     *
     * @param value the value of the <code>url</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setUrl(String value);   
   
	/**
	 * Checks if the value of the <code>url</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>url</code> attribute is defined.
	 */
    public boolean isUrlDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
}
