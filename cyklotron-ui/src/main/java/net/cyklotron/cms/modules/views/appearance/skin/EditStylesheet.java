package net.cyklotron.cms.modules.views.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: EditStylesheet.java,v 1.2 2005-01-25 11:23:41 pablo Exp $
 */
public class EditStylesheet extends BaseAppearanceScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        String skin = parameters.get("skin");
        templatingContext.put("skin", skin);
        SiteResource site = getSite();
        String path = "/style.css";
        try
        {
            templatingContext.put("contents", skinService.getContentFileContents(site, skin, path, data.getEncoding()));
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to load file contents", e);
        }
    }
}
