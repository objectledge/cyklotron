package net.cyklotron.cms.modules.views.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinResource;
import net.cyklotron.cms.skins.SkinService;

public class SkinList
    extends BaseAppearanceScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            String current = skinService.getCurrentSkin(site);
            SkinResource[] skins = skinService.getSkins(site);
            String key = SkinService.PREVIEW_KEY_PREFIX + site.getName();
            String preview = (String)data.getGlobalContext().getAttribute(key);
            templatingContext.put("preview", preview);
            templatingContext.put("current", current);
            templatingContext.put("skins", skins);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve skin information");
        }
    }
}
