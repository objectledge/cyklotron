package net.cyklotron.cms.modules.actions.appearance.style;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.picocontainer.Parameter;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;

public class AddLevel
    extends BaseAppearanceAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
    {
        int levelCount = parameters.getInt("level_count");
        parameters.add("level_"+levelCount, 0);
        parameters.set("level_count", levelCount+1);
    }
}
