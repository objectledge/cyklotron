package net.cyklotron.cms.modules.actions.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.StyleException;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: UpdateLayout.java,v 1.1 2005-01-24 04:34:04 pablo Exp $
 */
public class UpdateLayout extends BaseAppearanceAction
{
    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        String layout = parameters.get("layout");
        String skin = parameters.get("skin");
        String contents = parameters.get("contents");
        SiteResource site = getSite(context);
        try
        {
            skinService.setLayoutTemplateContents(getSite(context), skin, layout, contents);
            String[] templateSockets = null;
            try
            {
                templateSockets = styleService.findSockets(contents);
            }
            catch(StyleException e)
            {
                templatingContext.put("result", "template_saved_parse_error");
                templatingContext.put("parse_trace", StringUtils.stackTrace(e.getRootCause()));
            }
            if(templateSockets != null)
            {
                LayoutResource layoutRes = styleService.getLayout(site, layout);
                if(!styleService.matchSockets(layoutRes, templateSockets))
                {
                    templatingContext.put("result", "template_saved_sockets_mismatch");
                    data.setView("appearance,skin,ValidateLayout");
                    return;
                }
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
        if(context.containsKey("result"))
        {
            data.setView("appearance,skin,EditLayout");
        }
        else
        {
            templatingContext.put("result","updated_successfully");
        }
    }
}
