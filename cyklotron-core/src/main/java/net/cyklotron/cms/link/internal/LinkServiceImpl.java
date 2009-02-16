package net.cyklotron.cms.link.internal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.event.ResourceDeletionListener;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.picocontainer.Startable;

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
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.workflow.ProtectedTransitionResource;
import net.cyklotron.cms.workflow.StatefulResource;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * Implementation of Link Service
 *
 * @author <a href="mailto:publo@ngo.pl">Pawel Potempski</a>
 * @version $Id: LinkServiceImpl.java,v 1.11 2007-11-18 21:23:43 rafal Exp $
 */
public class LinkServiceImpl
    implements LinkService, ResourceDeletionListener,Startable
{
    // instance variables ////////////////////////////////////////////////////

    /** logging facility */
    private Logger log;

    /** workflow service */
    private WorkflowService workflowService;
    
    private CoralSessionFactory sessionFactory;

    // initialization ////////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public LinkServiceImpl(CoralSessionFactory sessionFactory, Logger logger,
        WorkflowService workflowService)
    {
        this.log = logger;
        this.workflowService = workflowService;
        this.sessionFactory = sessionFactory;
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            coralSession.getEvent().addResourceDeletionListener(this, null);
        }
        catch(Exception e)
        {
            throw new ComponentInitializationError("Could not start link service", e);
        }
        finally
        {
            coralSession.close();
        }        
    }
    
    public void start()
    {
    }

    public void stop()
    {
        
    }


    /**
     * return the links root resource.
     *
     * @param site the site resource.
     * @return the links root resource.
     * @throws LinkException if the operation fails.
     */
    public LinkRootResource getLinkRoot(CoralSession coralSession, SiteResource site)
        throws LinkException
    {
        Resource[] applications = coralSession.getStore().getResource(site, "applications");
        if(applications == null || applications.length != 1)
        {
            throw new LinkException("Applications root for site: "+site.getName()+" not found");
        }
        Resource[] roots = coralSession.getStore().getResource(applications[0], "links");
        if(roots.length == 1)
        {
            return (LinkRootResource)roots[0];
        }
        if(roots.length == 0)
        {
            try
            {
                return LinkRootResourceImpl.createLinkRootResource(coralSession, "links", applications[0]);
            }
            catch(InvalidResourceNameException e)
            {
                throw new LinkException("unexpected exception", e);
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
     * @throws LinkException if the operation fails.
     */
    public List getLinks(CoralSession coralSession, LinkRootResource linkRoot, Parameters config)
        throws LinkException
    {
        long pid = config.getLong("pid",-1);
        if(pid != -1)
        {
            PoolResource poolResource = null;
            try
            {
                poolResource = PoolResourceImpl.getPoolResource(coralSession, pid);
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
     */
    public List getPools(CoralSession coralSession, LinkRootResource linkRoot)
    {
        Resource[] links = coralSession.getStore().getResource(linkRoot);
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
    public void followLink(CoralSession coralSession, BaseLinkResource link)
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
    public void deleteLink(CoralSession coralSession,BaseLinkResource link)
        throws LinkException
    {
        try
        {
            coralSession.getStore().deleteResource(link);
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
	 * @param targetName the name of the new link.
	 * @param pool the target pool.
	 * @param subject the subject.
	 * @return the copied link.
	 * @throws LinkException if the operation fails.
	 */
	public BaseLinkResource copyLink(CoralSession coralSession, BaseLinkResource source, String targetName, PoolResource pool, Subject subject)
		throws LinkException
	{
		try
		{
			LinkRootResource linksRoot = (LinkRootResource)pool.getParent();
			BaseLinkResource linkResource = null;
			if(source instanceof CmsLinkResource)
			{
				linkResource = CmsLinkResourceImpl.
					createCmsLinkResource(coralSession, targetName, linksRoot);
				((CmsLinkResource)linkResource).setNode(((CmsLinkResource)source).getNode());
			}
			else
			{
				linkResource = ExternalLinkResourceImpl.
					createExternalLinkResource(coralSession, targetName, linksRoot);
				((ExternalLinkResource)linkResource).setTarget(((ExternalLinkResource)source).getTarget());
			}
			linkResource.setDescription(source.getDescription());
			linkResource.setStartDate(source.getStartDate());
			linkResource.setEndDate(source.getEndDate());
			linkResource.setEternal(source.getEternal());
			Resource workflowRoot = linksRoot.getParent().getParent().getParent().getParent();
			workflowService.assignState(coralSession, workflowRoot, linkResource);
			
			//uncomment this it if you want copy the state of the link 
			/*
			linkResource.setState(source.getState());
			workflowService.enterState(linkResource, source.getState());
			*/
			linkResource.update();
			ResourceList links = new ResourceList(sessionFactory, pool.getLinks());
			links.add(linkResource);
			pool.setLinks(links);
			pool.update();
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
    public void checkLinkState(CoralSession coralSession)
    {
    	try
    	{
    		Resource readyState = coralSession.getStore()
				.getUniqueResourceByPath("/cms/workflow/automata/link.link/states/ready");
			Resource activeState = coralSession.getStore()
				.getUniqueResourceByPath("/cms/workflow/automata/link.link/states/active");				
			QueryResults results = coralSession.getQuery().
				executeQuery("FIND RESOURCE FROM cms.link.base_link WHERE state = "+readyState.getIdString());
			Resource[] nodes = results.getArray(1);
			log.debug("CheckLinkState "+nodes.length+" ready links found");
			for(int i = 0; i < nodes.length; i++)
			{
				checkLinkState(coralSession, (BaseLinkResource)nodes[i]);
			}
			results = coralSession.getQuery()
				.executeQuery("FIND RESOURCE FROM cms.link.base_link WHERE state = "+activeState.getIdString());
			nodes = results.getArray(1);
			log.debug("CheckLinkState "+nodes.length+" active links found");
			for(int i = 0; i < nodes.length; i++)
			{
				checkLinkState(coralSession, (BaseLinkResource)nodes[i]);
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
                Resource[] links = coralSession.getStore().getResource(linksRoot);
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
    private void checkLinkState(CoralSession coralSession,BaseLinkResource linkResource)
    {
        try
        {
            Date today = Calendar.getInstance().getTime();
            ProtectedTransitionResource[] transitions = workflowService.getAllowedTransitions(coralSession, linkResource, coralSession.getUserSubject());
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
                    workflowService.performTransition(coralSession, linkResource, transition);
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
                    workflowService.performTransition(coralSession, linkResource, transition);
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
                    workflowService.performTransition(coralSession, linkResource, transition);
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
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            String query = "FIND RESOURCE FROM cms.link.pool";
            QueryResults results = coralSession.getQuery().executeQuery(query);
            Resource[] pools = results.getArray(1);
            if(resource instanceof NavigationNodeResource)
            {
                query = "FIND RESOURCE FROM cms.link.cms_link WHERE node = " + resource.getId();
                results = coralSession.getQuery().executeQuery(query);
                Resource[] links = results.getArray(1);
                for(int i = 0; i < links.length; i++)
                {
                    BaseLinkResource link = (BaseLinkResource)links[i];
                    for(int j = 0; j < pools.length; j++)
                    {
                        PoolResource pool = (PoolResource)pools[j];
                        // make sure different object is set to force update
                        ResourceList l = new ResourceList(sessionFactory, pool.getLinks());
                        if(l != null && l.size() > 0)
                        {
                            if(l.remove(link))
                            {
                                pool.setLinks(l);
                                pool.update();
                            }
                        }
                    }
                    deleteLink(coralSession, link);
                }
            }
        }
        catch(Exception e)
        {
            log.error("Exception occured in LinkService listener ", e);
        }
        finally
        {
            coralSession.close();
        }
    }

}

