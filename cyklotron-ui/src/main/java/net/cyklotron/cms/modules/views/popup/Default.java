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
package net.cyklotron.cms.modules.views.popup;

import net.cyklotron.cms.site.SiteService;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.builders.AbstractBuilder;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.builders.EnclosingView;
import org.objectledge.web.mvc.finders.MVCFinder;

/**
 * A default view.
 *  
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Default.java,v 1.6 2005-08-08 09:07:24 rafal Exp $
 */
public class Default extends AbstractBuilder
{
    /** the finder */
    private MVCFinder mvcFinder;
    
    public Default(Context context, MVCFinder mvcFinder, 
        SiteService siteService)
    {
        super(context);
        this.mvcFinder = mvcFinder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(TemplatingContext templatingContext)
    {
        templatingContext.put("mvc_context", MVCContext.getMVCContext(context));
        templatingContext.put("http_context", HttpContext.getHttpContext(context));
    }
    
    /**
     * {@inheritDoc}
     */
    public EnclosingView getEnclosingView(String thisViewName)
    {
        if("popup.Default".equals(thisViewName))
        {
            return new EnclosingView("Page");
        }
        return EnclosingView.DEFAULT;
    }
}
