/*
 * Created on Oct 29, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.views.periodicals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.site.SiteResource;


/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CreateTemplate
    extends BasePeriodicalsScreen
{
    private WebcoreService webcoreService;
    
    public CreateTemplate()
    {
        webcoreService = (WebcoreService)Labeo.getBroker().
            getService(WebcoreService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) 
        throws ProcessingException
    {
        String renderer = parameters.get("renderer");
        templatingContext.put("renderer", renderer);
        SiteResource site = getSite();
        String[] variants = periodicalsService.getTemplateVariants(site, renderer);
        templatingContext.put("variants", Arrays.asList(variants));
        Map locales = new HashMap();
        List provided = periodicalsService.getDefaultTemplateLocales(renderer);
        Iterator i = provided.iterator();
        while(i.hasNext())
        {
            Locale l = (Locale)i.next();
            locales.put(l, webcoreService.getLocaleDescription(l));
        }
        templatingContext.put("locales", locales);
    }
}
