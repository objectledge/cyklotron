package net.cyklotron.cms.modules.views.appearance.skin;

import java.util.Arrays;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinException;
import net.cyklotron.cms.skins.SkinResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CreateSkin.java,v 1.2 2005-01-25 11:23:41 pablo Exp $
 */
public class CreateSkin extends BaseAppearanceScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) 
        throws ProcessingException
    {
        SiteResource site = getSite();
        try
        {
            SkinResource[] skins = skinService.getSkins(site);
            templatingContext.put("skins", Arrays.asList(skins));
        }
        catch (SkinException e)
        {
            throw new ProcessingException("failed to retrieve information", e);
        }
    }
}
