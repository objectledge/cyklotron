package net.cyklotron.cms.modules.actions.banner;

import java.io.IOException;

import net.labeo.Labeo;
import net.labeo.services.ServiceBroker;
import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.TemplateAction;

import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannerResourceImpl;
import net.cyklotron.cms.banner.BannerService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: FollowBanner.java,v 1.1 2005-01-24 04:34:40 pablo Exp $
 */
public class FollowBanner
    extends TemplateAction
{
    /** service broker */
    protected ServiceBroker broker;

    /** logging facility */
    protected Logger log;

    /** banner service */
    protected BannerService bannerService;

    /** resource service */
    protected CoralSession coralSession;


    public FollowBanner()
    {
        broker = Labeo.getBroker();
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("banner");
        bannerService = (BannerService)broker.getService(BannerService.SERVICE_NAME);
        coralSession = (CoralSession)broker.getService(CoralSession.SERVICE_NAME);
    }


    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        //Subject subject = coralSession.getUserSubject();
        int bid = parameters.getInt("bid", -1);
        if(bid == -1)
        {
            throw new ProcessingException("Banner id not found");
        }
        try
        {
            BannerResource bannerResource = BannerResourceImpl.getBannerResource(coralSession,bid);
            bannerService.followBanner(bannerResource);
            String target = bannerResource.getTarget();
            data.getResponse().sendRedirect(target);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        catch(IOException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
}
