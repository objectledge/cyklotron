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

import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.parameters.Parameters;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.site.SiteResource;

/**
 * An implementation of <code>documents.document_alias</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class DocumentAliasResourceImpl
    extends DocumentNodeResourceImpl
    implements DocumentAliasResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>originalDocument</code> attribute. */
    private static AttributeDefinition originalDocumentDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>documents.document_alias</code> resource wrapper.
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
    public DocumentAliasResourceImpl(net.cyklotron.cms.security.SecurityService securityService,
        net.cyklotron.cms.site.SiteService siteService, org.objectledge.html.HTMLService
        htmlService, net.cyklotron.cms.structure.StructureService structureService,
        net.cyklotron.cms.CmsDataFactory cmsDataFactory,
        org.objectledge.web.mvc.tools.LinkToolFactory linkToolFactory,
        org.objectledge.cache.CacheFactory cacheFactory, net.cyklotron.cms.documents.DocumentService
        documentService)
    {
        super(securityService, siteService, htmlService, structureService, cmsDataFactory, linkToolFactory, cacheFactory, documentService);
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>documents.document_alias</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static DocumentAliasResource getDocumentAliasResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof DocumentAliasResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not documents.document_alias");
        }
        return (DocumentAliasResource)res;
    }

    /**
     * Creates a new <code>documents.document_alias</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param originalDocument the originalDocument attribute
     * @param preferences the preferences attribute
     * @param site the site attribute
     * @param title the title attribute
     * @return a new DocumentAliasResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static DocumentAliasResource createDocumentAliasResource(CoralSession session, String
        name, Resource parent, DocumentNodeResource originalDocument, Parameters preferences,
        SiteResource site, String title)
        throws ValueRequiredException, InvalidResourceNameException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("documents.document_alias");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("originalDocument"), originalDocument);
            attrs.put(rc.getAttribute("preferences"), preferences);
            attrs.put(rc.getAttribute("site"), site);
            attrs.put(rc.getAttribute("title"), title);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof DocumentAliasResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (DocumentAliasResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>originalDocument</code> attribute.
     *
     * @return the value of the <code>originalDocument</code> attribute.
     */
    public DocumentNodeResource getOriginalDocument()
    {
        return (DocumentNodeResource)getInternal(originalDocumentDef, null);
    }
 
    /**
     * Sets the value of the <code>originalDocument</code> attribute.
     *
     * @param value the value of the <code>originalDocument</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setOriginalDocument(DocumentNodeResource value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(originalDocumentDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute originalDocument "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
     
    // @custom methods ///////////////////////////////////////////////////////

    // @import java.util.Date
    // @import org.objectledge.coral.security.Subject
    // @import net.cyklotron.cms.CmsData
    
    /**
     * Getter delegated to originalDocument.
     */
    public String getLang()
    {
        return getOriginalDocument().getLang();
    }
    
    /**
     * Getter delegated to originalDocument.
     */
    public String getSubTitle()
    {
        return getOriginalDocument().getSubTitle();
    }
    
    /**
     * Getter delegated to originalDocument.
     */
    public String getTitleCalendar()
    {
        return getOriginalDocument().getTitleCalendar();
    }
    
    /**
     * Getter delegated to originalDocument.
     */
    public String getKeywords()
    {
        return getOriginalDocument().getKeywords();
    }
    
    /**
     * Getter delegated to originalDocument.
     */
    public String getAbstract()
    {
        return getOriginalDocument().getAbstract();
    }
    
    /**
     * Getter delegated to originalDocument.
     */
    public String getContent()
    {
        return getOriginalDocument().getContent();
    }
    
    /**
     * Getter delegated to originalDocument.
     */
    public String getFooter()
    {
        return getOriginalDocument().getFooter();
    }
    
    /**
     * Getter delegated to originalDocument.
     */
    public String getMeta()
    {
        return getOriginalDocument().getMeta();
    }
    
    /**
     * Requires access rights both to this node and originalDocument node.
     */
    public boolean canView(CoralSession coralSession, Subject subject)
    {
        return super.canView(coralSession, subject)
            && getOriginalDocument().canView(coralSession, subject);
    }

    /**
     * Requires access rights both to this node and originalDocument node.
     */
    public boolean canView(CoralSession coralSession,
        Subject subject, Date date)
    {
        return super.canView(coralSession, subject, date)
            && getOriginalDocument().canView(coralSession, subject, date);
    }

    /**
     * Requires access rights both to this node and originalDocument node.
     */
    public boolean canView(CoralSession coralSession, CmsData cmsData,
        Subject subject)
    {
        return super.canView(coralSession, cmsData, subject)
            && getOriginalDocument().canView(coralSession, cmsData, subject);
    }
}
