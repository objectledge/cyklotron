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

import org.objectledge.context.Context;
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
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
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

    /** The AttributeDefinition object for the <code>eventEnd</code> attribute. */
    private AttributeDefinition eventEndDef;

    /** The AttributeDefinition object for the <code>eventPlace</code> attribute. */
    private AttributeDefinition eventPlaceDef;

    /** The AttributeDefinition object for the <code>eventStart</code> attribute. */
    private AttributeDefinition eventStartDef;

    /** The AttributeDefinition object for the <code>footer</code> attribute. */
    private AttributeDefinition footerDef;

    /** The AttributeDefinition object for the <code>keywords</code> attribute. */
    private AttributeDefinition keywordsDef;

    /** The AttributeDefinition object for the <code>lang</code> attribute. */
    private AttributeDefinition langDef;

    /** The AttributeDefinition object for the <code>meta</code> attribute. */
    private AttributeDefinition metaDef;

    /** The AttributeDefinition object for the <code>subTitle</code> attribute. */
    private AttributeDefinition subTitleDef;

    /** The AttributeDefinition object for the <code>titleCalendar</code> attribute. */
    private AttributeDefinition titleCalendarDef;

	// custom injected fields /////////////////////////////////////////////////
	
    /** The SiteService. */
    protected SiteService siteService;

    /** The HTMLService. */
    protected HTMLService htmlService;

    /** The StructureService. */
    protected StructureService structureService;

    /** The CmsDataFactory. */
    protected CmsDataFactory cmsDataFactory;

    /** The org.objectledge.web.mvc.tools.LinkToolFactory. */
    protected org.objectledge.web.mvc.tools.LinkToolFactory linkToolFactory;

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
     * @param siteService the SiteService.
     * @param htmlService the HTMLService.
     * @param structureService the StructureService.
     * @param cmsDataFactory the CmsDataFactory.
     * @param linkToolFactory the org.objectledge.web.mvc.tools.LinkToolFactory.
     */
    public DocumentNodeResourceImpl(CoralSchema schema, Database database, Logger logger,
        SiteService siteService, HTMLService htmlService, StructureService structureService,
        CmsDataFactory cmsDataFactory, org.objectledge.web.mvc.tools.LinkToolFactory
        linkToolFactory)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("documents.document_node");
            abstractDef = rc.getAttribute("abstract");
            contentDef = rc.getAttribute("content");
            eventEndDef = rc.getAttribute("eventEnd");
            eventPlaceDef = rc.getAttribute("eventPlace");
            eventStartDef = rc.getAttribute("eventStart");
            footerDef = rc.getAttribute("footer");
            keywordsDef = rc.getAttribute("keywords");
            langDef = rc.getAttribute("lang");
            metaDef = rc.getAttribute("meta");
            subTitleDef = rc.getAttribute("subTitle");
            titleCalendarDef = rc.getAttribute("titleCalendar");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
        this.siteService = siteService;
        this.htmlService = htmlService;
        this.structureService = structureService;
        this.cmsDataFactory = cmsDataFactory;
        this.linkToolFactory = linkToolFactory;
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
     * Returns the value of the <code>eventEnd</code> attribute.
     *
     * @return the value of the <code>eventEnd</code> attribute.
     */
    public Date getEventEnd()
    {
        return (Date)get(eventEndDef);
    }
    
    /**
     * Returns the value of the <code>eventEnd</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>eventEnd</code> attribute.
     */
    public Date getEventEnd(Date defaultValue)
    {
        if(isDefined(eventEndDef))
        {
            return (Date)get(eventEndDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>eventEnd</code> attribute.
     *
     * @param value the value of the <code>eventEnd</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEventEnd(Date value)
    {
        try
        {
            if(value != null)
            {
                set(eventEndDef, value);
            }
            else
            {
                unset(eventEndDef);
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
	 * Checks if the value of the <code>eventEnd</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>eventEnd</code> attribute is defined.
	 */
    public boolean isEventEndDefined()
	{
	    return isDefined(eventEndDef);
	}
 
    /**
     * Returns the value of the <code>eventPlace</code> attribute.
     *
     * @return the value of the <code>eventPlace</code> attribute.
     */
    public String getEventPlace()
    {
        return (String)get(eventPlaceDef);
    }
    
    /**
     * Returns the value of the <code>eventPlace</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>eventPlace</code> attribute.
     */
    public String getEventPlace(String defaultValue)
    {
        if(isDefined(eventPlaceDef))
        {
            return (String)get(eventPlaceDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>eventPlace</code> attribute.
     *
     * @param value the value of the <code>eventPlace</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEventPlace(String value)
    {
        try
        {
            if(value != null)
            {
                set(eventPlaceDef, value);
            }
            else
            {
                unset(eventPlaceDef);
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
	 * Checks if the value of the <code>eventPlace</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>eventPlace</code> attribute is defined.
	 */
    public boolean isEventPlaceDefined()
	{
	    return isDefined(eventPlaceDef);
	}
 
    /**
     * Returns the value of the <code>eventStart</code> attribute.
     *
     * @return the value of the <code>eventStart</code> attribute.
     */
    public Date getEventStart()
    {
        return (Date)get(eventStartDef);
    }
    
    /**
     * Returns the value of the <code>eventStart</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>eventStart</code> attribute.
     */
    public Date getEventStart(Date defaultValue)
    {
        if(isDefined(eventStartDef))
        {
            return (Date)get(eventStartDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>eventStart</code> attribute.
     *
     * @param value the value of the <code>eventStart</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEventStart(Date value)
    {
        try
        {
            if(value != null)
            {
                set(eventStartDef, value);
            }
            else
            {
                unset(eventStartDef);
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
	 * Checks if the value of the <code>eventStart</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>eventStart</code> attribute is defined.
	 */
    public boolean isEventStartDefined()
	{
	    return isDefined(eventStartDef);
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
     * Returns the value of the <code>subTitle</code> attribute.
     *
     * @return the value of the <code>subTitle</code> attribute.
     */
    public String getSubTitle()
    {
        return (String)get(subTitleDef);
    }
    
    /**
     * Returns the value of the <code>subTitle</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>subTitle</code> attribute.
     */
    public String getSubTitle(String defaultValue)
    {
        if(isDefined(subTitleDef))
        {
            return (String)get(subTitleDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>subTitle</code> attribute.
     *
     * @param value the value of the <code>subTitle</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSubTitle(String value)
    {
        try
        {
            if(value != null)
            {
                set(subTitleDef, value);
            }
            else
            {
                unset(subTitleDef);
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
	 * Checks if the value of the <code>subTitle</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>subTitle</code> attribute is defined.
	 */
    public boolean isSubTitleDefined()
	{
	    return isDefined(subTitleDef);
	}
 
    /**
     * Returns the value of the <code>titleCalendar</code> attribute.
     *
     * @return the value of the <code>titleCalendar</code> attribute.
     */
    public String getTitleCalendar()
    {
        return (String)get(titleCalendarDef);
    }
    
    /**
     * Returns the value of the <code>titleCalendar</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>titleCalendar</code> attribute.
     */
    public String getTitleCalendar(String defaultValue)
    {
        if(isDefined(titleCalendarDef))
        {
            return (String)get(titleCalendarDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>titleCalendar</code> attribute.
     *
     * @param value the value of the <code>titleCalendar</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTitleCalendar(String value)
    {
        try
        {
            if(value != null)
            {
                set(titleCalendarDef, value);
            }
            else
            {
                unset(titleCalendarDef);
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
	 * Checks if the value of the <code>titleCalendar</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>titleCalendar</code> attribute is defined.
	 */
    public boolean isTitleCalendarDefined()
	{
	    return isDefined(titleCalendarDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////

    // @extends structure.navigation_node
    // @import java.util.List
    // @import java.util.Iterator
    // @import net.cyklotron.cms.CmsDataFactory
    // @import net.cyklotron.cms.site.SiteService
    // @import net.cyklotron.cms.structure.NavigationNodeResourceImpl
    // @import net.cyklotron.cms.structure.StructureService
    // @import org.objectledge.context.Context
    // @import org.objectledge.coral.session.CoralSession
    // @import org.objectledge.parameters.Parameters
    // @import org.objectledge.parameters.RequestParameters
    // @import org.objectledge.pipeline.ProcessingException
    // @import org.objectledge.web.HttpContext    
	// @field SiteService siteService
    // @field HTMLService htmlService
    // @field StructureService structureService
    // @field CmsDataFactory cmsDataFactory
    // @field org.objectledge.web.mvc.tools.LinkToolFactory linkToolFactory
    
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
            StringBuilder buf = new StringBuilder(256);
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

    public DocumentTool getDocumentTool(Context context)
    throws ProcessingException
    {
        if(docHelper == null)
        {
            docHelper = new DocumentRenderingHelper(context, siteService,
                structureService, htmlService,
            	this, new RequestLinkRenderer(siteService, context), new PassThroughHTMLContentFilter());
        }

		// determine current page for this document
		int currentPage = 1; 
		if(this == cmsDataFactory.getCmsData(context).getNode())
		{
            Parameters parameters = RequestParameters.getRequestParameters(context);
			currentPage = parameters.getInt("doc_pg",1);
		}
		// create tool
        HttpContext httpContext = HttpContext.getHttpContext(context);
        return new DocumentTool(docHelper, currentPage, httpContext.getEncoding());
    }

    public void clearCache()
    {
        docHelper = null;
    }

	public DocumentTool getDocumentTool(Context context,
		LinkRenderer linkRenderer, HTMLContentFilter filter, String characterEncoding)
	throws ProcessingException
	{
		DocumentRenderingHelper tmpDocHelper =
			new DocumentRenderingHelper(context, siteService,
                structureService, htmlService,this, linkRenderer, filter);
		// create tool
		return new DocumentTool(tmpDocHelper, 1, characterEncoding);
	}
}
