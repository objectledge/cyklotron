package net.cyklotron.cms.modules.views.banner;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannerResourceImpl;

/**
 *
 */
public class Banner
    extends BaseBannerScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        long bid = parameters.getLong("bid", -1);
        if(bid == -1)
        {
            throw new ProcessingException("Banners id not found");
        }
        BannerResource banner = null;
        try
        {
            banner = BannerResourceImpl.getBannerResource(coralSession, bid);
            templatingContext.put("banner",banner);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("PollException: ",e);
            return;
        }
    }
}
