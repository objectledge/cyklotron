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
 * @version $Id: CreateSkin.java,v 1.1 2005-01-24 04:34:19 pablo Exp $
 */
public class CreateSkin extends BaseAppearanceScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) 
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
