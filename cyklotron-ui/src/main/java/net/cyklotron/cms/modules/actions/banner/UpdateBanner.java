package net.cyklotron.cms.modules.actions.banner;

import java.util.Date;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannerResourceImpl;
import net.cyklotron.cms.banner.ExternalBannerResource;
import net.cyklotron.cms.banner.MediaBannerResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.services.workflow.StateResource;
import net.cyklotron.services.workflow.WorkflowException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateBanner.java,v 1.1 2005-01-24 04:34:40 pablo Exp $
 */
public class UpdateBanner
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
        if(bid == -1)
        {
            throw new ProcessingException("Banners id not found");
        }

        String altText = parameters.get("alt_text","");
        String target = parameters.get("target","");
        String title = parameters.get("title","");
        String description = parameters.get("description","");

        if(title.length() < 1 || title.length() > 64)
        {
            route(data, "banner,EditBanner", "invalid_title");
            return;
        }
        if(description.length() > 256)
        {
            route(data, "banner,EditBanner", "invalid_description");
            return;
        }

        // time stuff
        long startTime = parameters.getLong("start_time", 0);
        long endTime = parameters.getLong("end_time", 0);
        Date start = new Date(startTime);
        Date end = new Date(endTime);

        try
        {
            BannerResource bannerResource = BannerResourceImpl.getBannerResource(coralSession, bid);
            if(bannerResource instanceof MediaBannerResource)
            {
                String mediaPath = parameters.get("src","");
                Resource[] media = coralSession.getStore().getResourceByPath("/cms/sites/" + mediaPath);
                if(media.length != 1 || !(media[0] instanceof FileResource))
                {
                    route(data, "banner,EditBanner", "invalid_media");
                    return;
                }
                ((MediaBannerResource)bannerResource).setMedia(media[0]);
            }
            else
            {
                String src = parameters.get("src","");
                ((ExternalBannerResource)bannerResource).setImage(src);
            }
            if(!bannerResource.getName().equals(title))
            {
                coralSession.getStore().setName(bannerResource, title);
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
            if(bannerResource.getState().getName().equals("active") ||
               bannerResource.getState().getName().equals("expired"))
            {
                StateResource[] states = workflowService.getStates(workflowService.getAutomaton(bannerResource.getState()),false);
                int i = 0;
                for(;i < states.length; i++)
                {
                    if(states[i].getName().equals("ready"))
                    {
                        bannerResource.setState(states[i]);
                        workflowService.enterState(bannerResource,states[i]);
                        break;
                    }
                }
                if(i == states.length)
                {
                    templatingContext.put("result","state_not_found");
                    return;
                }
            }
            bannerResource.update(subject);
        }
        catch(WorkflowException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        templatingContext.put("result","updated_successfully");
    }
}


