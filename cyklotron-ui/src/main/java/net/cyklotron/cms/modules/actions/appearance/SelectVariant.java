package net.cyklotron.cms.modules.actions.appearance;

import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.ComponentVariantResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

public class SelectVariant
    extends BaseAppearanceAction
{
    protected PreferencesService preferencesService;

    protected SiteService siteService;

    public SelectVariant()
    {
        preferencesService = (PreferencesService)broker.getService(PreferencesService.SERVICE_NAME);
        siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData(context);
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
		String app = preferences.get("component."+instance+".app").
			asString(null);
		String component = preferences.get("component."+instance+".class").
			asString(null);
		String currentVariant = preferences.get("component."+instance+".variant."+
			app+"."+component.replace(',','.'),"Default");

        String newVariant  = parameters.get("selected","Default");

        if(currentVariant.equals(newVariant))
        {
            return;
        }

        ComponentVariantResource[] variants;
        try
        {
            String skin = skinService.getCurrentSkin(site);
            variants = skinService.getComponentVariants(site, skin, app, component);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve variant information");
        }

        boolean variantExists = false;
        for(int i=0; i<variants.length; i++)
        {
            if(variants[i].getName().equals(newVariant))
            {
                variantExists = true;
                break;
            }
        }

        if(variantExists)
        {
            if(cmsData.getNode() != null)
            {
                preferences = preferencesService.getNodePreferences(node);
            }
            preferences.set("component."+instance+".variant."+
	       		app+"."+component.replace(',','.'), newVariant);
            if(cmsData.getNode() != null)
            {
                node.update(coralSession.getUserSubject());
            }
        }
        else
        {
            throw new ProcessingException("cannot set a non existant variant");
        }
    }
}
