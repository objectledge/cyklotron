package net.cyklotron.cms.modules.actions.banner;

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
import net.cyklotron.cms.banner.BannerException;
import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannerResourceImpl;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteBanner.java,v 1.2 2005-01-24 10:27:29 pablo Exp $
 */
public class DeleteBanner
    extends BaseBannerAction
{
    
    public DeleteBanner(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, BannerService bannerService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, bannerService, workflowService);
        // TODO Auto-generated constructor stub
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        int bid = parameters.getInt("bid", -1);
        if(bid == -1)
        {
            throw new ProcessingException("Banners root id not found");
        }
        try
        {
            BannerResource bannerResource = BannerResourceImpl.getBannerResource(coralSession,bid);
            bannerService.deleteBanner(coralSession, bannerResource);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        catch(BannerException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
}
