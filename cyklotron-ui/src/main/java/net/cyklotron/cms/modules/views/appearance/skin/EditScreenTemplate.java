package net.cyklotron.cms.modules.views.appearance.skin;

import java.io.StringReader;
import java.io.StringWriter;

import net.labeo.services.templating.Context;
import net.labeo.services.templating.MergingException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.ScreenVariantResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: EditScreenTemplate.java,v 1.2 2005-01-24 10:27:20 pablo Exp $
 */
public class EditScreenTemplate extends BaseAppearanceScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            String skin = parameters.get("skin");
            String app = parameters.get("appName");
            String screen = parameters.get("screenName");
            String variant =
                parameters.get("variant","Default");
            String state =
                parameters.get("state","Default");
            templatingContext.put("skin", skin);
            templatingContext.put("appName", app);
            templatingContext.put("screenName", screen);
            templatingContext.put("variant", variant);
            if (parameters.isDefined("state"))
            {
                templatingContext.put("state", state);
            }
            ApplicationResource appRes = integrationService.getApplication(app);
            ScreenResource screenRes =
                integrationService.getScreen(appRes, screen);
            ScreenVariantResource variantRes =
                skinService.getScreenVariant(
                    site,
                    skin,
                    screenRes.getApplicationName(),
                    screenRes.getScreenName(),
                    variant);
            templatingContext.put("variant_description", variantRes.getDescription());
            String fileName =
                skinService.getTemplateFilename(
                    screenRes.getScreenName(),
                    variant,
                    state);
            templatingContext.put("filename", fileName);

            try
            {
                String contents =
                    skinService.getScreenTemplateContents(
                        site,
                        skin,
                        screenRes.getApplicationName(),
                        screenRes.getScreenName(),
                        variant,
                        state);
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
                            "<screen template>");
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
                throw new ProcessingException(
                    "failed to load file contents",
                    e);
            }
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);
        }
    }
}
