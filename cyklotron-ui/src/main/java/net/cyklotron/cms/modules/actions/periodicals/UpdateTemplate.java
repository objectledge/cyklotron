/*
 * Created on Oct 29, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.actions.periodicals;

import java.io.StringReader;
import java.io.StringWriter;

import net.labeo.services.templating.Context;
import net.labeo.services.templating.MergingException;
import net.labeo.services.templating.TemplatingService;
import net.labeo.services.webcore.NotFoundException;
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
public class UpdateTemplate extends BasePeriodicalsAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        SiteResource site = getSite(context);
        String renderer = parameters.get("renderer");
        String name = parameters.get("name");
        String contents = parameters.get("contents");
        try
        {
            periodicalsService.setTemplateVariantContents(site, renderer, name, contents);

            TemplatingService templatingService = (TemplatingService)data.getBroker().
                getService(TemplatingService.SERVICE_NAME);
            Context blankContext = templatingService.createContext();
            StringReader in = new StringReader(contents);
            StringWriter out = new StringWriter();
            try
            {
                templatingService.merge("", blankContext, in, out, "<component template>");
            }
            catch(MergingException e)
            {
                templatingContext.put("result", "template_saved_parse_error");
                templatingContext.put("parse_trace", StringUtils.stackTrace(e.getRootCause()));                
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
        if(context.containsKey("result"))
        {
            data.setView("periodicals,EditTemplate");
        }
        else
        {
            templatingContext.put("result","updated_successfully");
        }
    }
}
