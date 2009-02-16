package net.cyklotron.cms.modules.actions.banner;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.PoolResource;
import net.cyklotron.cms.banner.PoolResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateBannerPool.java,v 1.1 2005-06-15 12:08:46 pablo Exp $
 */
public class UpdateBannerPool
    extends BaseBannerAction
{
    private CoralSessionFactory coralSessionFactory;
    
    public UpdateBannerPool(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, BannerService bannerService, WorkflowService workflowService,
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
        if(description.length() > 255)
        {
            templatingContext.put("result","invalid_description");
            return;
        }

        int pid = parameters.getInt("pid", -1);
        if(pid == -1)
        {
            throw new ProcessingException("Poll id not found");
        }
        try
        {
            PoolResource poolResource = PoolResourceImpl.getPoolResource(coralSession, pid);
            if(!poolResource.getName().equals(title))
            {
                coralSession.getStore().setName(poolResource, title);
            }
            poolResource.setDescription(description);
            Resource[] resources = coralSession.getStore().getResource(poolResource.getParent());
            ResourceList banners = new ResourceList(coralSessionFactory);
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof BannerResource)
                {
                    if(parameters.isDefined("resource-"+resources[i].getId()))
                    {
                        banners.add(resources[i]);
                    }
                }
            }
            poolResource.setBanners(banners);
            poolResource.update();
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("PollException: ",e);
            return;
        }
        catch(InvalidResourceNameException e)
        {
            templatingContext.put("result","invalid_name");
            return;
        }
        templatingContext.put("result","updated_successfully");
    }
}


