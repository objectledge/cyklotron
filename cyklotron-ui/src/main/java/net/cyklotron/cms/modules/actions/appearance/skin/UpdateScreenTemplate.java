package net.cyklotron.cms.modules.actions.appearance.skin;

import java.io.StringReader;
import java.io.StringWriter;

import net.labeo.services.templating.Context;
import net.labeo.services.templating.MergingException;
import net.labeo.services.templating.TemplatingService;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: UpdateScreenTemplate.java,v 1.1 2005-01-24 04:34:04 pablo Exp $
 */
public class UpdateScreenTemplate extends BaseAppearanceAction
{
    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        String skin = parameters.get("skin");
        String app = parameters.get("appName");
        String screen = parameters.get("screenName");
        String variant = parameters.get("variant","Default");
        String state = parameters.get("state","Default");
        ApplicationResource appRes = integrationService.getApplication(app);
        ScreenResource screenRes = integrationService.getScreen(appRes, 
            screen);
        String contents = parameters.get("contents");            
        try
        {
            skinService.setScreenTemplateContents(getSite(context), skin, screenRes.getApplicationName(),
                screenRes.getScreenName(), variant, state, contents);
            
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
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
        if(context.containsKey("result"))
        {
            data.setView("appearance,skin,EditScreenTemplate");
        }
        else
        {
            templatingContext.put("result","updated_successfully");
        }
    }
}
