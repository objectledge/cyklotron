package net.cyklotron.cms.modules.actions.appearance.layout;

import net.labeo.util.configuration.Parameter;
import net.labeo.util.configuration.Parameters;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;

public class RemoveSocket
    extends BaseAppearanceAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
    {
        Parameters pc = parameters;
        int socketCount = pc.get("socket_count").asInt();
        int socket = pc.get("socket").asInt();
        for(int i=socket; i<socketCount; i++)
        {
            Parameter p = pc.get("socket_"+(i+1));
            pc.set("socket_"+i, p);
        }
        pc.remove("socket_"+socketCount);
        pc.set("socket_count", socketCount-1);
    }
}
