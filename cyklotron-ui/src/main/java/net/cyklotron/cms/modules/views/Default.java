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
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.builders.AbstractBuilder;
import org.objectledge.web.mvc.builders.EnclosingView;

/**
 * A default view.
 *  
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Default.java,v 1.9 2007-11-18 21:24:44 rafal Exp $
 */
public class Default extends AbstractBuilder
{
    public Default(Context context)
    {
        super(context);
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
        if("Default".equals(thisViewName))
        {
            return new EnclosingView("Page");
        }
        return EnclosingView.DEFAULT;
    }
}
