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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpUtils;

import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;

import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.RequestTrackingValve;

/**
 * A default view.
 *  
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Error.java,v 1.1 2008-10-02 15:45:23 rafal Exp $
 */
public class Error extends BaseCmsRedirectedView
{    
    private static final String TIME_FORMAT_PATTERN = "yyyy/MM/dd HH:mm:ss";
    
    public Error(Context context, SiteService siteService,
        PreferencesService preferencesService, SkinService skinService)
    {
        super(context, siteService, preferencesService, skinService);
    }

    @Override
    public void process(TemplatingContext templatingContext)
        throws ProcessingException
    {
         super.process(templatingContext);

         String requestMarker = (String) context.getAttribute(RequestTrackingValve.REQUEST_MARKER_KEY);
         templatingContext.put("requestMarker", requestMarker);
         HttpContext httpContext = context.getAttribute(HttpContext.class);
         StringBuffer buff = httpContext.getRequest().getRequestURL();
         String query = httpContext.getRequest().getQueryString();
         if(query != null)
         {
             buff.append("?").append(query);
         }
         templatingContext.put("url", buff.toString());
         DateFormat f = new SimpleDateFormat(TIME_FORMAT_PATTERN);
         templatingContext.put("time", f.format(new Date()));         
    }
}
