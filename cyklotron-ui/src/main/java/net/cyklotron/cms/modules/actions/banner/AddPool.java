package net.cyklotron.cms.modules.actions.banner;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.BannersResource;
import net.cyklotron.cms.banner.BannersResourceImpl;
import net.cyklotron.cms.banner.PoolResource;
import net.cyklotron.cms.banner.PoolResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddPool.java,v 1.4 2005-06-13 11:08:32 rafal Exp $
 */
public class AddPool
    extends BaseBannerAction
{
    private final CoralSessionFactory coralSessionFactory;
    
    public AddPool(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
        BannerService bannerService, WorkflowService workflowService, 
        CoralSessionFactory coralSessionFactory)
    {
        super(logger, structureService, cmsDataFactory, bannerService, workflowService);
        this.coralSessionFactory = coralSessionFactory;
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();

        String title = parameters.get("title","");
        String description = parameters.get("description","");
        if(title.length() < 1 || title.length() > 32)
        {
            templatingContext.put("result","invalid_title");
            return;
        }
        if(description.length() > 256)
        {
            templatingContext.put("result","invalid_description");
            return;
        }

        int bsid = parameters.getInt("bsid", -1);
        if(bsid == -1)
        {
            throw new ProcessingException("Banners root id not found");
        }

        try
        {
            BannersResource bannersRoot = BannersResourceImpl
                .getBannersResource(coralSession, bsid);
            PoolResource poolResource = PoolResourceImpl.createPoolResource(coralSession, title,
                bannersRoot);
            poolResource.setDescription(description);
            poolResource.setBanners(new ResourceList(coralSessionFactory));
            poolResource.update();
        }
        catch(InvalidResourceNameException e)
        {
            templatingContext.put("result", "invalid_name");
            return;
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        templatingContext.put("result","added_successfully");
    }
}


