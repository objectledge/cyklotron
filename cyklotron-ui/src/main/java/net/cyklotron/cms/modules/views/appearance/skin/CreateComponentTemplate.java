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
import net.cyklotron.cms.integration.ComponentResource;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.ComponentVariantResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CreateComponentTemplate.java,v 1.3 2005-01-25 11:23:41 pablo Exp $
 */
public class CreateComponentTemplate extends BaseAppearanceScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) 
        throws ProcessingException
    {
        try
        {
            String skin = parameters.get("skin");
            String app = parameters.get("appName");
            String component = parameters.get("compName");
            String variant = parameters.get("variant","Default");
            String state = parameters.get("state","Default");
            SiteResource site = getSite();
            templatingContext.put("skin", skin);
            templatingContext.put("appName", app);
            templatingContext.put("compName", component);
            templatingContext.put("variant", variant);
            if(parameters.isDefined("state"))
            {
                templatingContext.put("state", state);
            }
            ApplicationResource appRes = integrationService.getApplication(app);
            ComponentResource compRes = integrationService.getComponent(appRes, 
                component);
            ComponentVariantResource variantRes = skinService.
                getComponentVariant(site, skin, compRes.getApplicationName(), 
                compRes.getComponentName(), variant);
            templatingContext.put("variant_description", variantRes.getDescription());
            List supported = skinService.getComponentTemplateLocales(compRes.getApplicationName(), 
                compRes.getComponentName(), state);
            WebcoreService webcoreService = (WebcoreService)data.getBroker().
                getService(WebcoreService.SERVICE_NAME);
            Map locales = new HashMap();
            for(Iterator i = supported.iterator(); i.hasNext();)
            {
                Locale l = (Locale)i.next();
                locales.put(l, webcoreService.getLocaleDescription(l));
            }
            templatingContext.put("locales", locales);
            if(skinService.hasComponentTemplate(site, skin, 
                compRes.getApplicationName(), compRes.getComponentName(), 
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
