package net.cyklotron.cms.modules.actions.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.integration.ScreenStateResource;
import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DeleteScreenVariant.java,v 1.1 2005-01-24 04:34:04 pablo Exp $
 */
public class DeleteScreenVariant extends BaseAppearanceAction
{
    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        SiteResource site = getSite(context);
        String skin = parameters.get("skin");
        String app = parameters.get("appName");
        String screen = parameters.get("screenName");
        String variant =
            parameters.get("variant","Default");
        ApplicationResource appRes = integrationService.getApplication(app);
        ScreenResource screenRes =
            integrationService.getScreen(appRes, screen);
        try
        {
            ScreenStateResource[] states =
                integrationService.getScreenStates(screenRes);
            if (states.length > 0)
            {
                for (int i = 0; i < states.length; i++)
                {
                    if(skinService.hasScreenTemplate(
                        site,
                        skin,
                        screenRes.getApplicationName(),
                        screenRes.getScreenName(),
                        variant,
                        states[i].getName()))
                    {
                        skinService.deleteScreenTemplate(
                            site,
                            skin,
                            screenRes.getApplicationName(),
                            screenRes.getScreenName(),
                            variant,
                            states[i].getName());
                    }
                }
            }
            else
            {
                if(skinService.hasScreenTemplate(
                    site,
                    skin,
                    screenRes.getApplicationName(),
                    screenRes.getScreenName(),
                    variant,
                    "Default"))
                {
                    skinService.deleteScreenTemplate(
                        site,
                        skin,
                        screenRes.getApplicationName(),
                        screenRes.getScreenName(),
                        variant,
                        "Default");
                }
            }
            skinService.deleteScreenVariant(
                site,
                skin,
                screenRes.getApplicationName(),
                screenRes.getScreenName(),
                variant);
            }
        catch (Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
        if (context.containsKey("result"))
        {
            data.setView("appearance,skin,DeleteScreenVariant");
        }
        else
        {
            templatingContext.put("result", "deleted_successfully");
        }
    }
}
