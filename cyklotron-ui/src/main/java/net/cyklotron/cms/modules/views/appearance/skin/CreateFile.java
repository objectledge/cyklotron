package net.cyklotron.cms.modules.views.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CreateFile.java,v 1.2 2005-01-25 11:23:41 pablo Exp $
 */
public class CreateFile extends BaseAppearanceScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
    {
        String skin = parameters.get("skin");
        String path = parameters.get("path");
        templatingContext.put("skin", skin);
        templatingContext.put("path", path);
        templatingContext.put("path_slashes", path.replace(',','/'));
    }
}
