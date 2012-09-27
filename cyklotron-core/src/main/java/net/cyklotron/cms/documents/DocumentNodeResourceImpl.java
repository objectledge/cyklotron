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
import java.util.Map;
import java.util.Set;

import org.objectledge.context.Context;
import org.objectledge.coral.BackendException;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.html.HTMLContentFilter;
import org.objectledge.html.HTMLContentFilterChain;
import org.objectledge.html.HTMLException;
import org.objectledge.html.HTMLService;
import org.objectledge.html.PassThroughHTMLContentFilter;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.internal.DocumentRenderingHelper;
import net.cyklotron.cms.documents.internal.RequestLinkRenderer;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;
import org.dom4j.Document;

/**
 * An implementation of <code>documents.document_node</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class DocumentNodeResourceImpl
    extends NavigationNodeResourceImpl
    implements DocumentNodeResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
	@SuppressWarnings("unused")
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>abstract</code> attribute. */
	private static AttributeDefinition<String> abstractDef;

    /** The AttributeDefinition object for the <code>content</code> attribute. */
	private static AttributeDefinition<String> contentDef;

    /** The AttributeDefinition object for the <code>eventEnd</code> attribute. */
	private static AttributeDefinition<Date> eventEndDef;

    /** The AttributeDefinition object for the <code>eventPlace</code> attribute. */
	private static AttributeDefinition<String> eventPlaceDef;

    /** The AttributeDefinition object for the <code>eventStart</code> attribute. */
	private static AttributeDefinition<Date> eventStartDef;

    /** The AttributeDefinition object for the <code>footer</code> attribute. */
	private static AttributeDefinition<String> footerDef;

    /** The AttributeDefinition object for the <code>keywords</code> attribute. */
	private static AttributeDefinition<String> keywordsDef;

    /** The AttributeDefinition object for the <code>lang</code> attribute. */
	private static AttributeDefinition<String> langDef;

    /** The AttributeDefinition object for the <code>meta</code> attribute. */
	private static AttributeDefinition<String> metaDef;

    /** The AttributeDefinition object for the <code>organizationIds</code> attribute. */
	private static AttributeDefinition<String> organizationIdsDef;

    /** The AttributeDefinition object for the <code>proposedContent</code> attribute. */
	private static AttributeDefinition<String> proposedContentDef;

    /** The AttributeDefinition object for the <code>redactorsNote</code> attribute. */
	private static AttributeDefinition<String> redactorsNoteDef;

    /** The AttributeDefinition object for the <code>relatedResourcesSequence</code> attribute. */
	private static AttributeDefinition<ResourceList> relatedResourcesSequenceDef;

    /** The AttributeDefinition object for the <code>subTitle</code> attribute. */
	private static AttributeDefinition<String> subTitleDef;

    /** The AttributeDefinition object for the <code>titleCalendar</code> attribute. */
	private static AttributeDefinition<String> titleCalendarDef;

	// custom injected fields /////////////////////////////////////////////////
	
    /** The net.cyklotron.cms.site.SiteService. */
    protected net.cyklotron.cms.site.SiteService siteService;

    /** The org.objectledge.html.HTMLService. */
    protected org.objectledge.html.HTMLService htmlService;

    /** The net.cyklotron.cms.structure.StructureService. */
    protected net.cyklotron.cms.structure.StructureService structureService;

    /** The net.cyklotron.cms.CmsDataFactory. */
    protected net.cyklotron.cms.CmsDataFactory cmsDataFactory;

    /** The org.objectledge.web.mvc.tools.LinkToolFactory. */
    protected org.objectledge.web.mvc.tools.LinkToolFactory linkToolFactory;

    /** The org.objectledge.cache.CacheFactory. */
    protected org.objectledge.cache.CacheFactory cacheFactory;

    /** The net.cyklotron.cms.documents.DocumentService. */
    protected net.cyklotron.cms.documents.DocumentService documentService;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>documents.document_node</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param securityService the net.cyklotron.cms.security.SecurityService.
     * @param siteService the net.cyklotron.cms.site.SiteService.
     * @param htmlService the org.objectledge.html.HTMLService.
     * @param structureService the net.cyklotron.cms.structure.StructureService.
     * @param cmsDataFactory the net.cyklotron.cms.CmsDataFactory.
     * @param linkToolFactory the org.objectledge.web.mvc.tools.LinkToolFactory.
     * @param cacheFactory the org.objectledge.cache.CacheFactory.
     * @param documentService the net.cyklotron.cms.documents.DocumentService.
     */
    public DocumentNodeResourceImpl(net.cyklotron.cms.security.SecurityService securityService,
        net.cyklotron.cms.site.SiteService siteService, org.objectledge.html.HTMLService
        htmlService, net.cyklotron.cms.structure.StructureService structureService,
        net.cyklotron.cms.CmsDataFactory cmsDataFactory,
        org.objectledge.web.mvc.tools.LinkToolFactory linkToolFactory,
        org.objectledge.cache.CacheFactory cacheFactory, net.cyklotron.cms.documents.DocumentService
        documentService)
    {
        super(securityService);
        this.siteService = siteService;
        this.htmlService = htmlService;
        this.structureService = structureService;
        this.cmsDataFactory = cmsDataFactory;
        this.linkToolFactory = linkToolFactory;
        this.cacheFactory = cacheFactory;
        this.documentService = documentService;
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
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static DocumentNodeResource createDocumentNodeResource(CoralSession session, String
        name, Resource parent, String title, SiteResource site, Parameters preferences)
        throws ValueRequiredException, InvalidResourceNameException
    {
        try
        {
            ResourceClass<DocumentNodeResource> rc = session.getSchema().getResourceClass("documents.document_node", DocumentNodeResource.class);
			Map<AttributeDefinition<?>, Object> attrs = new HashMap<AttributeDefinition<?>, Object>();
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
        return get(abstractDef);
    }
    
    /**
     * Returns the value of the <code>abstract</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>abstract</code> attribute.
     */
    public String getAbstract(String defaultValue)
    {
        return get(abstractDef, defaultValue);
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
        return get(contentDef);
    }
    
    /**
     * Returns the value of the <code>content</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>content</code> attribute.
     */
    public String getContent(String defaultValue)
    {
        return get(contentDef, defaultValue);
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
        return get(eventEndDef);
    }
    
    /**
     * Returns the value of the <code>eventEnd</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>eventEnd</code> attribute.
     */
    public Date getEventEnd(Date defaultValue)
    {
        return get(eventEndDef, defaultValue);
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
        return get(eventPlaceDef);
    }
    
    /**
     * Returns the value of the <code>eventPlace</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>eventPlace</code> attribute.
     */
    public String getEventPlace(String defaultValue)
    {
        return get(eventPlaceDef, defaultValue);
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
        return get(eventStartDef);
    }
    
    /**
     * Returns the value of the <code>eventStart</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>eventStart</code> attribute.
     */
    public Date getEventStart(Date defaultValue)
    {
        return get(eventStartDef, defaultValue);
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
        return get(footerDef);
    }
    
    /**
     * Returns the value of the <code>footer</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>footer</code> attribute.
     */
    public String getFooter(String defaultValue)
    {
        return get(footerDef, defaultValue);
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
        return get(keywordsDef);
    }
    
    /**
     * Returns the value of the <code>keywords</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>keywords</code> attribute.
     */
    public String getKeywords(String defaultValue)
    {
        return get(keywordsDef, defaultValue);
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
        return get(langDef);
    }
    
    /**
     * Returns the value of the <code>lang</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lang</code> attribute.
     */
    public String getLang(String defaultValue)
    {
        return get(langDef, defaultValue);
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
        return get(metaDef);
    }
    
    /**
     * Returns the value of the <code>meta</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>meta</code> attribute.
     */
    public String getMeta(String defaultValue)
    {
        return get(metaDef, defaultValue);
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
     * Returns the value of the <code>organizationIds</code> attribute.
     *
     * @return the value of the <code>organizationIds</code> attribute.
     */
    public String getOrganizationIds()
    {
        return get(organizationIdsDef);
    }
    
    /**
     * Returns the value of the <code>organizationIds</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>organizationIds</code> attribute.
     */
    public String getOrganizationIds(String defaultValue)
    {
        return get(organizationIdsDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>organizationIds</code> attribute.
     *
     * @param value the value of the <code>organizationIds</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setOrganizationIds(String value)
    {
        try
        {
            if(value != null)
            {
                set(organizationIdsDef, value);
            }
            else
            {
                unset(organizationIdsDef);
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
	 * Checks if the value of the <code>organizationIds</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>organizationIds</code> attribute is defined.
	 */
    public boolean isOrganizationIdsDefined()
	{
	    return isDefined(organizationIdsDef);
	}
 
    /**
     * Returns the value of the <code>proposedContent</code> attribute.
     *
     * @return the value of the <code>proposedContent</code> attribute.
     */
    public String getProposedContent()
    {
        return get(proposedContentDef);
    }
    
    /**
     * Returns the value of the <code>proposedContent</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>proposedContent</code> attribute.
     */
    public String getProposedContent(String defaultValue)
    {
        return get(proposedContentDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>proposedContent</code> attribute.
     *
     * @param value the value of the <code>proposedContent</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setProposedContent(String value)
    {
        try
        {
            if(value != null)
            {
                set(proposedContentDef, value);
            }
            else
            {
                unset(proposedContentDef);
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
	 * Checks if the value of the <code>proposedContent</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>proposedContent</code> attribute is defined.
	 */
    public boolean isProposedContentDefined()
	{
	    return isDefined(proposedContentDef);
	}
 
    /**
     * Returns the value of the <code>redactorsNote</code> attribute.
     *
     * @return the value of the <code>redactorsNote</code> attribute.
     */
    public String getRedactorsNote()
    {
        return get(redactorsNoteDef);
    }
    
    /**
     * Returns the value of the <code>redactorsNote</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>redactorsNote</code> attribute.
     */
    public String getRedactorsNote(String defaultValue)
    {
        return get(redactorsNoteDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>redactorsNote</code> attribute.
     *
     * @param value the value of the <code>redactorsNote</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRedactorsNote(String value)
    {
        try
        {
            if(value != null)
            {
                set(redactorsNoteDef, value);
            }
            else
            {
                unset(redactorsNoteDef);
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
	 * Checks if the value of the <code>redactorsNote</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>redactorsNote</code> attribute is defined.
	 */
    public boolean isRedactorsNoteDefined()
	{
	    return isDefined(redactorsNoteDef);
	}
 
    /**
     * Returns the value of the <code>relatedResourcesSequence</code> attribute.
     *
     * @return the value of the <code>relatedResourcesSequence</code> attribute.
     */
    public ResourceList getRelatedResourcesSequence()
    {
        return get(relatedResourcesSequenceDef);
    }
    
    /**
     * Returns the value of the <code>relatedResourcesSequence</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>relatedResourcesSequence</code> attribute.
     */
    public ResourceList getRelatedResourcesSequence(ResourceList defaultValue)
    {
        return get(relatedResourcesSequenceDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>relatedResourcesSequence</code> attribute.
     *
     * @param value the value of the <code>relatedResourcesSequence</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRelatedResourcesSequence(ResourceList value)
    {
        try
        {
            if(value != null)
            {
                set(relatedResourcesSequenceDef, value);
            }
            else
            {
                unset(relatedResourcesSequenceDef);
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
	 * Checks if the value of the <code>relatedResourcesSequence</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>relatedResourcesSequence</code> attribute is defined.
	 */
    public boolean isRelatedResourcesSequenceDefined()
	{
	    return isDefined(relatedResourcesSequenceDef);
	}
 
    /**
     * Returns the value of the <code>subTitle</code> attribute.
     *
     * @return the value of the <code>subTitle</code> attribute.
     */
    public String getSubTitle()
    {
        return get(subTitleDef);
    }
    
    /**
     * Returns the value of the <code>subTitle</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>subTitle</code> attribute.
     */
    public String getSubTitle(String defaultValue)
    {
        return get(subTitleDef, defaultValue);
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
        return get(titleCalendarDef);
    }
    
    /**
     * Returns the value of the <code>titleCalendar</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>titleCalendar</code> attribute.
     */
    public String getTitleCalendar(String defaultValue)
    {
        return get(titleCalendarDef, defaultValue);
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
    // @import java.util.Set
    // @import org.dom4j.Document    
    // @import net.cyklotron.cms.CmsDataFactory
    // @import net.cyklotron.cms.site.SiteService
    // @import net.cyklotron.cms.structure.NavigationNodeResourceImpl
    // @import net.cyklotron.cms.structure.StructureService
    // @import net.cyklotron.cms.structure.StructureException
    // @import org.objectledge.context.Context
    // @import org.objectledge.coral.session.CoralSession
    // @import org.objectledge.html.HTMLContentFilter
    // @import org.objectledge.html.HTMLContentFilterChain
    // @import org.objectledge.html.HTMLException
    // @import org.objectledge.html.HTMLService
    // @import org.objectledge.html.PassThroughHTMLContentFilter    
    // @import org.objectledge.parameters.Parameters
    // @import org.objectledge.parameters.RequestParameters
    // @import org.objectledge.pipeline.ProcessingException
    // @import org.objectledge.web.HttpContext    
    // @import net.cyklotron.cms.documents.internal.DocumentRenderingHelper
    // @import net.cyklotron.cms.documents.internal.RequestLinkRenderer

	// @field net.cyklotron.cms.site.SiteService siteService
    // @field org.objectledge.html.HTMLService htmlService
    // @field net.cyklotron.cms.structure.StructureService structureService
    // @field net.cyklotron.cms.CmsDataFactory cmsDataFactory
    // @field org.objectledge.web.mvc.tools.LinkToolFactory linkToolFactory
    // @field org.objectledge.cache.CacheFactory cacheFactory
    // @field net.cyklotron.cms.documents.DocumentService documentService
    
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
        if(fieldName.equals("validityStart"))
        {
            return getValidityStart();
        }
        else
        if(fieldName.equals("eventStart"))
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
		if(fieldName.equals("eventEnd"))
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
		if(fieldName.equals("titleCalendar"))
		{
			String title = getTitleCalendar();
			if(title == null || title.length()==0)
			{
				return EMPTY_TITLE;
			}
			return title;
		}
		else
		if(fieldName.equals("lastRedactor"))
		{
			return getLastRedactor();
		}
		else
		if(fieldName.equals("lastEditor"))
		{
			return getLastEditor();
		}
        else
        if(fieldName.equals("authors"))
        {
            return getMetaFieldText("/meta/authors");
        }
        else
        if(fieldName.equals("authorsName"))
        {
            return getMetaFieldText("/meta/authors/author/name");
        }
        else
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
            org.dom4j.Document metaDom = DocumentMetadataHelper.textToDom4j(meta);
            return DocumentMetadataHelper.selectAllText(metaDom, xpath);
        }
        catch (HTMLException e)
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
		if(fieldName.equals("validityStart") ||
           fieldName.equals("eventStart") ||
		   fieldName.equals("eventEnd") ||
		   fieldName.equals("titleCalendar"))
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
		if(fieldName.equals("validityStart") ||
           fieldName.equals("eventStart") ||
		   fieldName.equals("eventEnd"))
		{
			return false; 
		}
		return true;
	}

    private String htmlToText(String html)
    {
        try
        {
            Document doc = htmlService.textToDom4j(html);
            return htmlService.collectText(doc);
        }
        catch(HTMLException e)
        {
            return null;
        }
    }

    // view helper methods //////////////////////////////////////////////////
   
    public DocumentTool getDocumentTool(Context context)
        throws ProcessingException
    {
        DocumentRenderingHelper docHelper = getDocumentRenderingHelper(context);

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

    private DocumentRenderingHelper getDocumentRenderingHelper(Context context)
        throws ProcessingException
    {
        Map helperMap = cacheFactory.getInstance("docRenderingHelpers");
        synchronized(helperMap)
        {
            DocumentRenderingHelper docHelper = (DocumentRenderingHelper)helperMap.get(getIdObject());
            if(docHelper == null)
            {
                HttpContext httpContext = HttpContext.getHttpContext(context);
                CoralSession coralSession = context.getAttribute(CoralSession.class);
                RequestLinkRenderer linkRenderer = new RequestLinkRenderer(siteService, httpContext,
                    linkToolFactory);
                HTMLContentFilter filter = documentService.getContentFilter(this, linkRenderer, coralSession);
                docHelper = new DocumentRenderingHelper(coralSession, siteService, structureService,
                    htmlService, this, linkRenderer, filter);
                helperMap.put(getIdObject(), docHelper);
            }
            return docHelper;
        }
    }

    public void clearCache()
    {
        Map helperMap = cacheFactory.getInstance("docRenderingHelpers");
        synchronized(helperMap)
        {
            helperMap.remove(getIdObject());
        }
    }

    public DocumentTool getDocumentTool(CoralSession coralSession, LinkRenderer linkRenderer,
        HTMLContentFilter filter, String characterEncoding)
        throws ProcessingException
    {
        HTMLContentFilter stdFilter = documentService.getContentFilter(this, linkRenderer, coralSession);
        DocumentRenderingHelper tmpDocHelper = new DocumentRenderingHelper(coralSession,
            siteService, structureService, htmlService, this, linkRenderer, new HTMLContentFilterChain(
                stdFilter, filter));
        // create tool
        return new DocumentTool(tmpDocHelper, 1, characterEncoding);
    }
    
    public String getFooterContent(Context context)
        throws Exception
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        return documentService.getFooterContent(coralSession, getSite(), getFooter());
    }

    /**
     * Returns the existing aliases referring to a specified document node.
     * 
     * @return the aliases referring to this node.
     * @throws StructureException when alias tracking Coral relation cannot be accessed.
     */
    public Set<DocumentAliasResource> getAliases(CoralSession coralSession) throws StructureException
    {
        return structureService.getAliases(coralSession, this);
    }
}
