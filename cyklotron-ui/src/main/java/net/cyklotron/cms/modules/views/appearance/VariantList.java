package net.cyklotron.cms.modules.views.appearance;

import java.util.Arrays;

import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Parameters;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.ComponentVariantResource;
import net.cyklotron.cms.skins.SkinException;
import net.cyklotron.cms.structure.NavigationNodeResource;

public class VariantList
    extends BaseAppearanceScreen
{
    protected PreferencesService preferencesService;
    
    protected SiteService siteService;

    public VariantList()
    {
        preferencesService = (PreferencesService)broker.getService(PreferencesService.SERVICE_NAME);
        siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        SiteResource site = cmsData.getSite();
        NavigationNodeResource node = cmsData.getNode();
        Parameters preferences;
        if(node != null)
        {
            preferences = preferencesService.getCombinedNodePreferences(node);
        }
        else
        {
            preferences = preferencesService.getSystemPreferences();
            String dataSite = preferences.get("globalComponentsData","");
            try
            {
                site = siteService.getSite(dataSite);
            }
            catch(SiteException e)
            {
                throw new ProcessingException("failed to lookup global components data site");
            }
        }
        String instance = parameters.get("component_instance");
        templatingContext.put("component_instance", instance);

        String app = preferences.get("component."+instance+".app").
        	asString(null);
        String component = preferences.get("component."+instance+".class").
        	asString(null);
        String variant = preferences.get("component."+instance+".variant."+
        	app+"."+component.replace(',','.'),"Default");
        templatingContext.put("current_name", variant);

        String skin;
        try
        {
            skin = skinService.getCurrentSkin(site);
        }
        catch(SkinException e)
        {
            throw new ProcessingException("failed to retrieve skin information", e);
        }

        try
        {
            ComponentVariantResource[] variants =
                            skinService.getComponentVariants(site, skin, app, component);
            templatingContext.put("variants", Arrays.asList(variants));
            
            for(int i=0; i<variants.length; i++)
            {
                if(variants[i].getName().equals(variant))
                {
                    templatingContext.put("current_variant", variants[i]);
                    break;
                }
            }
        }
        catch(SkinException e)
        {
            // WARN: silent fail - do not display variant info
        }
    }
}
