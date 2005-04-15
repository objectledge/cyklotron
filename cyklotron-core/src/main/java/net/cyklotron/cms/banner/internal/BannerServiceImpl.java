package net.cyklotron.cms.banner.internal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.IdComparator;
import org.objectledge.parameters.Parameters;

import net.cyklotron.cms.banner.BannerException;
import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.BannersResource;
import net.cyklotron.cms.banner.BannersResourceImpl;
import net.cyklotron.cms.banner.MediaBannerResource;
import net.cyklotron.cms.banner.PoolResource;
import net.cyklotron.cms.banner.PoolResourceImpl;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.workflow.ProtectedTransitionResource;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * Implementation of Banner Service
 *
 * @author <a href="mailto:publo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BannerServiceImpl.java,v 1.6 2005-04-15 18:36:35 pablo Exp $
 */
public class BannerServiceImpl
    implements BannerService
{
    // instance variables ////////////////////////////////////////////////////

    /** logging facility */
    private Logger log;

    /** workflow service */
    private WorkflowService workflowService;

    /** pseudorandom generator */
    private Random random;

    /** counter update on click */
    private boolean updateOnClick;
    
    /** log click */
    private boolean logOnClick;
    
    private boolean rotateOn;

    // initialization ////////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public BannerServiceImpl(Configuration config, Logger logger, WorkflowService workflowService)
    {
        this.log = logger;
        this.workflowService = workflowService;
        random = new Random();
        updateOnClick = config.getChild(UPDATE_ON_CLICK).getValueAsBoolean(false);
		logOnClick = config.getChild(LOG_ON_CLICK).getValueAsBoolean(false);
        rotateOn = config.getChild(ROTATION).getValueAsBoolean(true);
    }

    /**
     * return the banners root resource.
     *
     * @param site the site resource.
     * @return the banners root resource.
     * @throws BannerException if the operation fails.
     */
    public BannersResource getBannersRoot(CoralSession coralSession, SiteResource site)
        throws BannerException
    {
        Resource[] applications = coralSession.getStore().getResource(site, "applications");
        if(applications == null || applications.length != 1)
        {
            throw new BannerException("Applications root for site: "+site.getName()+" not found");
        }
        Resource[] roots = coralSession.getStore().getResource(applications[0], "banners");
        if(roots.length == 1)
        {
            return (BannersResource)roots[0];
        }
        if(roots.length == 0)
        {
            return BannersResourceImpl.createBannersResource(coralSession, "banners", applications[0]);
        }
        throw new BannerException("Too much banners root resources for site: "+site.getName());
    }

    /**
     * return the next banner from banners root.
     *
     * @param root the banner root.
     * @param config the configuration.
     * @return the banner.
     */
    public BannerResource getBanner(CoralSession coralSession, BannersResource root, Parameters config)
        throws BannerException
    {
        long poolId = config.getLong("pid",-1);
        if(poolId != -1)
        {
            PoolResource poolResource = null;
            try
            {
                poolResource = PoolResourceImpl.getPoolResource(coralSession, poolId);
                return getBanner(coralSession, poolResource.getBanners(), config);
            }
            catch(EntityDoesNotExistException e)
            {
                throw new BannerException("Pool not found",e);
            }
        }
        return null;
    }

    private BannerResource getBanner(CoralSession coralSession, List banners, Parameters config)
    {
        BannerResource banner = null;
        if(banners.size() == 0)
        {
            return null;
        }
        List internal = new ArrayList();
        List external = new ArrayList();
        for(int i = 0; i < banners.size(); i++)
        {
            BannerResource bannerResource = (BannerResource)banners.get(i);
            if(bannerResource.getState().getName().equals("active"))
            {
                if(bannerResource instanceof MediaBannerResource)
                {
                    internal.add(bannerResource);
                }
                else
                {
                    external.add(bannerResource);
                }
            }
        }
        if(internal.size() == 0 && external.size() == 0)
        {
            return null;
        }
        Comparator comp = new IdComparator();
        Collections.sort(internal, comp);
        Collections.sort(external, comp);
        
        int next = 0;
        String mode = config.get("mode","mixed");
        if(mode.equals("internal") && internal.size() > 0)
        {
            if(rotateOn)
            {
                next = random.nextInt(internal.size());
            }
            banner = (BannerResource)internal.get(next);
        }
        if(mode.equals("external") && external.size() > 0)
        {
            if(rotateOn)
            {
                next = random.nextInt(external.size());
            }
            banner = (BannerResource)external.get(next);
        }
        if(mode.equals("mixed"))
        {
            float ratio = config.getFloat("ratio",(float)0.5);
            float pseudo = random.nextFloat();
            if((pseudo > ratio && internal.size() > 0) || external.size()==0)
            {
                if(rotateOn)
                {
                    next = random.nextInt(internal.size());
                }
                banner = (BannerResource)internal.get(next);
            }
            else
            {
                if(rotateOn)
                {
                    next = random.nextInt(external.size());
                }
                banner = (BannerResource)external.get(next);
            }
        }

        if(banner != null)
        {
            int counter = banner.getExpositionCounter();
            counter++;
            banner.setExpositionCounter(counter);
            banner.update();
        }
        return banner;
    }

    /**
     * notify that banner was clicked.
     *
     * @param banner the banner that is being clicked.
     */
    public void followBanner(CoralSession coralSession, BannerResource banner)
    {
    	if(updateOnClick)
    	{
    		int counter = banner.getFollowedCounter();
        	counter++;
        	banner.setFollowedCounter(counter);
        	banner.update();
    	}
    	if(logOnClick)
    	{
    		log.info(banner.getIdString());
    	}
    }

    /**
     * delete the banner.
     *
     * @param banner the banner.
     */
    public void deleteBanner(CoralSession coralSession, BannerResource banner)
        throws BannerException
    {
        try
        {
            coralSession.getStore().deleteResource(banner);
        }
        catch(EntityInUseException e)
        {
            throw new BannerException("Couldn't not delete banner",e);
        }
    }


    /**
     * execute logic of the job to check expiration date.
     */
    public void checkBannerState(CoralSession coralSession)
    {
		try
		{
			Resource readyState = coralSession.getStore()
				.getUniqueResourceByPath("/cms/workflow/automata/banner.banner/states/ready");
			Resource activeState = coralSession.getStore()
				.getUniqueResourceByPath("/cms/workflow/automata/banner.banner/states/active");				
			QueryResults results = coralSession.getQuery().
				executeQuery("FIND RESOURCE FROM cms.banner.banner WHERE state = "+readyState.getIdString());
			Resource[] nodes = results.getArray(1);
			log.debug("CheckBannerState "+nodes.length+" ready banners found");
			for(int i = 0; i < nodes.length; i++)
			{
				checkBannerState(coralSession, (BannerResource)nodes[i]);
			}
			results = coralSession.getQuery()
				.executeQuery("FIND RESOURCE FROM cms.banner.banner WHERE state = "+activeState.getIdString());
			nodes = results.getArray(1);
			log.debug("CheckBannerState "+nodes.length+" active banners found");
			for(int i = 0; i < nodes.length; i++)
			{
				checkBannerState(coralSession, (BannerResource)nodes[i]);
			}
		}
		catch(Exception e)
		{
			log.error("CheckBannerState exception ",e);
		}
    	
    	/*
        SiteResource[] sites = siteService.getSites();
        for(int i=0; i < sites.length; i++)
        {
            try
            {
                BannersResource bannersRoot = getBannersRoot(sites[i]);
                Resource[] banners = coralSession.getStore().getResource(bannersRoot);
                for(int j = 0; j < banners.length; j++)
                {
                    if(banners[j] instanceof BannerResource)
                    {
                        checkBannerState((BannerResource)banners[j]);
                    }
                }
            }
            catch(BannerException e)
            {
                //simple the site has no banner application
                //do nothing.
            }
        }
        */
    }


    /**
     * check state of the poll and expire it if the end date was reached.
     */
    private void checkBannerState(CoralSession coralSession, BannerResource bannerResource)
    {
        try
        {
            Date today = Calendar.getInstance().getTime();
            ProtectedTransitionResource[] transitions = 
                workflowService.getAllowedTransitions(coralSession, bannerResource, coralSession.getUserSubject());
            String state = bannerResource.getState().getName();
            ProtectedTransitionResource transition = null;

            if(state.equals("ready"))
            {
                if(today.after(bannerResource.getEndDate()))
                {
                    for(int i = 0; i < transitions.length; i++)
                    {
                        if(transitions[i].getName().equals("expire_ready"))
                        {
                            transition = transitions[i];
                            break;
                        }
                    }
                    workflowService.performTransition(coralSession, bannerResource, transition);
                    return;
                }
                if(today.after(bannerResource.getStartDate()))
                {
                    for(int i = 0; i < transitions.length; i++)
                    {
                        if(transitions[i].getName().equals("activate"))
                        {
                            transition = transitions[i];
                            break;
                        }
                    }
                    workflowService.performTransition(coralSession, bannerResource, transition);
                    return;
                }
            }
            if(state.equals("active"))
            {
                if(today.after(bannerResource.getEndDate()))
                {
                    for(int i = 0; i < transitions.length; i++)
                    {
                        if(transitions[i].getName().equals("expire_active"))
                        {
                            transition = transitions[i];
                            break;
                        }
                    }
                    workflowService.performTransition(coralSession, bannerResource, transition);
                    return;
                }
            }
        }
        catch(WorkflowException e)
        {
            log.error("Poll Job Exception",e);
        }
    }

}

