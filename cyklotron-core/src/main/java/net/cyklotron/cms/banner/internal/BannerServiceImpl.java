package net.cyklotron.cms.banner.internal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.labeo.services.BaseService;
import net.labeo.services.ConfigurationError;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.query.QueryResults;
import net.labeo.util.configuration.Configuration;

import net.cyklotron.cms.banner.BannerException;
import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.BannersResource;
import net.cyklotron.cms.banner.BannersResourceImpl;
import net.cyklotron.cms.banner.MediaBannerResource;
import net.cyklotron.cms.banner.PoolResource;
import net.cyklotron.cms.banner.PoolResourceImpl;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.services.workflow.ProtectedTransitionResource;
import net.cyklotron.services.workflow.WorkflowException;
import net.cyklotron.services.workflow.WorkflowService;

/**
 * Implementation of Banner Service
 *
 * @author <a href="mailto:publo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BannerServiceImpl.java,v 1.1 2005-01-12 20:45:20 pablo Exp $
 */
public class BannerServiceImpl
    extends BaseService
    implements BannerService
{
    // instance variables ////////////////////////////////////////////////////

    /** logging facility */
    private LoggingFacility log;

	/** logging facility */
	private LoggingFacility clickLog;

    /** resource service */
    private ResourceService resourceService;

    /** site serive */
    private SiteService siteService;

    /** workflow service */
    private WorkflowService workflowService;

    /** pseudorandom generator */
    private Random random;

    /** system subject */
    private Subject subject;
    
    /** counter update on click */
    private boolean updateOnClick;
    
    /** log click */
    private boolean logOnClick;

    // initialization ////////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public void init()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
            getFacility(LOGGING_FACILITY);
		clickLog = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
			getFacility(CLICK_LOGGING_FACILITY);            
        resourceService = (ResourceService)broker.getService(ResourceService.SERVICE_NAME);
        siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
        workflowService = (WorkflowService)broker.getService(WorkflowService.SERVICE_NAME);
        random = new Random();
        try
        {
            subject = resourceService.getSecurity().getSubject(Subject.ROOT);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ConfigurationError("Couldn't find system subject");
        }
        updateOnClick = config.get(UPDATE_ON_CLICK).asBoolean(false);
		logOnClick = config.get(LOG_ON_CLICK).asBoolean(false);
    }

    /**
     * return the banners root resource.
     *
     * @param site the site resource.
     * @return the banners root resource.
     * @throws BannerException.
     */
    public BannersResource getBannersRoot(SiteResource site)
        throws BannerException
    {
        Resource[] applications = resourceService.getStore().getResource(site, "applications");
        if(applications == null || applications.length != 1)
        {
            throw new BannerException("Applications root for site: "+site.getName()+" not found");
        }
        Resource[] roots = resourceService.getStore().getResource(applications[0], "banners");
        if(roots.length == 1)
        {
            return (BannersResource)roots[0];
        }
        if(roots.length == 0)
        {
            try
            {
                return BannersResourceImpl.createBannersResource(resourceService, "banners", applications[0], subject);
            }
            catch(ValueRequiredException e)
            {
                throw new BannerException("Couldn't create banners root node");
            }
        }
        throw new BannerException("Too much banners root resources for site: "+site.getName());
    }

    /**
     * return the next banner from banners root.
     *
     * @param root the banner root.
     * @param the configuration.
     * @return the banner.
     */
    public BannerResource getBanner(BannersResource root, Configuration config)
        throws BannerException
    {
        long poolId = config.get("pid").asLong(-1);
        if(poolId != -1)
        {
            PoolResource poolResource = null;
            try
            {
                poolResource = PoolResourceImpl.getPoolResource(resourceService, poolId);
                return getBanner(poolResource.getBanners(), config);
            }
            catch(EntityDoesNotExistException e)
            {
                throw new BannerException("Pool not found",e);
            }
        }
        return null;
    }

    private BannerResource getBanner(List banners, Configuration config)
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

        String mode = config.get("mode").asString("mixed");
        if(mode.equals("internal") && internal.size() > 0)
        {
            int next = random.nextInt(internal.size());
            banner = (BannerResource)internal.get(next);
        }
        if(mode.equals("external") && external.size() > 0)
        {
            int next = random.nextInt(external.size());
            banner = (BannerResource)external.get(next);
        }
        if(mode.equals("mixed"))
        {
            float ratio = config.get("ratio").asFloat((float)0.5);
            float pseudo = random.nextFloat();
            if((pseudo > ratio && internal.size() > 0) || external.size()==0)
            {
                int next = random.nextInt(internal.size());
                banner = (BannerResource)internal.get(next);
            }
            else
            {
                int next = random.nextInt(external.size());
                banner = (BannerResource)external.get(next);
            }
        }

        if(banner != null)
        {
            int counter = banner.getExpositionCounter();
            counter++;
            banner.setExpositionCounter(counter);
            banner.update(subject);
        }
        return banner;
    }

    /**
     * notify that banner was clicked.
     *
     * @param banner the banner that is being clicked.
     */
    public void followBanner(BannerResource banner)
    {
    	if(updateOnClick)
    	{
    		int counter = banner.getFollowedCounter();
        	counter++;
        	banner.setFollowedCounter(counter);
        	banner.update(subject);
    	}
    	if(logOnClick)
    	{
    		clickLog.info(banner.getIdString());
    	}
    }

    /**
     * delete the banner.
     *
     * @param banner the banner.
     */
    public void deleteBanner(BannerResource banner)
        throws BannerException
    {
        try
        {
            resourceService.getStore().deleteResource(banner);
        }
        catch(EntityInUseException e)
        {
            throw new BannerException("Couldn't not delete banner",e);
        }
    }


    /**
     * execute logic of the job to check expiration date.
     */
    public void checkBannerState()
    {
		try
		{
			Resource readyState = resourceService.getStore()
				.getUniqueResourceByPath("/cms/workflow/automata/banner.banner/states/ready");
			Resource activeState = resourceService.getStore()
				.getUniqueResourceByPath("/cms/workflow/automata/banner.banner/states/active");				
			QueryResults results = resourceService.getQuery().
				executeQuery("FIND RESOURCE FROM cms.banner.banner WHERE state = "+readyState.getIdString());
			Resource[] nodes = results.getArray(1);
			log.debug("CheckBannerState "+nodes.length+" ready banners found");
			for(int i = 0; i < nodes.length; i++)
			{
				checkBannerState((BannerResource)nodes[i]);
			}
			results = resourceService.getQuery()
				.executeQuery("FIND RESOURCE FROM cms.banner.banner WHERE state = "+activeState.getIdString());
			nodes = results.getArray(1);
			log.debug("CheckBannerState "+nodes.length+" active banners found");
			for(int i = 0; i < nodes.length; i++)
			{
				checkBannerState((BannerResource)nodes[i]);
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
                Resource[] banners = resourceService.getStore().getResource(bannersRoot);
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
    private void checkBannerState(BannerResource bannerResource)
    {
        try
        {
            Date today = Calendar.getInstance().getTime();
            ProtectedTransitionResource[] transitions = workflowService.getAllowedTransitions(bannerResource, subject);
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
                    workflowService.performTransition(bannerResource, transition, subject);
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
                    workflowService.performTransition(bannerResource, transition, subject);
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
                    workflowService.performTransition(bannerResource, transition, subject);
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

