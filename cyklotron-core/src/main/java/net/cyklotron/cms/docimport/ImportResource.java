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
 
package net.cyklotron.cms.docimport;

import java.util.Date;

import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.CmsNodeResource;

/**
 * Defines the accessor methods of <code>docimport.import</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface ImportResource
    extends Resource, CmsNodeResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the Coral resource class. */    
    public static final String CLASS_NAME = "docimport.import";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>abstractCleanupProfile</code> attribute.
     *
     * @return the value of the the <code>abstractCleanupProfile</code> attribute.
     */
    public String getAbstractCleanupProfile();

    /**
     * Sets the value of the <code>abstractCleanupProfile</code> attribute.
     *
     * @param value the value of the <code>abstractCleanupProfile</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAbstractCleanupProfile(String value);   
   
	/**
	 * Checks if the value of the <code>abstractCleanupProfile</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>abstractCleanupProfile</code> attribute is defined.
	 */
    public boolean isAbstractCleanupProfileDefined();
	
    /**
     * Returns the value of the <code>abstractEntityEncoded</code> attribute.
     *
     * @return the value of the the <code>abstractEntityEncoded</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getAbstractEntityEncoded()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>abstractEntityEncoded</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>abstractEntityEncoded</code> attribute.
     */
    public boolean getAbstractEntityEncoded(boolean defaultValue);

    /**
     * Sets the value of the <code>abstractEntityEncoded</code> attribute.
     *
     * @param value the value of the <code>abstractEntityEncoded</code> attribute.
     */
    public void setAbstractEntityEncoded(boolean value);

	/**
     * Removes the value of the <code>abstractEntityEncoded</code> attribute.
     */
    public void unsetAbstractEntityEncoded();
   
	/**
	 * Checks if the value of the <code>abstractEntityEncoded</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>abstractEntityEncoded</code> attribute is defined.
	 */
    public boolean isAbstractEntityEncodedDefined();
 
    /**
     * Returns the value of the <code>abstractXPath</code> attribute.
     *
     * @return the value of the the <code>abstractXPath</code> attribute.
     */
    public String getAbstractXPath();

    /**
     * Sets the value of the <code>abstractXPath</code> attribute.
     *
     * @param value the value of the <code>abstractXPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAbstractXPath(String value);   
   
	/**
	 * Checks if the value of the <code>abstractXPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>abstractXPath</code> attribute is defined.
	 */
    public boolean isAbstractXPathDefined();
 
    /**
     * Returns the value of the <code>attachentURLXPath</code> attribute.
     *
     * @return the value of the the <code>attachentURLXPath</code> attribute.
     */
    public String getAttachentURLXPath();

    /**
     * Sets the value of the <code>attachentURLXPath</code> attribute.
     *
     * @param value the value of the <code>attachentURLXPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAttachentURLXPath(String value);   
   
	/**
	 * Checks if the value of the <code>attachentURLXPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>attachentURLXPath</code> attribute is defined.
	 */
    public boolean isAttachentURLXPathDefined();
	
    /**
     * Returns the value of the <code>attachmentURLComposite</code> attribute.
     *
     * @return the value of the the <code>attachmentURLComposite</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getAttachmentURLComposite()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>attachmentURLComposite</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>attachmentURLComposite</code> attribute.
     */
    public boolean getAttachmentURLComposite(boolean defaultValue);

    /**
     * Sets the value of the <code>attachmentURLComposite</code> attribute.
     *
     * @param value the value of the <code>attachmentURLComposite</code> attribute.
     */
    public void setAttachmentURLComposite(boolean value);

	/**
     * Removes the value of the <code>attachmentURLComposite</code> attribute.
     */
    public void unsetAttachmentURLComposite();
   
	/**
	 * Checks if the value of the <code>attachmentURLComposite</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>attachmentURLComposite</code> attribute is defined.
	 */
    public boolean isAttachmentURLCompositeDefined();
 
    /**
     * Returns the value of the <code>attachmentURLSeparator</code> attribute.
     *
     * @return the value of the the <code>attachmentURLSeparator</code> attribute.
     */
    public String getAttachmentURLSeparator();

    /**
     * Sets the value of the <code>attachmentURLSeparator</code> attribute.
     *
     * @param value the value of the <code>attachmentURLSeparator</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAttachmentURLSeparator(String value);   
   
	/**
	 * Checks if the value of the <code>attachmentURLSeparator</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>attachmentURLSeparator</code> attribute is defined.
	 */
    public boolean isAttachmentURLSeparatorDefined();
 
    /**
     * Returns the value of the <code>attachmentsLocation</code> attribute.
     *
     * @return the value of the the <code>attachmentsLocation</code> attribute.
     */
    public Resource getAttachmentsLocation();
 
    /**
     * Sets the value of the <code>attachmentsLocation</code> attribute.
     *
     * @param value the value of the <code>attachmentsLocation</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setAttachmentsLocation(Resource value)
        throws ValueRequiredException;
    
    /**
     * Returns the value of the <code>calendarStructureType</code> attribute.
     *
     * @return the value of the the <code>calendarStructureType</code> attribute.
     */
    public String getCalendarStructureType();

    /**
     * Sets the value of the <code>calendarStructureType</code> attribute.
     *
     * @param value the value of the <code>calendarStructureType</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCalendarStructureType(String value);   
   
	/**
	 * Checks if the value of the <code>calendarStructureType</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>calendarStructureType</code> attribute is defined.
	 */
    public boolean isCalendarStructureTypeDefined();
 
    /**
     * Returns the value of the <code>categories</code> attribute.
     *
     * @return the value of the the <code>categories</code> attribute.
     */
    public ResourceList getCategories();

    /**
     * Sets the value of the <code>categories</code> attribute.
     *
     * @param value the value of the <code>categories</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCategories(ResourceList value);   
   
	/**
	 * Checks if the value of the <code>categories</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>categories</code> attribute is defined.
	 */
    public boolean isCategoriesDefined();
 
    /**
     * Returns the value of the <code>contentCleanupProfile</code> attribute.
     *
     * @return the value of the the <code>contentCleanupProfile</code> attribute.
     */
    public String getContentCleanupProfile();

    /**
     * Sets the value of the <code>contentCleanupProfile</code> attribute.
     *
     * @param value the value of the <code>contentCleanupProfile</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setContentCleanupProfile(String value);   
   
	/**
	 * Checks if the value of the <code>contentCleanupProfile</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>contentCleanupProfile</code> attribute is defined.
	 */
    public boolean isContentCleanupProfileDefined();
	
    /**
     * Returns the value of the <code>contentEntitytEncoded</code> attribute.
     *
     * @return the value of the the <code>contentEntitytEncoded</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getContentEntitytEncoded()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>contentEntitytEncoded</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>contentEntitytEncoded</code> attribute.
     */
    public boolean getContentEntitytEncoded(boolean defaultValue);

    /**
     * Sets the value of the <code>contentEntitytEncoded</code> attribute.
     *
     * @param value the value of the <code>contentEntitytEncoded</code> attribute.
     */
    public void setContentEntitytEncoded(boolean value);

	/**
     * Removes the value of the <code>contentEntitytEncoded</code> attribute.
     */
    public void unsetContentEntitytEncoded();
   
	/**
	 * Checks if the value of the <code>contentEntitytEncoded</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>contentEntitytEncoded</code> attribute is defined.
	 */
    public boolean isContentEntitytEncodedDefined();
 
    /**
     * Returns the value of the <code>contentXPath</code> attribute.
     *
     * @return the value of the the <code>contentXPath</code> attribute.
     */
    public String getContentXPath();

    /**
     * Sets the value of the <code>contentXPath</code> attribute.
     *
     * @param value the value of the <code>contentXPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setContentXPath(String value);   
   
	/**
	 * Checks if the value of the <code>contentXPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>contentXPath</code> attribute is defined.
	 */
    public boolean isContentXPathDefined();
 
    /**
     * Returns the value of the <code>creationDateXPath</code> attribute.
     *
     * @return the value of the the <code>creationDateXPath</code> attribute.
     */
    public String getCreationDateXPath();

    /**
     * Sets the value of the <code>creationDateXPath</code> attribute.
     *
     * @param value the value of the <code>creationDateXPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setCreationDateXPath(String value);   
   
	/**
	 * Checks if the value of the <code>creationDateXPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>creationDateXPath</code> attribute is defined.
	 */
    public boolean isCreationDateXPathDefined();
 
    /**
     * Returns the value of the <code>dateFormat</code> attribute.
     *
     * @return the value of the the <code>dateFormat</code> attribute.
     */
    public String getDateFormat();

    /**
     * Sets the value of the <code>dateFormat</code> attribute.
     *
     * @param value the value of the <code>dateFormat</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setDateFormat(String value);   
   
	/**
	 * Checks if the value of the <code>dateFormat</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>dateFormat</code> attribute is defined.
	 */
    public boolean isDateFormatDefined();
 
    /**
     * Returns the value of the <code>dateRangeEndParameter</code> attribute.
     *
     * @return the value of the the <code>dateRangeEndParameter</code> attribute.
     */
    public String getDateRangeEndParameter();

    /**
     * Sets the value of the <code>dateRangeEndParameter</code> attribute.
     *
     * @param value the value of the <code>dateRangeEndParameter</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setDateRangeEndParameter(String value);   
   
	/**
	 * Checks if the value of the <code>dateRangeEndParameter</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>dateRangeEndParameter</code> attribute is defined.
	 */
    public boolean isDateRangeEndParameterDefined();
 
    /**
     * Returns the value of the <code>dateRangeStartParameter</code> attribute.
     *
     * @return the value of the the <code>dateRangeStartParameter</code> attribute.
     */
    public String getDateRangeStartParameter();

    /**
     * Sets the value of the <code>dateRangeStartParameter</code> attribute.
     *
     * @param value the value of the <code>dateRangeStartParameter</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setDateRangeStartParameter(String value);   
   
	/**
	 * Checks if the value of the <code>dateRangeStartParameter</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>dateRangeStartParameter</code> attribute is defined.
	 */
    public boolean isDateRangeStartParameterDefined();
 
    /**
     * Returns the value of the <code>documentXPath</code> attribute.
     *
     * @return the value of the the <code>documentXPath</code> attribute.
     */
    public String getDocumentXPath();

    /**
     * Sets the value of the <code>documentXPath</code> attribute.
     *
     * @param value the value of the <code>documentXPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setDocumentXPath(String value);   
   
	/**
	 * Checks if the value of the <code>documentXPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>documentXPath</code> attribute is defined.
	 */
    public boolean isDocumentXPathDefined();
 
    /**
     * Returns the value of the <code>footer</code> attribute.
     *
     * @return the value of the the <code>footer</code> attribute.
     */
    public String getFooter();

    /**
     * Sets the value of the <code>footer</code> attribute.
     *
     * @param value the value of the <code>footer</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setFooter(String value);   
   
	/**
	 * Checks if the value of the <code>footer</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>footer</code> attribute is defined.
	 */
    public boolean isFooterDefined();
 
    /**
     * Returns the value of the <code>lastNewDocumentsCheck</code> attribute.
     *
     * @return the value of the the <code>lastNewDocumentsCheck</code> attribute.
     */
    public Date getLastNewDocumentsCheck();

    /**
     * Sets the value of the <code>lastNewDocumentsCheck</code> attribute.
     *
     * @param value the value of the <code>lastNewDocumentsCheck</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLastNewDocumentsCheck(Date value);   
   
	/**
	 * Checks if the value of the <code>lastNewDocumentsCheck</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lastNewDocumentsCheck</code> attribute is defined.
	 */
    public boolean isLastNewDocumentsCheckDefined();
 
    /**
     * Returns the value of the <code>lastUpdatedDocumentsCheck</code> attribute.
     *
     * @return the value of the the <code>lastUpdatedDocumentsCheck</code> attribute.
     */
    public Date getLastUpdatedDocumentsCheck();

    /**
     * Sets the value of the <code>lastUpdatedDocumentsCheck</code> attribute.
     *
     * @param value the value of the <code>lastUpdatedDocumentsCheck</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLastUpdatedDocumentsCheck(Date value);   
   
	/**
	 * Checks if the value of the <code>lastUpdatedDocumentsCheck</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lastUpdatedDocumentsCheck</code> attribute is defined.
	 */
    public boolean isLastUpdatedDocumentsCheckDefined();
 
    /**
     * Returns the value of the <code>location</code> attribute.
     *
     * @return the value of the the <code>location</code> attribute.
     */
    public String getLocation();
 
    /**
     * Sets the value of the <code>location</code> attribute.
     *
     * @param value the value of the <code>location</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setLocation(String value)
        throws ValueRequiredException;
    
    /**
     * Returns the value of the <code>modificationDateXPath</code> attribute.
     *
     * @return the value of the the <code>modificationDateXPath</code> attribute.
     */
    public String getModificationDateXPath();

    /**
     * Sets the value of the <code>modificationDateXPath</code> attribute.
     *
     * @param value the value of the <code>modificationDateXPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setModificationDateXPath(String value);   
   
	/**
	 * Checks if the value of the <code>modificationDateXPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>modificationDateXPath</code> attribute is defined.
	 */
    public boolean isModificationDateXPathDefined();
 
    /**
     * Returns the value of the <code>originalURLXPath</code> attribute.
     *
     * @return the value of the the <code>originalURLXPath</code> attribute.
     */
    public String getOriginalURLXPath();

    /**
     * Sets the value of the <code>originalURLXPath</code> attribute.
     *
     * @param value the value of the <code>originalURLXPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setOriginalURLXPath(String value);   
   
	/**
	 * Checks if the value of the <code>originalURLXPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>originalURLXPath</code> attribute is defined.
	 */
    public boolean isOriginalURLXPathDefined();
 
    /**
     * Returns the value of the <code>ownerLogin</code> attribute.
     *
     * @return the value of the the <code>ownerLogin</code> attribute.
     */
    public String getOwnerLogin();
 
    /**
     * Sets the value of the <code>ownerLogin</code> attribute.
     *
     * @param value the value of the <code>ownerLogin</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setOwnerLogin(String value)
        throws ValueRequiredException;
    
    /**
     * Returns the value of the <code>sourceName</code> attribute.
     *
     * @return the value of the the <code>sourceName</code> attribute.
     */
    public String getSourceName();
 
    /**
     * Sets the value of the <code>sourceName</code> attribute.
     *
     * @param value the value of the <code>sourceName</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setSourceName(String value)
        throws ValueRequiredException;
    
    /**
     * Returns the value of the <code>targetLocation</code> attribute.
     *
     * @return the value of the the <code>targetLocation</code> attribute.
     */
    public Resource getTargetLocation();
 
    /**
     * Sets the value of the <code>targetLocation</code> attribute.
     *
     * @param value the value of the <code>targetLocation</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setTargetLocation(Resource value)
        throws ValueRequiredException;
    
    /**
     * Returns the value of the <code>titleCleanupProfile</code> attribute.
     *
     * @return the value of the the <code>titleCleanupProfile</code> attribute.
     */
    public String getTitleCleanupProfile();

    /**
     * Sets the value of the <code>titleCleanupProfile</code> attribute.
     *
     * @param value the value of the <code>titleCleanupProfile</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTitleCleanupProfile(String value);   
   
	/**
	 * Checks if the value of the <code>titleCleanupProfile</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>titleCleanupProfile</code> attribute is defined.
	 */
    public boolean isTitleCleanupProfileDefined();
	
    /**
     * Returns the value of the <code>titleEntityEncoded</code> attribute.
     *
     * @return the value of the the <code>titleEntityEncoded</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getTitleEntityEncoded()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>titleEntityEncoded</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>titleEntityEncoded</code> attribute.
     */
    public boolean getTitleEntityEncoded(boolean defaultValue);

    /**
     * Sets the value of the <code>titleEntityEncoded</code> attribute.
     *
     * @param value the value of the <code>titleEntityEncoded</code> attribute.
     */
    public void setTitleEntityEncoded(boolean value);

	/**
     * Removes the value of the <code>titleEntityEncoded</code> attribute.
     */
    public void unsetTitleEntityEncoded();
   
	/**
	 * Checks if the value of the <code>titleEntityEncoded</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>titleEntityEncoded</code> attribute is defined.
	 */
    public boolean isTitleEntityEncodedDefined();
 
    /**
     * Returns the value of the <code>titleXPath</code> attribute.
     *
     * @return the value of the the <code>titleXPath</code> attribute.
     */
    public String getTitleXPath();

    /**
     * Sets the value of the <code>titleXPath</code> attribute.
     *
     * @param value the value of the <code>titleXPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTitleXPath(String value);   
   
	/**
	 * Checks if the value of the <code>titleXPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>titleXPath</code> attribute is defined.
	 */
    public boolean isTitleXPathDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
}
