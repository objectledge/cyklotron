/*
 * Created on Oct 29, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.views.periodicals;

import java.io.StringReader;
import java.io.StringWriter;

import net.labeo.Labeo;
import net.labeo.services.templating.Context;
import net.labeo.services.templating.MergingException;
import net.labeo.services.templating.TemplatingService;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EditTemplate
    extends BasePeriodicalsScreen
{
    private TemplatingService templatingService;
    
    public EditTemplate()
    {
        templatingService = (TemplatingService)Labeo.getBroker().
            getService(TemplatingService.SERVICE_NAME);
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        SiteResource site = getSite();
        String renderer = parameters.get("renderer");
        String name = parameters.get("name");
        templatingContext.put("renderer", renderer);
        templatingContext.put("name", name);
        String contents = periodicalsService.getTemplateVariantContents(site, renderer, name);
        templatingContext.put("filename", name+".vt");
        if(!context.containsKey("result"))
        {
            Context blankContext = templatingService.createContext();
            StringReader in = new StringReader(contents);
            StringWriter out = new StringWriter();
            try
            {
                templatingService.merge(
                    "",
                    blankContext,
                    in,
                    out,
                    "<component template>");
            }
            catch (MergingException e)
            {
                templatingContext.put("result", "template_parse_error");
                templatingContext.put(
                    "parse_trace",
                    StringUtils.stackTrace(e.getRootCause()));
            }
        }
    }
}
