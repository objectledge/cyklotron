package net.cyklotron.cms.modules.views.appearance.style;

import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;

/**
 * 
 *
 * @author <a href="mailto:mover@ngo.pl">Michal Mach</a>
 */
public class AddStyle
    extends BaseAppearanceScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            Resource root = styleService.getStyleRoot(site);
            //String rootId = ""+root.getId();
            Resource[] resources = coralSession.getStore().getResource(root);
            if(resources.length != 1)
            {
                throw new ProcessingException("Default style not found nor unique");
            }
            templatingContext.put("style_id", resources[0].getIdString());
        }
        catch (Exception e)
        {
            log.error("Error occured while fetching styleRoot ",e);
            throw new ProcessingException("Error occured while fetching styleRoot ",e);
        }
    }
}
