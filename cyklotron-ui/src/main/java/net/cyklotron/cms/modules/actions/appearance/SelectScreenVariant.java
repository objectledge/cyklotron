package net.cyklotron.cms.modules.actions.appearance;

import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.ScreenVariantResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

public class SelectScreenVariant
	extends BaseAppearanceAction
{
	protected PreferencesService preferencesService;

	public SelectScreenVariant()
	{
		preferencesService = (PreferencesService)broker.getService(PreferencesService.SERVICE_NAME);
	}

	public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
		throws ProcessingException
	{
		SiteResource site = getSite(context);
		NavigationNodeResource node = getNode(context);
		Parameters prefs = preferencesService.getCombinedNodePreferences(node);
		String app = prefs.get("screen.app",null);
		String screen = prefs.get("screen.class",null);
        String screenVariantKey = "screen.variant."+app+"."+screen.replace(',','.');
		String currentVariant = prefs.get(screenVariantKey,"Default");
		String newVariant  = parameters.get("selected","Default");

		if(currentVariant.equals(newVariant))
		{
			return;
		}

		ScreenVariantResource[] variants;
		try
		{
			String skin = skinService.getCurrentSkin(site);
			variants = skinService.getScreenVariants(site, skin, app, screen);
		}
		catch(Exception e)
		{
			throw new ProcessingException("failed to retrieve variant information");
		}

		boolean variantExists = false;
		for(int i=0; i<variants.length; i++)
		{
			if(variants[i].getName().equals(currentVariant))
			{
				variantExists = true;
				break;
			}
		}

		if(variantExists)
		{
			prefs = preferencesService.getNodePreferences(node);
			prefs.set(screenVariantKey, newVariant);
			node.update(coralSession.getUserSubject());
		}
		else
		{
			throw new ProcessingException("cannot set a non existant variant");
		}
	}
}
