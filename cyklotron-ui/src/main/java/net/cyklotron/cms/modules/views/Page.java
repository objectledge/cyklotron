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
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.builders.AbstractBuilder;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.builders.EnclosingView;
import org.objectledge.web.mvc.finders.MVCFinder;

/**
 * A default page.
 *  
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Page.java,v 1.3.6.1 2005-07-26 12:15:52 rafal Exp $
 */
public class Page extends AbstractBuilder
{
    /** the finder */
    private MVCFinder mvcFinder;
    
    public Page(Context context, MVCFinder mvcFinder)
    {
        super(context);
        this.mvcFinder = mvcFinder;
    }

    /**
     * {@inheritDoc}
     */
    public EnclosingView getEnclosingView(String thisViewName)
    {
        return EnclosingView.TOP;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void process(TemplatingContext templatingContext)
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        templatingContext.put("pageEncoding", httpContext.getEncoding());
        templatingContext.put("pageContentType", httpContext.getContentType());        
    }
}
