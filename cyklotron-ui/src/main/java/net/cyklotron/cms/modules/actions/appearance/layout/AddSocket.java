package net.cyklotron.cms.modules.actions.appearance.layout;

import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;

public class AddSocket
    extends BaseAppearanceAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
    {
        int socketCount = parameters.getInt("socket_count");
        parameters.add("socket_"+socketCount, "");
        parameters.set("socket_count", socketCount+1);
    }
}
