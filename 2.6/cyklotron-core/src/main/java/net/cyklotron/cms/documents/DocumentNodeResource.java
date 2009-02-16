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
 
package net.cyklotron.cms.documents;

import java.util.Date;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.search.IndexableResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * Defines the accessor methods of <code>documents.document_node</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface DocumentNodeResource
    extends Resource, IndexableResource, NavigationNodeResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "documents.document_node";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>abstract</code> attribute.
     *
     * @return the value of the the <code>abstract</code> attribute.
     */
    public String getAbstract();

    /**
     * Sets the value of the <code>abstract</code> attribute.
     *
     * @param value the value of the <code>abstract</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAbstract(String value);   
   
	/**
	 * Checks if the value of the <code>abstract</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>abstract</code> attribute is defined.
	 */
    public boolean isAbstractDefined();
 
    /**
     * Returns the value of the <code>content</code> attribute.
     *
     * @return the value of the the <code>content</code> attribute.
     */
    public String getContent();

    /**
     * Sets the value of the <code>content</code> attribute.
     *
     * @param value the value of the <code>content</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setContent(String value);   
   
	/**
	 * Checks if the value of the <code>content</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>content</code> attribute is defined.
	 */
    public boolean isContentDefined();
 
    /**
     * Returns the value of the <code>eventEnd</code> attribute.
     *
     * @return the value of the the <code>eventEnd</code> attribute.
     */
    public Date getEventEnd();

    /**
     * Sets the value of the <code>eventEnd</code> attribute.
     *
     * @param value the value of the <code>eventEnd</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEventEnd(Date value);   
   
	/**
	 * Checks if the value of the <code>eventEnd</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>eventEnd</code> attribute is defined.
	 */
    public boolean isEventEndDefined();
 
    /**
     * Returns the value of the <code>eventPlace</code> attribute.
     *
     * @return the value of the the <code>eventPlace</code> attribute.
     */
    public String getEventPlace();

    /**
     * Sets the value of the <code>eventPlace</code> attribute.
     *
     * @param value the value of the <code>eventPlace</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEventPlace(String value);   
   
	/**
	 * Checks if the value of the <code>eventPlace</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>eventPlace</code> attribute is defined.
	 */
    public boolean isEventPlaceDefined();
 
    /**
     * Returns the value of the <code>eventStart</code> attribute.
     *
     * @return the value of the the <code>eventStart</code> attribute.
     */
    public Date getEventStart();

    /**
     * Sets the value of the <code>eventStart</code> attribute.
     *
     * @param value the value of the <code>eventStart</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEventStart(Date value);   
   
	/**
	 * Checks if the value of the <code>eventStart</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>eventStart</code> attribute is defined.
	 */
    public boolean isEventStartDefined();
 
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
     * Returns the value of the <code>keywords</code> attribute.
     *
     * @return the value of the the <code>keywords</code> attribute.
     */
    public String getKeywords();

    /**
     * Sets the value of the <code>keywords</code> attribute.
     *
     * @param value the value of the <code>keywords</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setKeywords(String value);   
   
	/**
	 * Checks if the value of the <code>keywords</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>keywords</code> attribute is defined.
	 */
    public boolean isKeywordsDefined();
 
    /**
     * Returns the value of the <code>lang</code> attribute.
     *
     * @return the value of the the <code>lang</code> attribute.
     */
    public String getLang();

    /**
     * Sets the value of the <code>lang</code> attribute.
     *
     * @param value the value of the <code>lang</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLang(String value);   
   
	/**
	 * Checks if the value of the <code>lang</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lang</code> attribute is defined.
	 */
    public boolean isLangDefined();
 
    /**
     * Returns the value of the <code>meta</code> attribute.
     *
     * @return the value of the the <code>meta</code> attribute.
     */
    public String getMeta();

    /**
     * Sets the value of the <code>meta</code> attribute.
     *
     * @param value the value of the <code>meta</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setMeta(String value);   
   
	/**
	 * Checks if the value of the <code>meta</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>meta</code> attribute is defined.
	 */
    public boolean isMetaDefined();
 
    /**
     * Returns the value of the <code>subTitle</code> attribute.
     *
     * @return the value of the the <code>subTitle</code> attribute.
     */
    public String getSubTitle();

    /**
     * Sets the value of the <code>subTitle</code> attribute.
     *
     * @param value the value of the <code>subTitle</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSubTitle(String value);   
   
	/**
	 * Checks if the value of the <code>subTitle</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>subTitle</code> attribute is defined.
	 */
    public boolean isSubTitleDefined();
 
    /**
     * Returns the value of the <code>titleCalendar</code> attribute.
     *
     * @return the value of the the <code>titleCalendar</code> attribute.
     */
    public String getTitleCalendar();

    /**
     * Sets the value of the <code>titleCalendar</code> attribute.
     *
     * @param value the value of the <code>titleCalendar</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTitleCalendar(String value);   
   
	/**
	 * Checks if the value of the <code>titleCalendar</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>titleCalendar</code> attribute is defined.
	 */
    public boolean isTitleCalendarDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
    // @import org.objectledge.context.Context
    // @import org.objectledge.pipeline.ProcessingException
    // @import org.objectledge.coral.session.CoralSession
    
    // @order title, site, preferences
    
    public DocumentTool getDocumentTool(Context context) 
        throws ProcessingException;
    public void clearCache();

	public DocumentTool getDocumentTool(CoralSession coralSession,
		LinkRenderer linkRenderer, HTMLContentFilter filter, String characterEncoding)
	    throws ProcessingException;
    
    public static final String EMPTY_TITLE = "1cmsdocumenttitlecalendarempty1";
}
