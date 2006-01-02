package net.cyklotron.cms.modules.actions.banner;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.BannersResource;
import net.cyklotron.cms.banner.BannersResourceImpl;
import net.cyklotron.cms.banner.ExternalBannerResource;
import net.cyklotron.cms.banner.ExternalBannerResourceImpl;
import net.cyklotron.cms.banner.MediaBannerResource;
import net.cyklotron.cms.banner.MediaBannerResourceImpl;
import net.cyklotron.cms.banner.PoolResource;
import net.cyklotron.cms.banner.PoolResourceImpl;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.TransitionResource;
import net.cyklotron.cms.workflow.WorkflowService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import java.util.Date;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddBanner.java,v 1.6 2006-01-02 14:58:10 rafal Exp $
 */
public class AddBanner
    extends BaseBannerAction
{
    private CoralSessionFactory coralSessionFactory;
    
    public AddBanner(Logger logger, StructureService structureService,
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

        String srcType = parameters.get("src_type","external");
        String mediaPath = parameters.get("int_src","");

        int bsid = parameters.getInt("bsid", -1);
        if(bsid == -1)
        {
            throw new ProcessingException("Banners root id not found");
        }

        String title = parameters.get("title","");
        String description = parameters.get("description","");

        if(title.length() < 1 || title.length() > 64)
        {
            route(mvcContext, templatingContext, "banner.AddBanner", "invalid_title");
            return;
        }
        if(description.length() > 256)
        {
            route(mvcContext, templatingContext, "banner.AddBanner", "invalid_description");
            return;
        }

        String altText = parameters.get("alt_text","");
        String target = parameters.get("target","");

        // time stuff
        long startTime = parameters.getLong("start_time", 0);
        long endTime = parameters.getLong("end_time", 0);
        Date start = new Date(startTime);
        Date end = new Date(endTime);

        try
        {
            BannersResource bannersRoot = BannersResourceImpl.getBannersResource(coralSession, bsid);
            BannerResource bannerResource = null;
            if(srcType.equals("media"))
            {
                // String baseMediaPath = parameters.get("media_path","");
                // Resource[] media =
                // coralSession.getStore().getResourceByPath(baseMediaPath +
                // mediaPath);
                Resource[] media = coralSession.getStore().getResourceByPath("/cms/sites/" + mediaPath);
                if(media.length != 1 || !(media[0] instanceof FileResource))
                {
                    route(mvcContext, templatingContext, "banner.AddBanner", "invalid_media");
                    return;
                }
                bannerResource = MediaBannerResourceImpl.
                    createMediaBannerResource(coralSession, title, bannersRoot);
                ((MediaBannerResource)bannerResource).setMedia(media[0]);
            }
            else
            {
                bannerResource = ExternalBannerResourceImpl.
                    createExternalBannerResource(coralSession, title, bannersRoot);
                String src = parameters.get("src","");
                ((ExternalBannerResource)bannerResource).setImage(src);
            }
            bannerResource.setDescription(description);
            bannerResource.setStartDate(start);
            bannerResource.setEndDate(end);
            bannerResource.setAltText(altText);
            if(!(target.startsWith("http://") ||  target.startsWith("https://")))
            {
                target = "http://"+target;
            }
            bannerResource.setTarget(target);
            bannerResource.setFollowedCounter(0);
            bannerResource.setExpositionCounter(0);

            Resource workflowRoot = bannersRoot.getParent().getParent().getParent().getParent();
            workflowService.assignState(coralSession, workflowRoot, bannerResource);
            String transitionName = parameters.get("transition","");
            if(transitionName.length() != 0)
            {
                TransitionResource[] transitions = workflowService.getTransitions(coralSession, bannerResource.getState());
                for(int i = 0; i<transitions.length; i++)
                {
                    if(transitions[i].getName().equals(transitionName))
                    {
                        bannerResource.setState(transitions[i].getTo());
                        workflowService.enterState(coralSession, bannerResource, transitions[i].getTo());
                        break;
                    }
                }
            }
            bannerResource.update();
            long pid = parameters.getLong("pid", -1);
            if(pid != -1)
            {
                PoolResource poolResource = PoolResourceImpl.getPoolResource(coralSession, pid);
                //force the list was modified
                ResourceList banners = new ResourceList(coralSessionFactory, poolResource.getBanners());
                banners.add(bannerResource);
                poolResource.setBanners(banners);
                poolResource.update();
            }


        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        templatingContext.put("result","added_successfully");
    }
}


