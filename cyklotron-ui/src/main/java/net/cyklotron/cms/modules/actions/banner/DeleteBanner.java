package net.cyklotron.cms.modules.actions.banner;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.banner.BannerException;
import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannerResourceImpl;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteBanner.java,v 1.1 2005-01-24 04:34:40 pablo Exp $
 */
public class DeleteBanner
    extends BaseBannerAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        int bid = parameters.getInt("bid", -1);
        if(bid == -1)
        {
            throw new ProcessingException("Banners root id not found");
        }
        try
        {
            BannerResource bannerResource = BannerResourceImpl.getBannerResource(coralSession,bid);
            bannerService.deleteBanner(bannerResource);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        catch(BannerException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
}
