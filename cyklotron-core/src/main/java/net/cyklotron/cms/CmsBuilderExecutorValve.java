// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
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
package net.cyklotron.cms;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.builders.Builder;
import org.objectledge.web.mvc.builders.BuilderExecutorValve;
import org.objectledge.web.mvc.builders.DefaultBuilder;
import org.objectledge.web.mvc.finders.MVCClassFinder;
import org.objectledge.web.mvc.finders.MVCTemplateFinder;
import org.objectledge.web.mvc.security.SecurityHelper;
import org.objectledge.web.mvc.tools.LinkTool;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.style.StyleService;

/**
 * Pipeline component for executing MVC view building.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CmsBuilderExecutorValve.java,v 1.4 2005-04-12 06:59:02 pablo Exp $
 */
public class CmsBuilderExecutorValve 
    implements Valve
{
    protected BuilderExecutorValve standardBuilder;
	/** Finder for builder objects. */
	protected MVCClassFinder classFinder;
	/** Finder for template objects. */
	protected MVCTemplateFinder templateFinder;
    /** SecurityHelper for access checking. */
    protected SecurityHelper securityHelper;
    
    protected CmsDataFactory cmsDataFactory;
    
    protected StyleService styleService;
    
    protected SkinService skinService;
	/**
	 * Component constructor.
	 * 
     * @param classFinder finder for builder objects
     * @param templateFinder finder for template objects
     * @param securityHelper security helper for access checking
     * @param standardBuilder the standard builder.
	 */
	public CmsBuilderExecutorValve(MVCClassFinder classFinder,
        MVCTemplateFinder templateFinder, SecurityHelper securityHelper,
        BuilderExecutorValve standardBuilder, CmsDataFactory cmsDataFactory,
        StyleService styleService, SkinService skinService)
	{
		this.classFinder = classFinder;
		this.templateFinder = templateFinder;
        this.securityHelper = securityHelper;
        this.standardBuilder = standardBuilder;
        this.cmsDataFactory = cmsDataFactory;
        this.styleService = styleService;
        this.skinService = skinService;
	}
	
	/**
	 * Run view building starting from a view builder chosen in request parameters.
     * 
     * @param context the thread's processing context.
     * @throws ProcessingException if the processing fails.
	 */
	public void process(Context context)
        throws ProcessingException
	{
        System.out.println("CMS BUILDER EXECUTOR FIRED!!!");
        Parameters parameters = RequestParameters.getRequestParameters(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        templatingContext.put("parameters", parameters);
        
        // Check if we are just browsing the sites (x parameter is
        // defined, or site_id is defined with no view)
        boolean x_p = parameters.isDefined("x");
        boolean n_p = parameters.isDefined("node_id");
        boolean s_p = parameters.isDefined("site_id");
        String view = parameters.get("view","");
        boolean v_p = !(view.length() == 0 || view.equals("Default"));
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        
        // this one here is not for the feeble of the heart...
        if(!(x_p || (!x_p && !n_p && s_p && !v_p)))
        // admin interface
        {
            SiteResource site = cmsData.getSite();
            if(site != null)
            {
                LinkTool link = (LinkTool)templatingContext.get("link");
                templatingContext.put("link", link.set("site_id",site.getIdString()));
            }
            standardBuilder.process(context);
            return;
        }
        // site browsing
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        NavigationNodeResource node = cmsData.getNode();
        
        Template template = null;
        if(node != null)
        {
            // choose layout normally
            if(cmsData.getBrowseMode().equals("emergency"))
            {
                template = templateFinder.findBuilderTemplate("Emergency").getTemplate();
            }
            else
            {
                StyleResource style = node.getEffectiveStyle();
                int level = node.getLevel();
                String layout = styleService.getLayout(coralSession, style, level);
                try
                {
                    template = skinService.
                        getLayoutTemplate(coralSession, node.getSite(), cmsData.getSkinName(), layout);
                }
                catch(Exception e)
                {
                    throw new ProcessingException("exception occured during template lookup",e);
                }
            }
        }
        else
        {
            throw new ProcessingException("Node not found!");
        }
        
        System.out.println("CMS PAGE PROCESSING...");
		// setup used contexts
        HttpContext httpContext = HttpContext.getHttpContext(context);
		MVCContext mvcContext = MVCContext.getMVCContext(context);
        
		// get initial builder, template and embedded result
		String result = null;
		Builder builder = new DefaultBuilder(context); 
            //classFinder.findBuilder("CmsLayout");
		
        System.out.println("TEMLATE "+template.getName());
        //securityHelper.checkSecurity(builder, context);

        // build view level --------------------------------------------------------------------
        try
        {
            result = builder.build(template, null);
            templatingContext.put("cmsLayoutPlaceholder", result);
            template = templateFinder.findBuilderTemplate("CmsPage").getTemplate();
            result = builder.build(template, null);
	    }
	    catch (BuildException e)
	    {
            System.out.println(new StackTrace(e));
	        throw new ProcessingException(e);
	    }
	    
        // escape on direct response
        if(httpContext.getDirectResponse())
        {
            return;
        }
		// store building result
		mvcContext.setBuildResult(result);
	}
}
