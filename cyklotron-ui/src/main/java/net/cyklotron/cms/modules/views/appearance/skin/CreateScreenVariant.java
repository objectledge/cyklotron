package net.cyklotron.cms.modules.views.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinException;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CreateScreenVariant.java,v 1.2 2005-01-25 11:23:41 pablo Exp $
 */
public class CreateScreenVariant extends BaseAppearanceScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            String skin = parameters.get("skin");
            String app = parameters.get("appName");
            SiteResource site = getSite();
            String screen = parameters.get("screenName");
            templatingContext.put("skin", skin);
            templatingContext.put("appName", app);
            templatingContext.put("screenName", screen);
            ApplicationResource appRes = integrationService.getApplication(app);
            ScreenResource screenRes = integrationService.getScreen(appRes, 
                screen);
            if(skinService.hasScreenVariant(site, skin, 
                screenRes.getApplicationName(), screenRes.getScreenName(), 
                "Default"))
            {
                templatingContext.put("default_exits", Boolean.TRUE);
            }
            else
            {
                templatingContext.put("default_exits", Boolean.FALSE);                
            }
        }
        catch (SkinException e)
        {
            throw new ProcessingException("failed to check variant info");
        }        
    }
}
