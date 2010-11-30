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

import org.objectledge.coral.store.Resource;

/**
 * Defines the accessor methods of <code>cms.periodicals.email_periodical</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface EmailPeriodicalResource
    extends Resource, PeriodicalResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the Coral resource class. */    
    public static final String CLASS_NAME = "cms.periodicals.email_periodical";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>addresses</code> attribute.
     *
     * @return the value of the the <code>addresses</code> attribute.
     */
    public String getAddresses();

    /**
     * Sets the value of the <code>addresses</code> attribute.
     *
     * @param value the value of the <code>addresses</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAddresses(String value);   
   
	/**
	 * Checks if the value of the <code>addresses</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>addresses</code> attribute is defined.
	 */
    public boolean isAddressesDefined();
 
    /**
     * Returns the value of the <code>fromHeader</code> attribute.
     *
     * @return the value of the the <code>fromHeader</code> attribute.
     */
    public String getFromHeader();

    /**
     * Sets the value of the <code>fromHeader</code> attribute.
     *
     * @param value the value of the <code>fromHeader</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setFromHeader(String value);   
   
	/**
	 * Checks if the value of the <code>fromHeader</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>fromHeader</code> attribute is defined.
	 */
    public boolean isFromHeaderDefined();
	
    /**
     * Returns the value of the <code>fullContent</code> attribute.
     *
     * @return the value of the the <code>fullContent</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getFullContent()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>fullContent</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>fullContent</code> attribute.
     */
    public boolean getFullContent(boolean defaultValue);

    /**
     * Sets the value of the <code>fullContent</code> attribute.
     *
     * @param value the value of the <code>fullContent</code> attribute.
     */
    public void setFullContent(boolean value);

	/**
     * Removes the value of the <code>fullContent</code> attribute.
     */
    public void unsetFullContent();
   
	/**
	 * Checks if the value of the <code>fullContent</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>fullContent</code> attribute is defined.
	 */
    public boolean isFullContentDefined();
 
    /**
     * Returns the value of the <code>notificationRenderer</code> attribute.
     *
     * @return the value of the the <code>notificationRenderer</code> attribute.
     */
    public String getNotificationRenderer();

    /**
     * Sets the value of the <code>notificationRenderer</code> attribute.
     *
     * @param value the value of the <code>notificationRenderer</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setNotificationRenderer(String value);   
   
	/**
	 * Checks if the value of the <code>notificationRenderer</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>notificationRenderer</code> attribute is defined.
	 */
    public boolean isNotificationRendererDefined();
 
    /**
     * Returns the value of the <code>notificationTemplate</code> attribute.
     *
     * @return the value of the the <code>notificationTemplate</code> attribute.
     */
    public String getNotificationTemplate();

    /**
     * Sets the value of the <code>notificationTemplate</code> attribute.
     *
     * @param value the value of the <code>notificationTemplate</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setNotificationTemplate(String value);   
   
	/**
	 * Checks if the value of the <code>notificationTemplate</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>notificationTemplate</code> attribute is defined.
	 */
    public boolean isNotificationTemplateDefined();
 
    /**
     * Returns the value of the <code>replyToHeader</code> attribute.
     *
     * @return the value of the the <code>replyToHeader</code> attribute.
     */
    public String getReplyToHeader();

    /**
     * Sets the value of the <code>replyToHeader</code> attribute.
     *
     * @param value the value of the <code>replyToHeader</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setReplyToHeader(String value);   
   
	/**
	 * Checks if the value of the <code>replyToHeader</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>replyToHeader</code> attribute is defined.
	 */
    public boolean isReplyToHeaderDefined();
	
    /**
     * Returns the value of the <code>sendEmpty</code> attribute.
     *
     * @return the value of the the <code>sendEmpty</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getSendEmpty()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>sendEmpty</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>sendEmpty</code> attribute.
     */
    public boolean getSendEmpty(boolean defaultValue);

    /**
     * Sets the value of the <code>sendEmpty</code> attribute.
     *
     * @param value the value of the <code>sendEmpty</code> attribute.
     */
    public void setSendEmpty(boolean value);

	/**
     * Removes the value of the <code>sendEmpty</code> attribute.
     */
    public void unsetSendEmpty();
   
	/**
	 * Checks if the value of the <code>sendEmpty</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>sendEmpty</code> attribute is defined.
	 */
    public boolean isSendEmptyDefined();
 
    /**
     * Returns the value of the <code>subject</code> attribute.
     *
     * @return the value of the the <code>subject</code> attribute.
     */
    public String getSubject();

    /**
     * Sets the value of the <code>subject</code> attribute.
     *
     * @param value the value of the <code>subject</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSubject(String value);   
   
	/**
	 * Checks if the value of the <code>subject</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>subject</code> attribute is defined.
	 */
    public boolean isSubjectDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
}
