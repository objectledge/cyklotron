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

import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.search.IndexableResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

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
     * Returns the value of the <code>event_end</code> attribute.
     *
     * @return the value of the the <code>event_end</code> attribute.
     */
    public Date getEvent_end();

    /**
     * Sets the value of the <code>event_end</code> attribute.
     *
     * @param value the value of the <code>event_end</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEvent_end(Date value);   
   
	/**
	 * Checks if the value of the <code>event_end</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>event_end</code> attribute is defined.
	 */
    public boolean isEvent_endDefined();
 
    /**
     * Returns the value of the <code>event_place</code> attribute.
     *
     * @return the value of the the <code>event_place</code> attribute.
     */
    public String getEvent_place();

    /**
     * Sets the value of the <code>event_place</code> attribute.
     *
     * @param value the value of the <code>event_place</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEvent_place(String value);   
   
	/**
	 * Checks if the value of the <code>event_place</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>event_place</code> attribute is defined.
	 */
    public boolean isEvent_placeDefined();
 
    /**
     * Returns the value of the <code>event_start</code> attribute.
     *
     * @return the value of the the <code>event_start</code> attribute.
     */
    public Date getEvent_start();

    /**
     * Sets the value of the <code>event_start</code> attribute.
     *
     * @param value the value of the <code>event_start</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEvent_start(Date value);   
   
	/**
	 * Checks if the value of the <code>event_start</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>event_start</code> attribute is defined.
	 */
    public boolean isEvent_startDefined();
 
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
     * Returns the value of the <code>sub_title</code> attribute.
     *
     * @return the value of the the <code>sub_title</code> attribute.
     */
    public String getSub_title();

    /**
     * Sets the value of the <code>sub_title</code> attribute.
     *
     * @param value the value of the <code>sub_title</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSub_title(String value);   
   
	/**
	 * Checks if the value of the <code>sub_title</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>sub_title</code> attribute is defined.
	 */
    public boolean isSub_titleDefined();
 
    /**
     * Returns the value of the <code>title_calendar</code> attribute.
     *
     * @return the value of the the <code>title_calendar</code> attribute.
     */
    public String getTitle_calendar();

    /**
     * Sets the value of the <code>title_calendar</code> attribute.
     *
     * @param value the value of the <code>title_calendar</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTitle_calendar(String value);   
   
	/**
	 * Checks if the value of the <code>title_calendar</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>title_calendar</code> attribute is defined.
	 */
    public boolean isTitle_calendarDefined();
  
    // @custom methods ///////////////////////////////////////////////////////

    // @import net.labeo.webcore.RunData
    // @import net.labeo.webcore.ProcessingException
    // @order title, site, preferences
    
    public DocumentTool getDocumentTool(RunData data) throws ProcessingException;
    public void clearCache();

	public DocumentTool getDocumentTool(
		LinkRenderer linkRenderer, HTMLContentFilter filter, String characterEncoding)
	throws ProcessingException;
    
    public static final String EMPTY_TITLE = "1cmsdocumenttitlecalendarempty1";
}
