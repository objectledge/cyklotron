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
 * @version $Id: DeleteFile.java,v 1.1 2005-01-24 04:34:04 pablo Exp $
 */
public class DeleteFile extends BaseAppearanceAction
{

    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        String path = parameters.get("path");
        String skin = parameters.get("skin");
        path = path.replace(',', '/');
        try
        {
            skinService.deleteContentFile(getSite(context), skin, path);
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
        if(context.containsKey("result"))
        {
            data.setView("appearance,skin,DeleteFile");
        }
        else
        {
            templatingContext.put("result","deleted_successfully");
        }
    }
}
