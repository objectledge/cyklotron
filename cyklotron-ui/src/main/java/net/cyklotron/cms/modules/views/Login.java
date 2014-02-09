package net.cyklotron.cms.modules.views;

import org.objectledge.context.Context;

import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;

public class Login
    extends BaseCmsRedirectedView
{

    public Login(Context context, SiteService siteService, PreferencesService preferencesService,
        SkinService skinService)
    {
        super(context, siteService, preferencesService, skinService);
    }

    @Override
    protected String viewToSystemScreen(String view)
    {
        return "AccessDenied";
    }
}
