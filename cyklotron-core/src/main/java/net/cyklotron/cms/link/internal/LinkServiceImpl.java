package net.cyklotron.cms.link.internal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.cyklotron.cms.link.BaseLinkResource;
import net.cyklotron.cms.link.CmsLinkResource;
import net.cyklotron.cms.link.CmsLinkResourceImpl;
import net.cyklotron.cms.link.ExternalLinkResource;
import net.cyklotron.cms.link.ExternalLinkResourceImpl;
import net.cyklotron.cms.link.LinkException;
import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.LinkRootResourceImpl;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.link.PoolResource;
import net.cyklotron.cms.link.PoolResourceImpl;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.services.workflow.ProtectedTransitionResource;
import net.cyklotron.services.workflow.StatefulResource;
import net.cyklotron.services.workflow.WorkflowException;
import net.cyklotron.services.workflow.WorkflowService;
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
import net.labeo.services.resource.event.ResourceDeletionListener;
import net.labeo.services.resource.query.QueryResults;
import net.labeo.util.configuration.Configuration;

/**
 * Implementation of Link Service
 *
 * @author <a href="mailto:publo@ngo.pl">Pawel Potempski</a>
 * @version $Id: LinkServiceImpl.java,v 1.1 2005-01-12 20:45:23 pablo Exp $
 */
