package net.cyklotron.cms.modules.actions.banner;

import java.util.Date;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
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
import net.cyklotron.cms.banner.ExternalBannerResource;
import net.cyklotron.cms.banner.MediaBannerResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.StateResource;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateBanner.java,v 1.4 2005-03-09 09:59:02 pablo Exp $
 */
public class UpdateBanner
    extends BaseBannerAction
{

    public UpdateBanner(Logger logger, StructureService structureService,
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
            route(mvcContext, templatingContext, "banner.EditBanner", "invalid_title");
            return;
        }
        if(description.length() > 256)
        {
            route(mvcContext, templatingContext, "banner.EditBanner", "invalid_description");
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
                    route(mvcContext, templatingContext, "banner.EditBanner", "invalid_media");
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
                StateResource[] states = workflowService.getStates(coralSession, workflowService.getAutomaton(coralSession, bannerResource.getState()),false);
                int i = 0;
                for(;i < states.length; i++)
                {
                    if(states[i].getName().equals("ready"))
                    {
                        bannerResource.setState(states[i]);
                        workflowService.enterState(coralSession, bannerResource,states[i]);
                        break;
                    }
                }
                if(i == states.length)
                {
                    templatingContext.put("result","state_not_found");
                    return;
                }
            }
            bannerResource.update();
        }
        catch(WorkflowException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        templatingContext.put("result","updated_successfully");
    }
}


