package net.cyklotron.cms.modules.actions.banner;

import java.util.Date;
import java.util.List;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannersResource;
import net.cyklotron.cms.banner.BannersResourceImpl;
import net.cyklotron.cms.banner.ExternalBannerResource;
import net.cyklotron.cms.banner.ExternalBannerResourceImpl;
import net.cyklotron.cms.banner.MediaBannerResource;
import net.cyklotron.cms.banner.MediaBannerResourceImpl;
import net.cyklotron.cms.banner.PoolResource;
import net.cyklotron.cms.banner.PoolResourceImpl;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.services.workflow.TransitionResource;
import net.cyklotron.services.workflow.WorkflowException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddBanner.java,v 1.1 2005-01-24 04:34:40 pablo Exp $
 */
public class AddBanner
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
            route(data, "banner,AddBanner", "invalid_title");
            return;
        }
        if(description.length() > 256)
        {
            route(data, "banner,AddBanner", "invalid_description");
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
                    route(data, "banner,AddBanner", "invalid_media");
                    return;
                }
                bannerResource = MediaBannerResourceImpl.
                    createMediaBannerResource(coralSession, title, bannersRoot, subject);
                ((MediaBannerResource)bannerResource).setMedia(media[0]);
            }
            else
            {
                bannerResource = ExternalBannerResourceImpl.
                    createExternalBannerResource(coralSession, title, bannersRoot, subject);
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
            workflowService.assignState(workflowRoot, bannerResource, subject);
            String transitionName = parameters.get("transition","");
            if(transitionName.length() != 0)
            {
                TransitionResource[] transitions = workflowService.getTransitions(bannerResource.getState());
                for(int i = 0; i<transitions.length; i++)
                {
                    if(transitions[i].getName().equals(transitionName))
                    {
                        bannerResource.setState(transitions[i].getTo());
                        workflowService.enterState(bannerResource, transitions[i].getTo());
                        break;
                    }
                }
            }
            bannerResource.update(subject);
            long pid = parameters.getLong("pid", -1);
            if(pid != -1)
            {
                PoolResource poolResource = PoolResourceImpl.getPoolResource(coralSession, pid);
                List banners = poolResource.getBanners();
                banners.add(bannerResource);
                poolResource.setBanners(banners);
                poolResource.update(subject);
            }


        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        catch(ValueRequiredException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        catch(WorkflowException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        templatingContext.put("result","added_successfully");
    }
}


