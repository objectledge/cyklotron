package net.cyklotron.cms.modules.views.appearance.layout;

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
public class AddLayout
    extends BaseAppearanceScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            Resource root = styleService.getLayoutRoot(site);
            String rootId = root.getIdString();
            templatingContext.put("root_id", rootId);
        }
        catch (Exception e)
        {
            log.error("Error occured while fetching layoutRoot ",e);
            throw new ProcessingException("Error occured while fetching layoutRoot ",e);
        }
    }
}