public class LinkServiceImpl
    extends BaseService
    implements LinkService, ResourceDeletionListener
{
    // instance variables ////////////////////////////////////////////////////

    /** logging facility */
    private LoggingFacility log;

    /** resource service */
    private ResourceService resourceService;

    /** site serive */
    private SiteService siteService;

    /** workflow service */
    private WorkflowService workflowService;

    /** system subject */
    private Subject subject;

    // initialization ////////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public void start()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
            getFacility(LOGGING_FACILITY);
        resourceService = (ResourceService)broker.getService(ResourceService.SERVICE_NAME);
        siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
        workflowService = (WorkflowService)broker.getService(WorkflowService.SERVICE_NAME);
        try
        {
            subject = resourceService.getSecurity().getSubject(Subject.ROOT);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ConfigurationError("Couldn't find system subject");
        }
        resourceService.getEvent().addResourceDeletionListener(this, null);
    }
    
    

    /**
     * return the links root resource.
     *
     * @param site the site resource.
     * @return the links root resource.
     * @throws LinkException.
     */
    public LinkRootResource getLinkRoot(SiteResource site)
        throws LinkException
    {
        Resource[] applications = resourceService.getStore().getResource(site, "applications");
        if(applications == null || applications.length != 1)
        {
            throw new LinkException("Applications root for site: "+site.getName()+" not found");
        }
        Resource[] roots = resourceService.getStore().getResource(applications[0], "links");
        if(roots.length == 1)
        {
            return (LinkRootResource)roots[0];
        }
        if(roots.length == 0)
        {
            try
            {
                return LinkRootResourceImpl.createLinkRootResource(resourceService, "links", applications[0], subject);
            }
            catch(ValueRequiredException e)
            {
                throw new LinkException("Couldn't create links root node");
            }
        }
        throw new LinkException("Too much links root resources for site: "+site.getName());
    }

    /**
     * return the list of links.
     *
     * @param linkRoot the links pool.
     * @param config the configuration.
     * @return the links list.
     * @throws LinkException.
     */
    public List getLinks(LinkRootResource linkRoot, Configuration config)
        throws LinkException
    {
        long pid = config.get("pid").asLong(-1);
        if(pid != -1)
        {
            PoolResource poolResource = null;
            try
            {
                poolResource = PoolResourceImpl.getPoolResource(resourceService, pid);
                List links = poolResource.getLinks();
                ArrayList active = new ArrayList();
                for(int i = 0; i < links.size(); i++)
                {
                    String name = ((StatefulResource)links.get(i)).getState().getName();
                    if(name.equals("active"))
                    {
                        active.add(links.get(i));
                    }
                }
                return active;
            }
            catch(EntityDoesNotExistException e)
            {
                throw new LinkException("Pool not found",e);
            }
        }
        return null;
    }

    /**
     * return the list of pools.
     *
     * @param linkRoot the links pool.
     * @return the links list.
     * @throws LinkException.
     */
    public List getPools(LinkRootResource linkRoot)
    {
        Resource[] links = resourceService.getStore().getResource(linkRoot);
        ArrayList pools = new ArrayList();
        for(int i = 0; i < links.length; i++)
        {
            if(links[i] instanceof PoolResource)
            {
                pools.add(links[i]);
            }
        }
        return pools;
    }

    /**
     * notify that link was clicked.
     *
     * @param link the link that is being clicked.
     */
    public void followLink(BaseLinkResource link)
    {
        /*
        int counter = link.getFollowedCounter();
        counter++;
        link.setFollowedCounter(counter);
        link.update(systemSubject);
        */
    }

    /**
     * delete the link.
     *
     * @param link the link.
     */
    public void deleteLink(BaseLinkResource link)
        throws LinkException
    {
        try
        {
            resourceService.getStore().deleteResource(link);
        }
        catch(EntityInUseException e)
        {
            throw new LinkException("Couldn't not delete link",e);
        }
    }

	/**
	 * Copy the link.
	 *
	 * @param source the source link.
	 * @param name the name of the new link.
	 * @param pool the pool.
	 * @param subject the subject.
	 * @return the copied link.
	 * @throws LinkException.
	 */
	public BaseLinkResource copyLink(BaseLinkResource source, String targetName, PoolResource pool, Subject subject)
		throws LinkException
	{
		try
		{
			LinkRootResource linksRoot = (LinkRootResource)pool.getParent();
			BaseLinkResource linkResource = null;
			if(source instanceof CmsLinkResource)
			{
				linkResource = CmsLinkResourceImpl.
					createCmsLinkResource(resourceService, targetName, linksRoot, subject);
				((CmsLinkResource)linkResource).setNode(((CmsLinkResource)source).getNode());
			}
			else
			{
				linkResource = ExternalLinkResourceImpl.
					createExternalLinkResource(resourceService, targetName, linksRoot, subject);
				((ExternalLinkResource)linkResource).setTarget(((ExternalLinkResource)source).getTarget());
			}
			linkResource.setDescription(source.getDescription());
			linkResource.setStartDate(source.getStartDate());
			linkResource.setEndDate(source.getEndDate());
			linkResource.setEternal(source.getEternal());
			Resource workflowRoot = linksRoot.getParent().getParent().getParent().getParent();
			workflowService.assignState(workflowRoot, linkResource, subject);
			
			//uncomment this it if you want copy the state of the link 
			/*
			linkResource.setState(source.getState());
			workflowService.enterState(linkResource, source.getState());
			*/
			linkResource.update(subject);
			List links = pool.getLinks();
			links.add(linkResource);
			pool.setLinks(links);
			pool.update(subject);
			return linkResource;
		}
		catch(Exception e)
		{
			throw new LinkException("Exception occured during link copying", e);	
		}
	}
    


    /**
     * execute logic of the job to check expiration date.
     */
    public void checkLinkState()
    {
    	try
    	{
    		Resource readyState = resourceService.getStore()
				.getUniqueResourceByPath("/cms/workflow/automata/link.link/states/ready");
			Resource activeState = resourceService.getStore()
				.getUniqueResourceByPath("/cms/workflow/automata/link.link/states/active");				
			QueryResults results = resourceService.getQuery().
				executeQuery("FIND RESOURCE FROM cms.link.base_link WHERE state = "+readyState.getIdString());
			Resource[] nodes = results.getArray(1);
			log.debug("CheckLinkState "+nodes.length+" ready links found");
			for(int i = 0; i < nodes.length; i++)
			{
				checkLinkState((BaseLinkResource)nodes[i]);
			}
			results = resourceService.getQuery()
				.executeQuery("FIND RESOURCE FROM cms.link.base_link WHERE state = "+activeState.getIdString());
			nodes = results.getArray(1);
			log.debug("CheckLinkState "+nodes.length+" active links found");
			for(int i = 0; i < nodes.length; i++)
			{
				checkLinkState((BaseLinkResource)nodes[i]);
			}
    	}
    	catch(Exception e)
    	{
    		log.error("CheckLinkState exception ",e);
    	}
		
		/*
        SiteResource[] sites = siteService.getSites();
        for(int i=0; i < sites.length; i++)
        {
            try
            {
                LinkRootResource linksRoot = getLinkRoot(sites[i]);
                Resource[] links = resourceService.getStore().getResource(linksRoot);
                for(int j = 0; j < links.length; j++)
                {
                    if(links[j] instanceof BaseLinkResource)
                    {
                        checkLinkState((BaseLinkResource)links[j]);
                    }
                }
            }
            catch(LinkException e)
            {
                //simple the site has no poll application
                //do nothing.
            }
        }
        */
    }


    /**
     * check state of the link and expire it if the end date was reached.
     */
    private void checkLinkState(BaseLinkResource linkResource)
    {
        try
        {
            Date today = Calendar.getInstance().getTime();
            ProtectedTransitionResource[] transitions = workflowService.getAllowedTransitions(linkResource, subject);
            String state = linkResource.getState().getName();
            ProtectedTransitionResource transition = null;

            if(state.equals("ready"))
            {
                if(today.after(linkResource.getEndDate()))
                {
                    for(int i = 0; i < transitions.length; i++)
                    {
                        if(transitions[i].getName().equals("expire_ready"))
                        {
                            transition = transitions[i];
                            break;
                        }
                    }
                    workflowService.performTransition(linkResource, transition, subject);
                    return;
                }
                if(today.after(linkResource.getStartDate()))
                {
                    for(int i = 0; i < transitions.length; i++)
                    {
                        if(transitions[i].getName().equals("activate"))
                        {
                            transition = transitions[i];
                            break;
                        }
                    }
                    workflowService.performTransition(linkResource, transition, subject);
                    return;
                }
            }
            if((!linkResource.getEternal()) && state.equals("active"))
            {
                if(today.after(linkResource.getEndDate()))
                {
                    for(int i = 0; i < transitions.length; i++)
                    {
                        if(transitions[i].getName().equals("expire_active"))
                        {
                            transition = transitions[i];
                            break;
                        }
                    }
                    workflowService.performTransition(linkResource, transition, subject);
                    return;
                }
            }
        }
        catch(WorkflowException e)
        {
            log.error("Poll Job Exception",e);
        }
    }

	// resource deletion listener //////////////////////////////////////////////////////////////////
	
    public void resourceDeleted(Resource resource)
    {
    	try
		{
    		String query = "FIND RESOURCE FROM cms.link.pool";
    		QueryResults results = resourceService.getQuery().executeQuery(query);
    		Resource[] pools = results.getArray(1);
	    	if (resource instanceof NavigationNodeResource)
	        {
		        query = "FIND RESOURCE FROM cms.link.cms_link WHERE node = "+resource.getId();
		        results = resourceService.getQuery().executeQuery(query);
		        Resource[] links = results.getArray(1);
		        for(int i = 0; i < links.length; i++)
		        {
		        	BaseLinkResource link = (BaseLinkResource)links[i];
		        	for(int j = 0; j < pools.length; j++)
			        {
		        		PoolResource pool = (PoolResource)pools[j];
		        		List l = pool.getLinks();
		        		if(l != null && l.size()>0)
		        		for(int k = 0; k < l.size(); k++)
				        {
		                    l.remove(link);
		                    pool.setLinks(l);
		                    pool.update(subject);
				        }	
			        }
		        	deleteLink(link);
		        }
	        }
		}
    	catch(Exception e)
		{
    		log.error("Exception occured in LinkService listener ",e);
		}
    }

}

