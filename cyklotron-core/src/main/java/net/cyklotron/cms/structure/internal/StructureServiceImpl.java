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
import net.cyklotron.cms.workflow.StateResource;
import net.cyklotron.cms.workflow.TransitionResource;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.AmbigousEntityNameException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.schema.CircularDependencyException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.utils.StringUtils;

/**
 * Implementation of Structure Service
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailto:publo@ngo.pl">Pawel Potempski</a>
 * @version $Id: StructureServiceImpl.java,v 1.2 2005-01-18 13:20:50 pablo Exp $
 */
public class StructureServiceImpl
    implements StructureService
{
    /** logging facility */
    private Logger log;

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
    public StructureServiceImpl(Configuration config, Logger logger, 
        WorkflowService workflowService, SecurityService cmsSecurityService)
    {
        this.log = logger;
        this.workflowService = workflowService;
        this.cmsSecurityService = cmsSecurityService;
        invalidNodeErrorScreen = config.getChild("invalidNodeErrorScreen").getValue("InvalidNodeError");
        enableWorkflow = config.getChild("enable_workflow").getValueAsBoolean(false);
    }

    /**
     * Returns the root navigation node of a site.
     *
     * @param site the site.
     * @return the root navigation node.
     */
    public NavigationNodeResource getRootNode(CoralSession coralSession, SiteResource site)
        throws StructureException
    {
        Resource res[] = coralSession.getStore().getResource(site, "structure");
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

        Resource structureNodes[] = coralSession.getStore().getResource(res[0]);
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
    public DocumentNodeResource addDocumentNode(CoralSession coralSession, String name, String title, Resource parent,
                                                Subject subject)
    throws StructureException
    {
        Resource[] resources = coralSession.getStore().getResource(parent, name);
        if(resources.length > 0)
        {
            throw new NavigationNodeAlreadyExistException(
                "the node '"+name+"' already exists under node path="+parent.getPath());
        }
        Parameters preferences = new DefaultParameters();
        SiteResource site = ((NavigationNodeResource)parent).getSite();

        DocumentNodeResource node = null;
        try
        {
            node = DocumentNodeResourceImpl.createDocumentNodeResource(
                coralSession, name, parent, title, site, preferences);
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
    public void deleteNode(CoralSession coralSession, NavigationNodeResource node, Subject subject)
        throws StructureException
    {
        // WARN: We may not remove site's root node.
        if(getRootNode(coralSession, node.getSite()) == node)
        {
            throw new StructureException("Cannot remove root node");
        }

        try
        {
            cmsSecurityService.deleteRole(coralSession, "cms.structure.administrator", node, false);
            cmsSecurityService.deleteRole(coralSession, "cms.structure.editor", node, false);
            cmsSecurityService.deleteRole(coralSession, "cms.structure.redactor", node, false);
            cmsSecurityService.deleteRole(coralSession, "cms.structure.visitor", node, false);
            cmsSecurityService.deleteRole(coralSession, "cms.structure.local_visitor", node, false);
            cmsSecurityService.deleteRole(coralSession, "cms.structure.reporter", node, false);
            coralSession.getStore().deleteResource(node);
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
    public void updateNode(CoralSession coralSession, NavigationNodeResource node, String name, Subject subject)
        throws StructureException
    {
        if(!name.equals(node.getName()))
        {
            coralSession.getStore().setName(node, name);
        }
		Permission permission = coralSession.getSecurity().getUniquePermission("cms.structure.modify");
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
						Resource state = coralSession.getStore(). 
							getUniqueResourceByPath("/cms/workflow/automata/structure.navigation_node/states/taken");
						node.setState((StateResource)state);
					}
					catch(AmbigousEntityNameException e)
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
        node.update();
    }

    /**
     * Update navigation node sequence info.
     *
     * @param node the navigation node.
     * @param sequence the sequence of the node.
     * @param subject the subject who performs the action.
     */
    public void updateNodeSequence(CoralSession coralSession, NavigationNodeResource node, int sequence,
                                   Subject subject)
        throws StructureException
    {
        int seq = node.getSequence(-sequence);
        if(sequence != seq)
        {
            node.setSequence(sequence);
            node.update();
        }
    }

    /**
     * Move navigation node to the new location.
     *
     * @param node the navigation node.
     * @param target net new parent node.
     * @param subject the subject who performs the action.
     */
    public void moveNode(CoralSession coralSession, NavigationNodeResource node,
                         NavigationNodeResource newParent, Subject subject)
        throws StructureException
    {
        try
        {
            coralSession.getStore().setParent(node, newParent);
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
    public void enterState(CoralSession coralSession, NavigationNodeResource node, String state, Subject subject)
        throws StructureException
    {
        try
        {
            Resource stateResource = coralSession.getStore().getUniqueResourceByPath("/cms/workflow/automata/structure.navigation_node/states/"+state);
            node.setState((StateResource)stateResource);
            workflowService.enterState(coralSession, node,(StateResource)stateResource);
            node.update();
        }
        catch(WorkflowException e)
        {
            throw new StructureException("WorkflowException occured",e);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new StructureException("WorkflowException occured",e);
        }
        catch(AmbigousEntityNameException e)
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
    public void fireTransition(CoralSession coralSession, NavigationNodeResource node, String transition, Subject subject)
        throws StructureException
    {
        try
        {
            TransitionResource[] transitions = workflowService.getTransitions(coralSession, node.getState());
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
            workflowService.enterState(coralSession, node, transitions[i].getTo());
            node.update();
        }
        catch(WorkflowException e)
        {
            throw new StructureException("WorkflowException occured",e);
        }
    }
    
	public NavigationNodeResource getParent(CoralSession coralSession, Resource root, Date date, Subject subject) 
		throws StructureException
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		String year = ""+calendar.get(Calendar.YEAR);
		String month = ""+(calendar.get(Calendar.MONTH)+1);
		String day = ""+(calendar.get(Calendar.DAY_OF_MONTH));
		day = StringUtils.fillString(day,2,'0');
		month = StringUtils.fillString(month,2,'0');
		Resource[] resources = coralSession.getStore().getResource(root, year);
		Resource yearResource = null;
		if (resources.length > 0)
		{
			yearResource = resources[0];
		}
		else
		{
			yearResource = addDocumentNode(coralSession, year, year, root, subject);
		}
		resources = coralSession.getStore().getResource(yearResource, month);
		Resource monthResource = null;
		if (resources.length > 0)
		{
			monthResource = resources[0];
		}
		else
		{
			monthResource = addDocumentNode(coralSession, month, month, yearResource, subject);
		}
		resources = coralSession.getStore().getResource(monthResource, day);
		if (resources.length > 0)
		{
			return (NavigationNodeResource)resources[0];
		}
		else
		{
			return addDocumentNode(coralSession, day, day, monthResource, subject);
		}
	}
    
    
}

