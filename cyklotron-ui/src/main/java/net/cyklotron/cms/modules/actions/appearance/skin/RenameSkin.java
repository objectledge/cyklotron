package net.cyklotron.cms.modules.actions.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: RenameSkin.java,v 1.1 2005-01-24 04:34:04 pablo Exp $
 */
public class RenameSkin extends BaseAppearanceAction
{
    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        String skin = parameters.get("skin");
        String newName = parameters.get("new_name");
        SiteResource site = getSite(context);
        try
        {
            if(skinService.hasSkin(site, newName))
            {
                templatingContext.put("result","skin_exists");
            }
            else
            {
                SkinResource skinRes = skinService.getSkin(site, skin);
                skinService.renameSkin(skinRes, newName);           
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
        if(context.containsKey("result"))
        {
            data.setView("appearance,skin,RenameSkin");
        }
        else
        {
            templatingContext.put("result","renamed_successfully");
        }
    }
}
