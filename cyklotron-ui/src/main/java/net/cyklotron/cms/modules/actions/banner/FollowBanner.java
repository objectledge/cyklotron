package net.cyklotron.cms.modules.actions.banner;

import java.io.IOException;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannerResourceImpl;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: FollowBanner.java,v 1.2 2005-01-24 10:27:29 pablo Exp $
 */
public class FollowBanner
    extends BaseCMSAction
{
    /** logging facility */
    protected Logger log;

    /** banner service */
    protected BannerService bannerService;

    
    public FollowBanner(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, BannerService bannerService)
    {
        super(logger, structureService, cmsDataFactory);
        this.bannerService = bannerService;
    }


    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        //Subject subject = coralSession.getUserSubject();
        int bid = parameters.getInt("bid", -1);
        if(bid == -1)
        {
            throw new ProcessingException("Banner id not found");
        }
        try
        {
            BannerResource bannerResource = BannerResourceImpl.getBannerResource(coralSession,bid);
            bannerService.followBanner(coralSession, bannerResource);
            String target = bannerResource.getTarget();
            httpContext.getResponse().sendRedirect(target);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        catch(IOException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
}
