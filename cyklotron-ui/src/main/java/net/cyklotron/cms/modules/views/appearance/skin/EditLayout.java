package net.cyklotron.cms.modules.views.appearance.skin;

import java.io.StringReader;
import java.io.StringWriter;

import net.labeo.services.templating.Context;
import net.labeo.services.templating.MergingException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: EditLayout.java,v 1.2 2005-01-24 10:27:20 pablo Exp $
 */
public class EditLayout extends BaseAppearanceScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        String skin = parameters.get("skin");
        String layout = parameters.get("layout");
        templatingContext.put("skin", skin);
        templatingContext.put("layout", layout);
        SiteResource site = getSite();
        try
        {
            String contents =
                skinService.getLayoutTemplateContents(site, skin, layout);
            templatingContext.put("contents", contents);
            if(!templatingContext.containsKey("result"))
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
                        "<layout template>");
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
        catch (Exception e)
        {
            throw new ProcessingException("failed to load file contents", e);
        }
    }
}
