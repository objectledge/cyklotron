package net.cyklotron.cms.modules.views.appearance.skin;

import net.labeo.services.templating.Context;
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
 * @version $Id: DeleteComponentTemplate.java,v 1.3 2005-01-25 11:23:41 pablo Exp $
 */
public class DeleteComponentTemplate extends BaseAppearanceScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            String skin = parameters.get("skin");
            String app = parameters.get("appName");
            String component = parameters.get("compName");
            String variant =
                parameters.get("variant","Default");
            String state =
                parameters.get("state","Default");
            templatingContext.put("skin", skin);
            templatingContext.put("appName", app);
            templatingContext.put("compName", component);
            templatingContext.put("variant", variant);
            if (parameters.isDefined("state"))
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
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);
        }
    }
}
