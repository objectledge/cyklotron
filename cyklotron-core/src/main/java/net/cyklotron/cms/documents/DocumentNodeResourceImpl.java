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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;
import org.objectledge.parameters.Parameters;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.search.SearchUtil;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.labeo.Labeo;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>documents.document_node</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class DocumentNodeResourceImpl
    extends NavigationNodeResourceImpl
    implements DocumentNodeResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>abstract</code> attribute. */
    private AttributeDefinition abstractDef;

    /** The AttributeDefinition object for the <code>content</code> attribute. */
    private AttributeDefinition contentDef;

    /** The AttributeDefinition object for the <code>event_end</code> attribute. */
    private AttributeDefinition event_endDef;

    /** The AttributeDefinition object for the <code>event_place</code> attribute. */
    private AttributeDefinition event_placeDef;

    /** The AttributeDefinition object for the <code>event_start</code> attribute. */
    private AttributeDefinition event_startDef;

    /** The AttributeDefinition object for the <code>footer</code> attribute. */
    private AttributeDefinition footerDef;

    /** The AttributeDefinition object for the <code>keywords</code> attribute. */
    private AttributeDefinition keywordsDef;

    /** The AttributeDefinition object for the <code>lang</code> attribute. */
    private AttributeDefinition langDef;

    /** The AttributeDefinition object for the <code>meta</code> attribute. */
    private AttributeDefinition metaDef;

    /** The AttributeDefinition object for the <code>sub_title</code> attribute. */
    private AttributeDefinition sub_titleDef;

    /** The AttributeDefinition object for the <code>title_calendar</code> attribute. */
    private AttributeDefinition title_calendarDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>documents.document_node</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public DocumentNodeResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("documents.document_node");
            abstractDef = rc.getAttribute("abstract");
            contentDef = rc.getAttribute("content");
            event_endDef = rc.getAttribute("event_end");
            event_placeDef = rc.getAttribute("event_place");
            event_startDef = rc.getAttribute("event_start");
            footerDef = rc.getAttribute("footer");
            keywordsDef = rc.getAttribute("keywords");
            langDef = rc.getAttribute("lang");
            metaDef = rc.getAttribute("meta");
            sub_titleDef = rc.getAttribute("sub_title");
            title_calendarDef = rc.getAttribute("title_calendar");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>documents.document_node</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static DocumentNodeResource getDocumentNodeResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof DocumentNodeResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not documents.document_node");
        }
        return (DocumentNodeResource)res;
    }

    /**
     * Creates a new <code>documents.document_node</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param title the title attribute
     * @param site the site attribute
     * @param preferences the preferences attribute
     * @return a new DocumentNodeResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     */
    public static DocumentNodeResource createDocumentNodeResource(CoralSession session, String
        name, Resource parent, String title, SiteResource site, Parameters preferences)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("documents.document_node");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("title"), title);
            attrs.put(rc.getAttribute("site"), site);
            attrs.put(rc.getAttribute("preferences"), preferences);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof DocumentNodeResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (DocumentNodeResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>abstract</code> attribute.
     *
     * @return the value of the <code>abstract</code> attribute.
     */
    public String getAbstract()
    {
        return (String)get(abstractDef);
    }
    
    /**
     * Returns the value of the <code>abstract</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>abstract</code> attribute.
     */
    public String getAbstract(String defaultValue)
    {
        if(isDefined(abstractDef))
        {
            return (String)get(abstractDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>abstract</code> attribute.
     *
     * @param value the value of the <code>abstract</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAbstract(String value)
    {
        try
        {
            if(value != null)
            {
                set(abstractDef, value);
            }
            else
            {
                unset(abstractDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>abstract</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>abstract</code> attribute is defined.
	 */
    public boolean isAbstractDefined()
	{
	    return isDefined(abstractDef);
	}
 
    /**
     * Returns the value of the <code>content</code> attribute.
     *
     * @return the value of the <code>content</code> attribute.
     */
    public String getContent()
    {
        return (String)get(contentDef);
    }
    
    /**
     * Returns the value of the <code>content</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>content</code> attribute.
     */
    public String getContent(String defaultValue)
    {
        if(isDefined(contentDef))
        {
            return (String)get(contentDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>content</code> attribute.
     *
     * @param value the value of the <code>content</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setContent(String value)
    {
        try
        {
            if(value != null)
            {
                set(contentDef, value);
            }
            else
            {
                unset(contentDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>content</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>content</code> attribute is defined.
	 */
    public boolean isContentDefined()
	{
	    return isDefined(contentDef);
	}
 
    /**
     * Returns the value of the <code>event_end</code> attribute.
     *
     * @return the value of the <code>event_end</code> attribute.
     */
    public Date getEvent_end()
    {
        return (Date)get(event_endDef);
    }
    
    /**
     * Returns the value of the <code>event_end</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>event_end</code> attribute.
     */
    public Date getEvent_end(Date defaultValue)
    {
        if(isDefined(event_endDef))
        {
            return (Date)get(event_endDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>event_end</code> attribute.
     *
     * @param value the value of the <code>event_end</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEvent_end(Date value)
    {
        try
        {
            if(value != null)
            {
                set(event_endDef, value);
            }
            else
            {
                unset(event_endDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>event_end</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>event_end</code> attribute is defined.
	 */
    public boolean isEvent_endDefined()
	{
	    return isDefined(event_endDef);
	}
 
    /**
     * Returns the value of the <code>event_place</code> attribute.
     *
     * @return the value of the <code>event_place</code> attribute.
     */
    public String getEvent_place()
    {
        return (String)get(event_placeDef);
    }
    
    /**
     * Returns the value of the <code>event_place</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>event_place</code> attribute.
     */
    public String getEvent_place(String defaultValue)
    {
        if(isDefined(event_placeDef))
        {
            return (String)get(event_placeDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>event_place</code> attribute.
     *
     * @param value the value of the <code>event_place</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEvent_place(String value)
    {
        try
        {
            if(value != null)
            {
                set(event_placeDef, value);
            }
            else
            {
                unset(event_placeDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>event_place</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>event_place</code> attribute is defined.
	 */
    public boolean isEvent_placeDefined()
	{
	    return isDefined(event_placeDef);
	}
 
    /**
     * Returns the value of the <code>event_start</code> attribute.
     *
     * @return the value of the <code>event_start</code> attribute.
     */
    public Date getEvent_start()
    {
        return (Date)get(event_startDef);
    }
    
    /**
     * Returns the value of the <code>event_start</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>event_start</code> attribute.
     */
    public Date getEvent_start(Date defaultValue)
    {
        if(isDefined(event_startDef))
        {
            return (Date)get(event_startDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>event_start</code> attribute.
     *
     * @param value the value of the <code>event_start</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEvent_start(Date value)
    {
        try
        {
            if(value != null)
            {
                set(event_startDef, value);
            }
            else
            {
                unset(event_startDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>event_start</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>event_start</code> attribute is defined.
	 */
    public boolean isEvent_startDefined()
	{
	    return isDefined(event_startDef);
	}
 
    /**
     * Returns the value of the <code>footer</code> attribute.
     *
     * @return the value of the <code>footer</code> attribute.
     */
    public String getFooter()
    {
        return (String)get(footerDef);
    }
    
    /**
     * Returns the value of the <code>footer</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>footer</code> attribute.
     */
    public String getFooter(String defaultValue)
    {
        if(isDefined(footerDef))
        {
            return (String)get(footerDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>footer</code> attribute.
     *
     * @param value the value of the <code>footer</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setFooter(String value)
    {
        try
        {
            if(value != null)
            {
                set(footerDef, value);
            }
            else
            {
                unset(footerDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>footer</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>footer</code> attribute is defined.
	 */
    public boolean isFooterDefined()
	{
	    return isDefined(footerDef);
	}
 
    /**
     * Returns the value of the <code>keywords</code> attribute.
     *
     * @return the value of the <code>keywords</code> attribute.
     */
    public String getKeywords()
    {
        return (String)get(keywordsDef);
    }
    
    /**
     * Returns the value of the <code>keywords</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>keywords</code> attribute.
     */
    public String getKeywords(String defaultValue)
    {
        if(isDefined(keywordsDef))
        {
            return (String)get(keywordsDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>keywords</code> attribute.
     *
     * @param value the value of the <code>keywords</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setKeywords(String value)
    {
        try
        {
            if(value != null)
            {
                set(keywordsDef, value);
            }
            else
            {
                unset(keywordsDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>keywords</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>keywords</code> attribute is defined.
	 */
    public boolean isKeywordsDefined()
	{
	    return isDefined(keywordsDef);
	}
 
    /**
     * Returns the value of the <code>lang</code> attribute.
     *
     * @return the value of the <code>lang</code> attribute.
     */
    public String getLang()
    {
        return (String)get(langDef);
    }
    
    /**
     * Returns the value of the <code>lang</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lang</code> attribute.
     */
    public String getLang(String defaultValue)
    {
        if(isDefined(langDef))
        {
            return (String)get(langDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>lang</code> attribute.
     *
     * @param value the value of the <code>lang</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLang(String value)
    {
        try
        {
            if(value != null)
            {
                set(langDef, value);
            }
            else
            {
                unset(langDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>lang</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lang</code> attribute is defined.
	 */
    public boolean isLangDefined()
	{
	    return isDefined(langDef);
	}
 
    /**
     * Returns the value of the <code>meta</code> attribute.
     *
     * @return the value of the <code>meta</code> attribute.
     */
    public String getMeta()
    {
        return (String)get(metaDef);
    }
    
    /**
     * Returns the value of the <code>meta</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>meta</code> attribute.
     */
    public String getMeta(String defaultValue)
    {
        if(isDefined(metaDef))
        {
            return (String)get(metaDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>meta</code> attribute.
     *
     * @param value the value of the <code>meta</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setMeta(String value)
    {
        try
        {
            if(value != null)
            {
                set(metaDef, value);
            }
            else
            {
                unset(metaDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>meta</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>meta</code> attribute is defined.
	 */
    public boolean isMetaDefined()
	{
	    return isDefined(metaDef);
	}
 
    /**
     * Returns the value of the <code>sub_title</code> attribute.
     *
     * @return the value of the <code>sub_title</code> attribute.
     */
    public String getSub_title()
    {
        return (String)get(sub_titleDef);
    }
    
    /**
     * Returns the value of the <code>sub_title</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>sub_title</code> attribute.
     */
    public String getSub_title(String defaultValue)
    {
        if(isDefined(sub_titleDef))
        {
            return (String)get(sub_titleDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>sub_title</code> attribute.
     *
     * @param value the value of the <code>sub_title</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSub_title(String value)
    {
        try
        {
            if(value != null)
            {
                set(sub_titleDef, value);
            }
            else
            {
                unset(sub_titleDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>sub_title</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>sub_title</code> attribute is defined.
	 */
    public boolean isSub_titleDefined()
	{
	    return isDefined(sub_titleDef);
	}
 
    /**
     * Returns the value of the <code>title_calendar</code> attribute.
     *
     * @return the value of the <code>title_calendar</code> attribute.
     */
    public String getTitle_calendar()
    {
        return (String)get(title_calendarDef);
    }
    
    /**
     * Returns the value of the <code>title_calendar</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>title_calendar</code> attribute.
     */
    public String getTitle_calendar(String defaultValue)
    {
        if(isDefined(title_calendarDef))
        {
            return (String)get(title_calendarDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>title_calendar</code> attribute.
     *
     * @param value the value of the <code>title_calendar</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTitle_calendar(String value)
    {
        try
        {
            if(value != null)
            {
                set(title_calendarDef, value);
            }
            else
            {
                unset(title_calendarDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>title_calendar</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>title_calendar</code> attribute is defined.
	 */
    public boolean isTitle_calendarDefined()
	{
	    return isDefined(title_calendarDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////

    // @extends structure.navigation_node

    // @import net.labeo.Labeo
    // @import net.labeo.webcore.RunData
    // @import net.labeo.webcore.ProcessingException
    // @import net.cyklotron.cms.search.SearchUtil
	// @import net.cyklotron.cms.CmsData
    // @import java.util.List
    // @import java.util.Iterator
	
    
    // @order title, site, preferences

    // indexable resource methods //////////////////////////////////////////////////////////////////

    public String getIndexAbbreviation()
    {
        return htmlToText(getAbstract());
    }

    public String getIndexContent()
    {
        return htmlToText(getContent());
    }

    public String getIndexTitle()
    {
        return getTitle();
    }

    public Object getFieldValue(String fieldName)
    {
        if("keywords".equals(fieldName))
        {
            return getKeywords();
        }
        // WARN hack - should be in Navigation Node Resource
        else
        if(fieldName.equals("validity_start"))
        {
            return getValidityStart();
        }
        else
        if(fieldName.equals("event_start"))
        {
            String title = getTitleCalendar();
            if(title == null || title.length()==0)
            {
                return null;
            }
        	Date eS = getEventStart();
            if(eS == null)
            {
                eS = new Date(0);
            }
            return eS;
        }
        else
		if(fieldName.equals("event_end"))
		{
            String title = getTitleCalendar();
            if(title == null || title.length()==0)
            {
                return null;
            }
            Date eE = getEventEnd();
            if(eE == null)
            {
                eE = new Date(Long.MAX_VALUE);
            }
            return eE;
		}
		else
		if(fieldName.equals("title_calendar"))
		{
			String title = getTitleCalendar();
			if(title == null || title.length()==0)
			{
				return EMPTY_TITLE;
			}
			return title;
		}
		else
		if(fieldName.equals("last_redactor"))
		{
			return getLastRedactor();
		}
		else
		if(fieldName.equals("last_editor"))
		{
			return getLastEditor();
		}
        else
        if(fieldName.equals("authors"))
        {
            return getMetaFieldText("/meta/authors");
        }
        if(fieldName.equals("sources"))
        {
            return getMetaFieldText("/meta/sources");
        }
		return null;
    }
    
    private String getMetaFieldText(String xpath)
    {
        String meta = getMeta();
        if(meta == null || meta.length() == 0)
        {
            return null;
        }
        
        try
        {
            StringBuffer buf = new StringBuffer(256);
            org.dom4j.Document metaDom = HTMLUtil.parseXmlAttribute(meta, "meta");
            List nodes = metaDom.selectNodes(xpath);
            for (Iterator iter = nodes.iterator(); iter.hasNext();)
            {
                org.dom4j.Element element = (org.dom4j.Element) iter.next();
                buf.append(element.getStringValue()).append(' ');
            }
            return buf.toString();
        }
        catch (DocumentException e)
        {
            return null;
        }
    }
    
	/**
	 * Returns the store flag of the field.
	 *
	 * @return the store flag.
	 */
	public boolean isStored(String fieldName)
	{
		if(fieldName.equals("validity_start") ||
           fieldName.equals("event_start") ||
		   fieldName.equals("event_end") ||
		   fieldName.equals("title_calendar"))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the indexed flag of the field.
	 *
	 * @return the indexed flag.
	 */
	public boolean isIndexed(String fieldName)
	{
		return true;
	}
		
	/**
	 * Returns the tokenized flag of the field.
	 *
	 * @return the tokenized flag.
	 */
	public boolean isTokenized(String fieldName)
	{
		if(fieldName.equals("validity_start") ||
           fieldName.equals("event_start") ||
		   fieldName.equals("event_end"))
		{
			return false; 
		}
		return true;
	}

    private String htmlToText(String html)
    {
		HTMLService htmlService =
        	(HTMLService)(Labeo.getBroker().getService(HTMLService.SERVICE_NAME));
        try
        {
            return htmlService.htmlToText(html);
        }
        catch(HTMLException e)
        {
            return null;
        }
    }

    // view helper methods //////////////////////////////////////////////////

    private DocumentRenderingHelper docHelper;

    public DocumentTool getDocumentTool(RunData data)
    throws ProcessingException
    {
        if(docHelper == null)
        {
            docHelper = new DocumentRenderingHelper(
            	this, new RequestLinkRenderer(data), new PassThroughHTMLContentFilter());
        }

		// determine current page for this document
		int currentPage = 1; 
		if(this == CmsData.getCmsData(data).getNode())
		{
			currentPage = data.getParameters().get("doc_pg").asInt(1);
		}
		// create tool
        return new DocumentTool(docHelper, currentPage, data.getEncoding());
    }

    public void clearCache()
    {
        docHelper = null;
    }

	public DocumentTool getDocumentTool(
		LinkRenderer linkRenderer, HTMLContentFilter filter, String characterEncoding)
	throws ProcessingException
	{
		DocumentRenderingHelper tmpDocHelper =
			new DocumentRenderingHelper(this, linkRenderer, filter);
		// create tool
		return new DocumentTool(tmpDocHelper, 1, characterEncoding);
	}
}
