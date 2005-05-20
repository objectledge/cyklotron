// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//	 this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//	 this list of conditions and the following disclaimer in the documentation  
//	 and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//	 nor the names of its contributors may be used to endorse or promote products  
//	 derived from this software without specific prior written permission. 
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
package net.cyklotron.cms.modules.views.structure;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.SubtreeVisitor;
import org.objectledge.coral.table.comparator.IdComparator;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.visitor.Visitor;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: PublicNodes.java,v 1.6 2005-05-20 00:46:44 rafal Exp $
 */
public class PublicNodes
    extends BaseCMSScreen
{
    
    private final StructureService structureService;

    public PublicNodes(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager, StructureService structureService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.structureService = structureService;
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        SiteResource site = getSite();
        final Subject subject = coralSession.getUserSubject();
        final Date now = new Date();
        final List<NavigationNodeResource> visible = new ArrayList<NavigationNodeResource>();
        SortedMap<SiteResource,List<NavigationNodeResource>> siteMap;
        try
        {
            final Resource traversalRoot = site != null ? 
                structureService.getRootNode(coralSession, site) : 
                coralSession.getStore().getUniqueResourceByPath("/cms/sites");
            Visitor visitor = new SubtreeVisitor()
            {
                public void visit(NavigationNodeResource node)
                {
                    if(node.canView(context, subject, now))
                    {
                        visible.add(node);
                    }
                }
            };
            visitor.traverseBreadthFirst(traversalRoot);
            Collections.sort(visible, new IdComparator());

            siteMap = sortNodes(visible, i18nContext.getLocale());
            templatingContext.put("siteMap", siteMap);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to lookup accessible nodes", e);
        }
        if(parameters.isDefined("text"))
        {
            try
            {
                httpContext.setContentType("text/plain");
                PrintWriter w = httpContext.getPrintWriter();
                for(SiteResource s : siteMap.keySet())
                {
                    List<NavigationNodeResource> l = siteMap.get(s);
                    w.println("site name="+s.getName() + " id=" + s.getIdString() + 
                        " pages="+ l.size());
                    for(NavigationNodeResource node : l)
                    {
                        w.println(node.getIdString());
                    }                    
                }
                w.flush();
            }
            catch(Exception e)
            {
                throw new ProcessingException("failed to write out node identifiers", e);
            }
        }
    }
    
    private SortedMap<SiteResource, List<NavigationNodeResource>> sortNodes(
        List<NavigationNodeResource> nodes, Locale locale)
    {
        SortedMap<SiteResource,List<NavigationNodeResource>> siteMap = 
            new TreeMap<SiteResource,List<NavigationNodeResource>>(new NameComparator(locale));
        for(NavigationNodeResource n : nodes)
        {
            List<NavigationNodeResource> l = siteMap.get(n.getSite());
            if(l == null)
            {
                l = new ArrayList<NavigationNodeResource>();
                siteMap.put(n.getSite(), l);
            }
            l.add(n);
        }
        return siteMap;
    }
    
    /**
     * @{inheritDoc}
     */
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return false;
    }
    
    
}
