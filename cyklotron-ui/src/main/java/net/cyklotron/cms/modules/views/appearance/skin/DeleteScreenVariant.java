package net.cyklotron.cms.modules.views.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.integration.ScreenStateResource;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.ScreenVariantResource;
import net.cyklotron.cms.skins.SkinException;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DeleteScreenVariant.java,v 1.2 2005-01-25 11:23:41 pablo Exp $
 */
public class DeleteScreenVariant extends BaseAppearanceScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        String skin = parameters.get("skin");
        String app = parameters.get("appName");
        SiteResource site = getSite();
        String screen = parameters.get("screenName");
        String variant = parameters.get("variant");
        templatingContext.put("skin", skin);
        templatingContext.put("appName", app);
        templatingContext.put("screenName", screen);        
        templatingContext.put("variant", variant);        
        ApplicationResource appRes = integrationService.getApplication(app);
        ScreenResource screenRes =
            integrationService.getScreen(appRes, screen);
        try
        {
            ScreenVariantResource variantRes =
                skinService.getScreenVariant(
                    site,
                    skin,
                    screenRes.getApplicationName(),
                    screenRes.getScreenName(),
                    variant);
            templatingContext.put("variant_description", variantRes.getDescription());
            
            ScreenStateResource[] states = integrationService.getScreenStates(screenRes); 
            if(states.length > 0)
            {
                boolean stateTemplatesPresent = false;
                for (int i = 0; i < states.length; i++)
                {
                    if (skinService
                        .hasScreenTemplate(
                            site,
                            skin,
                            screenRes.getApplicationName(),
                            screenRes.getScreenName(),
                            variant,
                            states[i].getName()))
                    {
                        stateTemplatesPresent = true;
                    }
                }
                if(stateTemplatesPresent)
                {
                    templatingContext.put("warn_stateful", Boolean.TRUE);
                }
            }
            else
            {
                if(skinService
                    .hasScreenTemplate(
                        site,
                        skin,
                        screenRes.getApplicationName(),
                        screenRes.getScreenName(),
                        variant,
                        "Default"))
                {
                    templatingContext.put("warn_stateless", Boolean.TRUE);
                }
            }
        }
        catch (SkinException e)
        {
            throw new ProcessingException("failed to look up information");            
        }
    }
}
