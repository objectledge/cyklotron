package net.cyklotron.cms.modules.actions.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.ScreenVariantResource;
import net.cyklotron.cms.skins.SkinException;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CreateScreenVariant.java,v 1.1 2005-01-24 04:34:04 pablo Exp $
 */
public class CreateScreenVariant extends BaseAppearanceAction
{
    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        SiteResource site = getSite(context);
        String skin = parameters.get("skin");        
        String appName = parameters.get("appName");
        String screenName = parameters.get("screenName");
        ApplicationResource appRes = integrationService.getApplication(appName);
        ScreenResource screenRes = integrationService.getScreen(appRes, 
            screenName);
        boolean defaultVariant = parameters.get("variant").
            asString("default").equals("default");
        String name = defaultVariant ? "Default" :
            parameters.get("name");
        String description = parameters.get("description");
        try
        {
            if(skinService.hasScreenVariant(site, skin, 
                screenRes.getApplicationName(), screenRes.getScreenName(), name))
            {
                templatingContext.put("result","variant_exists");
            }
            else
            {
                ScreenVariantResource variant = skinService.
                    createScreenVariant(site, skin, 
                    screenRes.getApplicationName(), screenRes.getScreenName(), 
                    name, coralSession.getUserSubject());
                variant.setDescription(description);
                variant.update(coralSession.getUserSubject());
            }
        }
        catch(SkinException e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
        if(context.containsKey("result"))
        {
            data.setView("appearance,skin,CreateScreenVariant");
        }
        else
        {
            templatingContext.put("result","added_successfully");
        }
    }
}
