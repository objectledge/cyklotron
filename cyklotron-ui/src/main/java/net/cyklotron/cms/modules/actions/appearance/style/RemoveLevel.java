package net.cyklotron.cms.modules.actions.appearance.style;

import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;

public class RemoveLevel
    extends BaseAppearanceAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
    {
        int levelCount = parameters.getInt("level_count");
        parameters.remove("level_"+(levelCount-1), 0);
        parameters.set("level_count", levelCount-1);
    }
}
