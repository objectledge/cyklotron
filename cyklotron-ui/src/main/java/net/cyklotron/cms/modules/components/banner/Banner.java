package net.cyklotron.cms.modules.components.banner;

import net.labeo.services.logging.LoggingService;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.BannersResource;
import net.cyklotron.cms.banner.BannersResourceImpl;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;


/**
 * Banner component.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: Banner.java,v 1.1 2005-01-24 04:35:44 pablo Exp $
 */

public class Banner extends SkinableCMSComponent
{
    private BannerService bannerService;

    public Banner()
    {
        bannerService = (BannerService)broker.getService(BannerService.SERVICE_NAME);
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME))
            .getFacility(BannerService.LOGGING_FACILITY);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) throws ProcessingException
    {
        try
        {
            Parameters componentConfig = getConfiguration();
            BannersResource bannersRoot = null;

            long bannersRootId = componentConfig.get("banner.rootId").asLong(-1);
            if(bannersRootId != -1)
            {
                bannersRoot = BannersResourceImpl.getBannersResource(coralSession, bannersRootId);
            }
            else
            {
                if(getSite(context) != null)
                {
                    bannersRoot = bannerService.getBannersRoot(getSite(context));
                }
            }

            if(bannersRoot != null)
            {
                BannerResource bannerResource = bannerService.getBanner(bannersRoot, componentConfig);
                templatingContext.put("banner",bannerResource);
            }
            else
            {
                componentError(context, "No site selected");
            }
        }
        catch(Exception e)
        {
            componentError(context, "Exception", e);
        }
    }
}
