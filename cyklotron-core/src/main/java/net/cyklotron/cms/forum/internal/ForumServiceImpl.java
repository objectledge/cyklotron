package net.cyklotron.cms.forum.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import net.labeo.services.BaseService;
import net.labeo.services.InitializationError;
import net.labeo.services.event.EventService;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.AmbigousNameException;
import net.labeo.services.resource.BackendException;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.generic.NodeResourceImpl;
import net.labeo.services.resource.generic.WeakResourceList;

import net.cyklotron.cms.forum.CommentaryResource;
import net.cyklotron.cms.forum.CommentaryResourceImpl;
import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.DiscussionResourceImpl;
import net.cyklotron.cms.forum.ForumException;
import net.cyklotron.cms.forum.ForumNodeResource;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.forum.ForumResourceImpl;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.services.workflow.StateChangeListener;
import net.cyklotron.services.workflow.StateResource;
import net.cyklotron.services.workflow.StatefulResource;
import net.cyklotron.services.workflow.WorkflowService;

/**
 * Implementation of Forum Service
 *
 * @author <a href="mailto:publo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ForumServiceImpl.java,v 1.1 2005-01-12 20:45:26 pablo Exp $
 */
public class ForumServiceImpl
    extends BaseService
    implements ForumService, StateChangeListener
{
    // instance variables ////////////////////////////////////////////////////

    /** logging facility */
    private LoggingFacility log;

    /** resource service */
    private ResourceService resourceService;

    /** site serivce */
    private SiteService siteService;

    /** workflow service */
    private WorkflowService workflowService;

    /** cms security service */
    private SecurityService cmsSecurityService;

	/** event service */
	private EventService eventService;

	/** system root subject */
	private Subject rootSubject;

    // initialization ////////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public void init()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
            getFacility(LOGGING_FACILITY);
        resourceService = (ResourceService)broker.getService(ResourceService.SERVICE_NAME);
        siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
        cmsSecurityService = (SecurityService)broker.getService(SecurityService.SERVICE_NAME);
        workflowService = (WorkflowService)broker.getService(WorkflowService.SERVICE_NAME);
        eventService = (EventService)broker.getService(EventService.SERVICE_NAME);
		try
		{
			rootSubject = resourceService.getSecurity().getSubject(Subject.ROOT);
		}
		catch (EntityDoesNotExistException e)
		{
			throw new InitializationError("Could not find root subject");
		}
		eventService.addListener(StateChangeListener.class,this,null);
    }

    /**
     * Creates forum associated with a site.
     *
     * @param site the site.
     * @param mailboxOwner the owner of the mailbox where mailng lists will
     * reside.
     * @param subject the subject that performs the operation.
     * @return a ForumResource object.
     */
    public ForumResource createForum(SiteResource site, Subject mailboxOwner,
                                     Subject subject)
        throws ForumException
    {
        Resource[] res = resourceService.getStore().getResource(site, "applications");
        if(res == null || res.length != 1)
        {
            throw new ForumException("Applications root for site: "+site.getName()+" not found");
        }
        ForumResource forum;
        try
        {
        	Resource[] resources = resourceService.getStore().getResource(res[0],"forum");
        	if(resources.length > 1)
        	{
				throw new ForumException("Strange - there is more than one forum node in site '"+site.getName()+"'");
        	}
			if(resources.length == 1)
			{
				return (ForumResource)resources[0];
			}
            forum = ForumResourceImpl.createForumResource(
                resourceService, "forum", res[0], site, mailboxOwner);
        }
        catch(ValueRequiredException e)
        {
            throw new ForumException("Unexpected ARL exception", e);
        }
        try
        {
            cmsSecurityService.createRole(site.getAdministrator(), 
                "cms.forum.administrator", forum, subject);
        }
        catch(Exception e)
        {
            throw new ForumException("Failed to setup security", e);
        }
        return forum;
    }

    /**
     * Creates a new discussion.
     *
     * @param forum the forum to create discussion in.
     * @param path the pathanme of the discussion relative to forum.
     * @param admin the discussion's administrator.
     * @param subject the subject that performs the operation.
     */
    public DiscussionResource createDiscussion(ForumResource forum, String path,
                                               Subject admin, Subject subject)
        throws ForumException
    {
        String name;
        Resource parent;
        try
        {
            parent = preparePath(resourceService, forum, path, subject);
            int i = path.lastIndexOf('/');
            if(i > 0)
            {
                name = path.substring(i+1);
            }
            else
            {
                name = path;
            }
        }
        catch(AmbigousNameException e)
        {
            throw new ForumException("Ambigous discussion pathname", e);
        }

        SiteResource site = forum.getSite();
        DiscussionResource discussion;
        try
        {
            discussion = DiscussionResourceImpl.
                createDiscussionResource(resourceService, name, parent,
                                         forum, subject);
            Resource workflowRoot = null;
            if(site != null)
            {
                workflowRoot = site.getParent().getParent();
            }
            workflowService.assignState(workflowRoot, discussion, subject);
        }
        catch(Exception e)
        {
            throw new ForumException("failed to create discussion", e);
        }
        return discussion;
    }

    /**
     * Creates a new commentary.
     *
     * @param forum the forum to create commentary in.
     * @param path the pathanme of the commentary relative to forum.
     * @param admin the commentary's administrator.
     * @param subject the subject that performs the operation.
     */
    public CommentaryResource createCommentary(ForumResource forum, String path, Resource resource,
                                               Subject admin, Subject subject)
        throws ForumException
    {
        String name;
        String docTitle = "";
        Resource parent;
        try
        {
            parent = preparePath(resourceService, forum, path, subject);
            int i = path.lastIndexOf('/');
            if(i > 0)
            {
                name = path.substring(i+1);
            }
            else
            {
                name = path;
            }
            if(resource instanceof NavigationNodeResource)
            {
                docTitle = ((NavigationNodeResource)resource).getTitle();
            }
        }
        catch(AmbigousNameException e)
        {
            throw new ForumException("Ambigous commentary pathname", e);
        }

        CommentaryResource commentary;
        try
        {
            commentary = createCommentaryNode(name, parent, forum, subject);
            commentary.setResourceId(resource.getId());
            commentary.setDocumentTitle(docTitle);
            commentary.setState(getInitialCommentaryState(forum));
            commentary.update(subject);
        }
        catch(Exception e)
        {
            throw new ForumException("failed to create commentary", e);
        }
        return commentary;
    }
    
    private synchronized CommentaryResource createCommentaryNode(String name, Resource parent,
    	ForumResource forum, Subject subject)
    	throws Exception
    {
        Resource[] resources = resourceService.getStore().getResource(parent, name);
        if(resources.length > 0)
        {
            return (CommentaryResource)resources[0];
        }
        else
        {
            return CommentaryResourceImpl.
            			createCommentaryResource(resourceService, name, parent, forum, subject);
        }
    }
    
    

    /**
     * Returns a discussion within a given forum.
     *
     * @param forum the forum.
     * @param path the pathname of the discussion relative to forum.
     * @return a discussion resource, or <code>null</code> if not found.
     */
    public DiscussionResource getDiscussion(ForumResource forum, String path)
        throws ForumException
    {
        Resource[] res = resourceService.getStore().getResourceByPath(forum, path);
        if(res.length == 1)
        {
            return (DiscussionResource)res[0];
        }
        else
        {
            return null;
        }
    }

    /**
     * Return the forum of a specific site.
     *
     * @param site the site resource.
     * @return the forum root resource.
     * @throws ForumException.
     */
    public ForumResource getForum(SiteResource site)
        throws ForumException
    {
        Resource[] roots = resourceService.getStore().getResource(site, "applications");
        if(roots == null || roots.length != 1)
        {
            throw new ForumException("Applications root for site: "+site.getName()+" not found");
        }
        roots = resourceService.getStore().getResource(roots[0], "forum");
        if(roots == null || roots.length != 1)
        {
            throw new ForumException("Forum resource for site: "+site.getName()+" not found");
        }
        return (ForumResource)roots[0];
    }

    /**
     * Return the messages in a discussion as a flat list.
     *
     * @param discussion the discussion resource.
     * @param subject the subject.
     * @return the messages list.
     * @throws ForumException.
     */
    public List listMessages(DiscussionResource discussion, Subject subject)
        throws ForumException
    {
        List target = new ArrayList();
        getSubPost(discussion,target);
        return target;
    }
    
    /**
     * Returns the initial state of commentaries created on demand.
     */
    public StateResource getInitialCommentaryState(ForumResource forum)
        throws ForumException
    {
        if(forum.getInitialCommentaryState() != null)
        {
            return forum.getInitialCommentaryState();
        }
        else
        {
            try
            {
                return (StateResource)resourceService.getStore().getUniqueResourceByPath(
                    "/cms/workflow/automata/forum.discussion/states/moderated");
            }
            catch(Exception e)
            {
                throw new ForumException("failed to lookup default state", e);
            }
        }
    }

    /**
     * Sets the initial state of commentaries created on demand.
     */
    public void setInitialCommentaryState(ForumResource forum, StateResource state, Subject subject)
        throws ForumException
    {
        try
        {
            forum.setInitialCommentaryState(state);
            forum.update(subject);
        }
        catch(Exception e)
        {
            throw new ForumException("failed to set default state", e);
        }
    }

    // implementation ////////////////////////////////////////////////////////

    /**
     * Unroll a tree into a flat list
     *
     * @param resource the (partial) tree root.
     * @param target the node list.
     */
    private void getSubPost(Resource resource, List target)
        throws ForumException
    {
        Resource[] resources = resourceService.getStore().getResource(resource);
        for(int i = 0; i< resources.length; i++)
        {
            target.add(resources[i]);
            getSubPost(resources[i], target);
        }
    }

    /**
     * Prepare a location for creating a new resource.
     *
     * TODO: this method could me moved to some sort of ARLUtils class
     *
     * <p>This method creates resources of type <code>node</code> that are
     * neccessary for creating a resource at the specific path.
     *
     * @param rs the ResourceService.
     * @param resource the path is considered to be relative to this resource,
     *        <code>null</code> for resource #1.
     * @param path the path.
     * @param subject the subject that performs the operation.
     * @return the immediate parent of the resource described by the pathname
     */
    private Resource preparePath(ResourceService rs, Resource resource, String path,
                               Subject subject)
        throws AmbigousNameException
    {
        StringTokenizer st = new StringTokenizer(path, "/");
        if(resource == null)
        {
            try
            {
                resource = rs.getStore().getResource(1L);
            }
            catch(EntityDoesNotExistException e)
            {
                throw new BackendException("root resource not found");
            }
        }
        Resource orig = resource;
        ArrayList tokens = new ArrayList();
        while(st.hasMoreTokens())
        {
            tokens.add(st.nextToken());
        }

        for(int i=0; i<tokens.size() - 1; i++)
        {
            String nc = (String)tokens.get(i);
            Resource[] res = rs.getStore().getResource(resource, nc);
            if(res.length == 0)
            {
                try
                {
                    resource = NodeResourceImpl.createNodeResource(rs, nc, resource, subject);
                }
                catch(ValueRequiredException e)
                {
                    throw new BackendException("Unexpected ARL exception", e);
                }
            }
            else if(res.length > 1)
            {
                throw new AmbigousNameException("pathname "+orig.getPath()+
                                                "/"+path+" is amibigous");
            }
            else
            {
                resource = res[0];
            }
        }
        return resource;
    }
    
	public void messageAccepted(MessageResource message, Subject subject)
		throws ForumException
	{
		addLastAdded(message.getDiscussion(), message, subject);
		addLastAdded((ForumNodeResource)message.getDiscussion().getParent(), message, subject);
		addLastAdded(message.getDiscussion().getForum(), message, subject);
	}
	
	public void addLastAdded(ForumNodeResource node, MessageResource message, Subject subject)
		throws ForumException
	{
		List list = node.getLastlyAdded();
        log.debug("Forum service: addLastAdded: node "+node.getIdString()+" for message: "
                  +message);
		int size = node.getLastlyAddedSize(10);
		LinkedList fifo = new LinkedList();
		if(list != null)
		{
            log.debug("Forum service: printListStatus: list size = "+list.size());
            for(int i = 0; i < list.size(); i++)
            {
                Object ob = list.get(i);
                if(ob != null && ob instanceof MessageResource)
                {
                    fifo.add(ob);
                }    
            }
        }
        log.debug("Forum service: addLastAdded: print fifo status before action");
        printListStatus(fifo);
		while(fifo.size() > size)
		{
			fifo.removeLast();
		}
        log.debug("Forum service: addLastAdded: print fifo status after cut");
        printListStatus(fifo);        
		if(message != null)
		{
            log.debug("Forum service: addLastAdded: adding message "+message.getIdString());
			if(fifo.size() == size)
			{
                log.debug("Forum service: addLastAdded: fifo size exceed max size: "+
                          fifo.size()+" > "+size);
				fifo.removeLast();
			}
			fifo.addFirst(message);
		}
        log.debug("Forum service: addLastAdded: fifo after update:");
        printListStatus(fifo);        
		list = new WeakResourceList(fifo);
		node.setLastlyAdded(list);
		node.update(subject);
        log.debug("Forum service: addLastAdded: fifo from resource:");
        printListStatus(node.getLastlyAdded());
	}
	
    private void printListStatus(List list)
    {
        log.debug("Forum service: printListStatus: list size = "+list.size());
        for(int i = 0; i< list.size(); i++)
        {
            Object obj = list.get(i);
            if(obj == null)
            {
                log.debug("Forum service: printListStatus: obj "+i+
                          " = null");
                continue;                          
            }
            if(obj instanceof MessageResource)
            {
                log.debug("Forum service: printListStatus: obj "+i+
                          " is message with id = "+((Resource)obj).getIdString());
                continue;
            }
            if(obj instanceof Resource)
            {
                log.debug("Forum service: printListStatus: obj "+i+
                          " is not message but other resource with id = "+((Resource)obj).getIdString());
            }
            log.debug("Forum service: printListStatus: obj "+i+ 
                      " object class = "+obj.getClass().getName());
        }
    }
    
	// state change listener
	
	
	public void stateChanged(Role role, StatefulResource resource)
	{
        log.debug("Forum service: state change listener: resource "+resource.getIdString()+" changed");
		if(resource instanceof MessageResource)
		{
            log.debug("Forum service: state change listener: resource "+resource.getIdString()+
                      " recognized as forum message in state: "+
            resource.getState().getName());
			if(resource.getState().getName().equals("visible"))
			{
				try
				{
					messageAccepted((MessageResource)resource, rootSubject);
				}
				catch(Exception e)
				{
					log.error("Couldn't add to the last added queue", e);
				}
			}
		}
	}
}

