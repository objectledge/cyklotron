package net.cyklotron.cms.structure.internal;

import java.util.Calendar;
import java.util.Date;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentNodeResourceImpl;
import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeAlreadyExistException;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.services.workflow.StateResource;
import net.cyklotron.services.workflow.TransitionResource;
import net.cyklotron.services.workflow.WorkflowException;
import net.cyklotron.services.workflow.WorkflowService;
import net.labeo.services.BaseService;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.AmbigousNameException;
import net.labeo.services.resource.CircularDependencyException;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.BaseParameterContainer;
import net.labeo.util.configuration.ParameterContainer;

/**
 * Implementation of Structure Service
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailto:publo@ngo.pl">Pawel Potempski</a>
 * @version $Id: StructureServiceImpl.java,v 1.1 2005-01-12 20:45:11 pablo Exp $
 */
public class StructureServiceImpl
    extends BaseService
    implements StructureService
{
    /** logging facility */
    private LoggingFacility log;

    /** resource service */
    private ResourceService resourceService;

    /** cms security service */
    private SecurityService cmsSecurityService;

    /** workflow service */
    private WorkflowService workflowService;
    
    /** invalid node error screen */
    private String invalidNodeErrorScreen;

    /** workflow switch */
    private boolean enableWorkflow;
    
    /**
     * Initializes the service.
     */
    public void init()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(LOGGING_FACILITY);
        resourceService = (ResourceService)broker.getService(ResourceService.SERVICE_NAME);
        cmsSecurityService = (SecurityService)broker.getService(SecurityService.SERVICE_NAME);
        workflowService = (WorkflowService)broker.getService(WorkflowService.SERVICE_NAME);
        invalidNodeErrorScreen = config.get("invalidNodeErrorScreen").asString("InvalidNodeError");
        enableWorkflow = config.get("enable_workflow").asBoolean(false);
    }

    /**
     * Returns the root navigation node of a site.
     *
     * @param site the site.
     * @return the root navigation node.
     */
    public NavigationNodeResource getRootNode(SiteResource site)
        throws StructureException
    {
        Resource res[] = resourceService.getStore().getResource(site, "structure");
        if(res.length == 0)
        {
            throw new StructureException("structure data not found for site "+
                                         site.getName());
        }

        if(res.length > 1)
        {
            throw new StructureException("multiple structure roots for site "+
                                         site.getName());
        }

        Resource structureNodes[] = resourceService.getStore().getResource(res[0]);
        if(structureNodes.length == 0)
        {
            throw new StructureException("site's root not found for site "+
                                         site.getName());
        }

        if(structureNodes.length > 1)
        {
            throw new StructureException("multiple root nodes for site "+
                                         site.getName());
        }

        return (NavigationNodeResource)structureNodes[0];
    }

    /**
     * Adds new document node to the structure.
     * @param name the name of the node.
     * @param subject the creator.
     * @param parentId the id of the parent node.
     *
     * @return created resource.
     */
    public DocumentNodeResource addDocumentNode(String name, String title, Resource parent,
                                                Subject subject)
    throws StructureException
    {
        Resource[] resources = resourceService.getStore().getResource(parent, name);
        if(resources.length > 0)
        {
            throw new NavigationNodeAlreadyExistException(
                "the node '"+name+"' already exists under node path="+parent.getPath());
        }
        
        ParameterContainer preferences = new BaseParameterContainer();
        SiteResource site = ((NavigationNodeResource)parent).getSite();

        DocumentNodeResource node = null;
        try
        {
            node = DocumentNodeResourceImpl.createDocumentNodeResource(
                resourceService, name, parent, title, site, preferences, subject);
        }
        catch(ValueRequiredException e)
        {
            throw new StructureException("Required attribute value was not set.",e);
        }
        return node;
    }

    /**
     * Delete the navigation node resource
     *
     * @param node the navigation node.
     * @param subject the subject who performs the action.
     */
    public void deleteNode(NavigationNodeResource node, Subject subject)
        throws StructureException
    {
        // WARN: We may not remove site's root node.
        if(getRootNode(node.getSite()) == node)
        {
            throw new StructureException("Cannot remove root node");
        }

        try
        {
            cmsSecurityService.deleteRole("cms.structure.administrator", node, subject, false);
            cmsSecurityService.deleteRole("cms.structure.editor", node, subject, false);
            cmsSecurityService.deleteRole("cms.structure.redactor", node, subject, false);
            cmsSecurityService.deleteRole("cms.structure.visitor", node, subject, false);
            cmsSecurityService.deleteRole("cms.structure.local_visitor", node, subject, false);
            cmsSecurityService.deleteRole("cms.structure.reporter", node, subject, false);
            resourceService.getStore().deleteResource(node);
        }
        catch(CmsSecurityException e)
        {
            throw new StructureException("Security Exception",e);
        }
        catch(EntityInUseException e)
        {
            throw new StructureException("Node is in use or does not exist",e);
        }
    }

    /**
     * Update navigation node info.
     *
     * @param node the navigation node.
     * @param name the name of the node.
     * @param subject the subject who performs the action.
     */
    public void updateNode(NavigationNodeResource node, String name, Subject subject)
        throws StructureException
    {
        if(!name.equals(node.getName()))
        {
            resourceService.getStore().setName(node, name);
        }
		Permission permission = resourceService.getSecurity().getUniquePermission("cms.structure.modify");
		if(!subject.hasPermission(node, permission) && isWorkflowEnabled())
		{
			if(node.getState()!=null)
			{
				String stateName = node.getState().getName();
				if(stateName != null &&
						("accepted".equals(stateName) || 
						 "published".equals(stateName) ||
						 "expired".equals(stateName) || 
						 "prepared".equals(stateName)))
				{
					try
					{
						Resource state = resourceService.getStore(). 
							getUniqueResourceByPath("/cms/workflow/automata/structure.navigation_node/states/taken");
						node.setState((StateResource)state);
					}
					catch(AmbigousNameException e)
					{
						throw new StructureException("cannot find the workflow state", e);
					}
					catch(EntityDoesNotExistException e)
					{
						throw new StructureException("cannot find the workflow state", e);
					}
				}
			}
		}
        node.update(subject);
    }

    /**
     * Update navigation node sequence info.
     *
     * @param node the navigation node.
     * @param sequence the sequence of the node.
     * @param subject the subject who performs the action.
     */
    public void updateNodeSequence(NavigationNodeResource node, int sequence,
                                   Subject subject)
        throws StructureException
    {
        int seq = node.getSequence(-sequence);
        if(sequence != seq)
        {
            node.setSequence(sequence);
            node.update(subject);
        }
    }

    /**
     * Move navigation node to the new location.
     *
     * @param node the navigation node.
     * @param target net new parent node.
     * @param subject the subject who performs the action.
     */
    public void moveNode(NavigationNodeResource node,
                         NavigationNodeResource newParent, Subject subject)
        throws StructureException
    {
        try
        {
            resourceService.getStore().setParent(node, newParent);
        }
        catch(CircularDependencyException e)
        {
            throw new StructureException("Circular dependency exception",e);
        }
    }

    /**
     * Returns the screen to render in invalid url case.
     *
     * @return the invalid url error screen.
     */
    public String getInvalidNodeErrorScreen()
    {
        return invalidNodeErrorScreen;
    }

    /**
     * Checks whether workflow is enabled.
     *
     * @return <code>true</code> if workflow is enabled.
     */
    public boolean isWorkflowEnabled()
    {
        return enableWorkflow;
    }

    /**
     * Set workflow state.
     *
     * @param node the navigation node.
     * @param state the name of the state.
     * @param subject the subject.
     */
    public void enterState(NavigationNodeResource node, String state, Subject subject)
        throws StructureException
    {
        try
        {
            Resource stateResource = resourceService.getStore().getUniqueResourceByPath("/cms/workflow/automata/structure.navigation_node/states/"+state);
            node.setState((StateResource)stateResource);
            workflowService.enterState(node,(StateResource)stateResource);
            node.update(subject);
        }
        catch(WorkflowException e)
        {
            throw new StructureException("WorkflowException occured",e);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new StructureException("WorkflowException occured",e);
        }
        catch(AmbigousNameException e)
        {
            throw new StructureException("WorkflowException occured",e);
        }
    }
    

    /**
     * Fire transition.
     *
     * @param node the navigation node.
     * @param transition the name of the transition.
     * @param subject the subject.
     */
    public void fireTransition(NavigationNodeResource node, String transition, Subject subject)
        throws StructureException
    {
        try
        {
            TransitionResource[] transitions = workflowService.getTransitions(node.getState());
            int i = 0;
            for(; i<transitions.length; i++)
            {
                if(transitions[i].getName().equals(transition))
                {
                    break;
                }
            }
            if(i == transitions.length)
            {
                throw new StructureException("Illegal transition name '"+transition+
                                             "' for navigation node in state '"+node.getState().getName());
                
            }
            node.setState(transitions[i].getTo());
            workflowService.enterState(node, transitions[i].getTo());
            node.update(subject);
        }
        catch(WorkflowException e)
        {
            throw new StructureException("WorkflowException occured",e);
        }
    }
    
	public NavigationNodeResource getParent(Resource root, Date date, Subject subject) 
		throws StructureException
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		String year = ""+calendar.get(Calendar.YEAR);
		String month = ""+(calendar.get(Calendar.MONTH)+1);
		String day = ""+(calendar.get(Calendar.DAY_OF_MONTH));
		day = StringUtils.fillString(day,2,'0');
		month = StringUtils.fillString(month,2,'0');
		Resource[] resources = resourceService.getStore().getResource(root, year);
		Resource yearResource = null;
		if (resources.length > 0)
		{
			yearResource = resources[0];
		}
		else
		{
			yearResource = addDocumentNode(year, year, root, subject);
		}
		resources = resourceService.getStore().getResource(yearResource, month);
		Resource monthResource = null;
		if (resources.length > 0)
		{
			monthResource = resources[0];
		}
		else
		{
			monthResource = addDocumentNode(month, month, yearResource, subject);
		}
		resources = resourceService.getStore().getResource(monthResource, day);
		if (resources.length > 0)
		{
			return (NavigationNodeResource)resources[0];
		}
		else
		{
			return addDocumentNode(day, day, monthResource, subject);
		}
	}
    
    
}

