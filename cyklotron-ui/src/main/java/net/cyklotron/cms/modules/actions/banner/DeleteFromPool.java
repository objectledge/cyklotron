package net.cyklotron.cms.modules.actions.banner;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
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
import net.cyklotron.cms.banner.PoolResource;
import net.cyklotron.cms.banner.PoolResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteFromPool.java,v 1.2 2005-01-24 10:27:29 pablo Exp $
 */
public class DeleteFromPool
    extends BaseBannerAction
{
    

    public DeleteFromPool(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, BannerService bannerService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, bannerService, workflowService);
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
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
            ResourceList banners = poolResource.getBanners();
            banners.remove(bannerResource);
            poolResource.setBanners(banners);
            poolResource.update();
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
}


