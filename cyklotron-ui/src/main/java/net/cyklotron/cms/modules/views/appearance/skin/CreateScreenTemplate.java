package net.cyklotron.cms.modules.views.appearance.skin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.labeo.services.templating.Context;
import net.labeo.services.webcore.WebcoreService;
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
 * @version $Id: CreateScreenTemplate.java,v 1.2 2005-01-24 10:27:20 pablo Exp $
 */
public class CreateScreenTemplate extends BaseAppearanceScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) 
        throws ProcessingException
    {
        try
        {
            String skin = parameters.get("skin");
            String app = parameters.get("appName");
            String screen = parameters.get("screenName");
            String variant = parameters.get("variant","Default");
            String state = parameters.get("state","Default");
            SiteResource site = getSite();
            templatingContext.put("skin", skin);
            templatingContext.put("appName", app);
            templatingContext.put("screenName", screen);
            templatingContext.put("variant", variant);
            if(parameters.isDefined("state"))
            {
                templatingContext.put("state", state);
            }
            ApplicationResource appRes = integrationService.getApplication(app);
            ScreenResource screenRes = integrationService.getScreen(appRes, 
                screen);
            ScreenVariantResource variantRes = skinService.
                getScreenVariant(site, skin, screenRes.getApplicationName(), 
                screenRes.getScreenName(), variant);
            templatingContext.put("variant_description", variantRes.getDescription());
            List supported = skinService.getScreenTemplateLocales(screenRes.getApplicationName(), 
                screenRes.getScreenName(), state);
            WebcoreService webcoreService = (WebcoreService)data.getBroker().
                getService(WebcoreService.SERVICE_NAME);
            Map locales = new HashMap();
            for(Iterator i = supported.iterator(); i.hasNext();)
            {
                Locale l = (Locale)i.next();
                locales.put(l, webcoreService.getLocaleDescription(l));
            }
            templatingContext.put("locales", locales);
            if(skinService.hasScreenTemplate(site, skin, 
                screenRes.getApplicationName(), screenRes.getScreenName(), 
                "Default", state))
            {
                templatingContext.put("def_variant_present", Boolean.TRUE);
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retreive information", e);
        }
    }
}
