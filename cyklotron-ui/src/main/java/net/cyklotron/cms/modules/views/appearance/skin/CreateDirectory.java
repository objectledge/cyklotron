package net.cyklotron.cms.modules.views.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CreateDirectory.java,v 1.1 2005-01-24 04:34:19 pablo Exp $
 */
public class CreateDirectory extends BaseAppearanceScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
    {
        String skin = parameters.get("skin");
        String path = parameters.get("path");
        templatingContext.put("skin", skin);
        templatingContext.put("path", path);
        templatingContext.put("path_slashes", path.replace(',','/'));
    }
}
