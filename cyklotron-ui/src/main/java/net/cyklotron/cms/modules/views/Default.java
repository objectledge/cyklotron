// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
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
package net.cyklotron.cms.modules.views;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.builders.DefaultBuilder;
import org.objectledge.web.mvc.builders.ViewPair;
import org.objectledge.web.mvc.finders.MVCFinder;
import org.picocontainer.PicoContainer;

import net.cyklotron.cms.site.SiteService;

/**
 * A default view.
 *  
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Default.java,v 1.4 2005-02-09 22:22:39 rafal Exp $
 */
public class Default extends DefaultBuilder
{
    /** the finder */
    private MVCFinder mvcFinder;
    
    private PicoContainer container;
    
    public Default(Context context, MVCFinder mvcFinder, 
        PicoContainer container, SiteService siteService)
    {
        super(context);
        this.mvcFinder = mvcFinder;
        this.container = container;
    }

    /**
     * {@inheritDoc}
     */
    public String build(Template template, String embeddedBuildResults) 
        throws BuildException
    {
        container.getComponentInstances();
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        templatingContext.put("mvc_context", MVCContext.getMVCContext(context));
        templatingContext.put("http_context", HttpContext.getHttpContext(context));
        return super.build(template, embeddedBuildResults);
    }
    
    /**
     * {@inheritDoc}
     */
    public ViewPair getEnclosingViewPair(Template template)
    {
        if("Default".equals(mvcFinder.findViewName(template)))
        {
            return new ViewPair(mvcFinder.findBuilder("Page"),
                                 mvcFinder.findBuilderTemplate("Page"));
        }
        return new ViewPair(this, null);
    }
}
