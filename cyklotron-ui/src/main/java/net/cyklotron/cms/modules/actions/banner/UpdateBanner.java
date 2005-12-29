package net.cyklotron.cms.modules.actions.banner;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import net.cyklotron.cms.banner.BannerResourceImpl;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.ExternalBannerResource;
import net.cyklotron.cms.banner.MediaBannerResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.banner.PoolResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.StateResource;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateBanner.java,v 1.6 2005-12-29 12:04:49 pablo Exp $
 */
public class UpdateBanner
    extends BaseBannerAction
{
    private CoralSessionFactory coralSessionFactory;

    public UpdateBanner(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, BannerService bannerService, 
        WorkflowService workflowService, CoralSessionFactory coralSessionFactory)
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
                try
                {
                    coralSession.getStore().setName(bannerResource, title);
                }
                catch(InvalidResourceNameException e)
                {
                    templatingContext.put("result", "invalid_name");
                    return;
                }
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
            
            // here update pools that link belongs to
            long[] params = parameters.getLongs("pool_id");
            Set<Long> selectionSet = new HashSet<Long>();
            for(int i = 0; i < params.length; i++)
            {
                selectionSet.add(new Long(params[i]));
            }
            Resource[] resources = coralSession.getStore().getResource(bannerResource.getParent());
            
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof PoolResource)
                {
                    List links = ((PoolResource)resources[i]).getBanners();
                    ResourceList newLinks = new ResourceList(coralSessionFactory);
                    boolean update = false;
                    if(selectionSet.contains(resources[i].getIdObject()))
                    {
                        // we are going to add if not exists
                        if(links == null)
                        {
                            newLinks.add(bannerResource);
                            update = true;
                        }
                        else
                        {
                            boolean found = false;
                            for(int j = 0; j < links.size(); j++)
                            {
                                Resource link = (Resource)links.get(j);
                                if(bannerResource.equals(link))
                                {
                                    found = true;
                                }
                                newLinks.add(link);
                            }
                            if(!found)
                            {
                                newLinks.add(bannerResource);
                            }
                            if(newLinks.size() > links.size())
                            {
                                update = true;
                            }
                        }
                    }
                    else
                    {
                        // we are going to del if exists
                        if(links != null)
                        {
                            for(int j = 0; j < links.size(); j++)
                            {
                                Resource link = (Resource)links.get(j);
                                if(!bannerResource.equals(link))
                                {
                                    newLinks.add(link);
                                }
                            }
                            if(newLinks.size() < links.size())
                            {
                                update = true;
                            }
                        }
                    }
                    if(update)
                    {
                        ((PoolResource)resources[i]).setBanners(newLinks);
                        resources[i].update();
                    }
                }
            }
            
            
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


