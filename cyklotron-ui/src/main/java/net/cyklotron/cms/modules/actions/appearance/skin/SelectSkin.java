package net.cyklotron.cms.modules.actions.appearance.skin;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;

import org.objectledge.pipeline.ProcessingException;

public class SelectSkin
    extends BaseAppearanceAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            String selected = parameters.get("selected");
            SiteResource site = getSite(context);
            skinService.setCurrentSkin(site, selected);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to set skin", e);
        }
    }
}
