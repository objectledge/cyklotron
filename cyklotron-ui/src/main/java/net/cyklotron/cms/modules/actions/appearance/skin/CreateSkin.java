package net.cyklotron.cms.modules.actions.appearance.skin;

import net.labeo.services.resource.Subject;
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
 * @version $Id: CreateSkin.java,v 1.1 2005-01-24 04:34:04 pablo Exp $
 */
public class CreateSkin extends BaseAppearanceAction
{

    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        String name = parameters.get("name");
        boolean copy = parameters.get("source","empty").
            equals("copy");
        String sourceSkin = parameters.get("source_skin");
        SiteResource site = getSite(context);
        Subject subject = coralSession.getUserSubject();
        try
        {
            if(name.length() == 0)
            {
                templatingContext.put("result", "empty_name");
            }
            else
            {
                if(skinService.hasSkin(site, name))
                {
                    templatingContext.put("result","skin_exists");
                }
                else
                {
                    if(copy)
                    {
                        SkinResource source = skinService.getSkin(site, sourceSkin);
                        skinService.createSkin(site, name, source, subject);      
                    }
                    else
                    {
                        skinService.createSkin(site, name, null, subject);
                    }
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
            data.setView("appearance,skin,CreateSkin");
        }
        else
        {
            templatingContext.put("result","added_successfully");
        }
    }
}
