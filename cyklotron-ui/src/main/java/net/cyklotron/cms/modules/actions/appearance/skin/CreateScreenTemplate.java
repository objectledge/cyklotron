package net.cyklotron.cms.modules.actions.appearance.skin;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;

import net.labeo.services.templating.Context;
import net.labeo.services.templating.MergingException;
import net.labeo.services.templating.TemplatingService;
import net.labeo.services.upload.UploadContainer;
import net.labeo.services.upload.UploadService;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CreateScreenTemplate.java,v 1.1 2005-01-24 04:34:04 pablo Exp $
 */
public class CreateScreenTemplate extends BaseAppearanceAction
{
    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        UploadService uploadService = (UploadService)data.getBroker().
            getService(UploadService.SERVICE_NAME);
        Context context = data.getContext();

        String skin = parameters.get("skin");
        String app = parameters.get("appName");
        String screen = parameters.get("screenName");
        String variant = parameters.get("variant","Default");
        String state = parameters.get("state","Default");
        ApplicationResource appRes = integrationService.getApplication(app);
        ScreenResource screenRes = integrationService.getScreen(appRes, 
            screen);        
        String source = parameters.get("source","app");
        UploadContainer file = uploadService.getItem(data, "file");
        SiteResource site = getSite(context);
        try
        {
            String contents = null;
            if(source.equals("file"))
            {
                if(file == null)
                {
                    templatingContext.put("result","file_not_selected");
                }
                else
                {
                    contents = file.getString();
                }
            }
            else if(source.equals("app"))
            {
                Locale locale = StringUtils.getLocale(parameters.
                    get("locale"));
                contents = skinService.getScreenTemplateContents(screenRes.getApplicationName(), 
                    screenRes.getScreenName(), state, locale);
            }
            else if(source.equals("def_variant"))
            {
                contents = skinService.getScreenTemplateContents(site, skin, 
                    screenRes.getApplicationName(), screenRes.getScreenName(), 
                    "Default", state);
            }
            else
            {
                contents = "";
            }                

            skinService.createScreenTemplate(site, skin,
                screenRes.getApplicationName(), screenRes.getScreenName(), 
                variant, state, contents);

            TemplatingService templatingService = (TemplatingService)data.getBroker().
                getService(TemplatingService.SERVICE_NAME);
            Context blankContext = templatingService.createContext();
            StringReader in = new StringReader(contents);
            StringWriter out = new StringWriter();
            try
            {
                templatingService.merge("", blankContext, in, out, "<screen template>");
            }
            catch(MergingException e)
            {
                templatingContext.put("result", "template_saved_parse_error");
                templatingContext.put("parse_trace", StringUtils.stackTrace(e.getRootCause()));
                data.setView("appearance,skin,EditScreenTemplate");
                return;                
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
        if(context.containsKey("result"))
        {
            data.setView("appearance,skin,CreateScreenTemplate");
        }
        else
        {
            templatingContext.put("result","added_successfully");
        }
   }
}
