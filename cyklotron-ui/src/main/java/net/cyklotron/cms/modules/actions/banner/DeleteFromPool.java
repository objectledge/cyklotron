package net.cyklotron.cms.modules.actions.banner;

import java.util.List;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannerResourceImpl;
import net.cyklotron.cms.banner.PoolResource;
import net.cyklotron.cms.banner.PoolResourceImpl;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteFromPool.java,v 1.1 2005-01-24 04:34:40 pablo Exp $
 */
public class DeleteFromPool
    extends BaseBannerAction
{

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        int bid = parameters.getInt("bid", -1);
        int pid = parameters.getInt("pid", -1);
        if(bid == -1 || pid == -1)
        {
            throw new ProcessingException("pool id nor banner id not found");
        }

        try
        {
            PoolResource poolResource = PoolResourceImpl.getPoolResource(coralSession, pid);
            BannerResource bannerResource = BannerResourceImpl.getBannerResource(coralSession, bid);
            List banners = poolResource.getBanners();
            banners.remove(bannerResource);
            poolResource.setBanners(banners);
            poolResource.update(subject);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
}


