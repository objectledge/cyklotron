package net.cyklotron.cms.modules.actions.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: UpdateStylesheet.java,v 1.1 2005-01-24 04:34:04 pablo Exp $
 */
public class UpdateStylesheet extends BaseAppearanceAction
{
    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        String path = "/style.css";
        String skin = parameters.get("skin");
        String contents = parameters.get("contents");
        try
        {
            skinService.setContentFileContents(getSite(context), skin, path, contents, data.getEncoding());
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
        if(context.containsKey("result"))
        {
            data.setView("appearance,skin,EditStylesheet");
        }
        else
        {
            templatingContext.put("result","updated_successfully");
        }
    }
}
