package net.cyklotron.cms.structure;

import java.util.Date;

import net.labeo.services.Service;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.site.SiteResource;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: StructureService.java,v 1.1 2005-01-12 20:44:33 pablo Exp $
 */
public interface StructureService
    extends Service
{
    /** The name of the service (<code>"structure"</code>). */
    public final static String SERVICE_NAME = "structure";

    /** The logging facility where the service issues it's informational
     * messages. */
    public static final String LOGGING_FACILITY = "structure";

    /**
     * Returns the root navigation node of a site.
     *
     * @param site the site.
     * @return the root navigation node.
     */
    public NavigationNodeResource getRootNode(SiteResource site)
        throws StructureException;

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
        throws StructureException;

    /**
     * Delete the navigation node resource
     *
     * @param node the navigation node.
     * @param subject the subject who performs the action.
     */
    public void deleteNode(NavigationNodeResource node,
                           Subject subject)
        throws StructureException;

    /**
     * Update navigation node info.
     *
     * @param node the navigation node.
     * @param name node's name.
     * @param subject the subject who performs the action.
     */
    public void updateNode(NavigationNodeResource node,
                           String name, Subject subject)
        throws StructureException;

    /**
     * Update navigation node sequence info.
     *
     * @param node the navigation node.
     * @param sequence the sequence of the node.
     * @param subject the subject who performs the action.
     */
    public void updateNodeSequence(NavigationNodeResource node,
                                   int sequence, Subject subject)
        throws StructureException;

    /**
     * Move navigation node to the new location.
     *
     * @param node the navigation node.
     * @param newParent the new parent node.
     * @param subject the subject who performs the action.
     */
    public void moveNode(NavigationNodeResource node,
                         NavigationNodeResource newParent, Subject subject)
        throws StructureException;

    /**
     * Returns the screen to render in invalid url case.
     *
     * @return the invalid url error screen.
     */
    public String getInvalidNodeErrorScreen();

    /**
     * Checks whether workflow is enabled.
     *
     * @return <code>true</code> if workflow is enabled.
     */
    public boolean isWorkflowEnabled();
    
    /**
     * Set workflow state.
     *
     * @param node the navigation node.
     * @param state the name of the state.
     * @param subject the subject.
     */
    public void enterState(NavigationNodeResource node, String state, Subject subject)
        throws StructureException;

    /**
     * Fire transition.
     *
     * @param node the navigation node.
     * @param transition the name of the transition.
     * @param subject the subject.
     */
    public void fireTransition(NavigationNodeResource node, String transition, Subject subject)
        throws StructureException;
        
	/**
	 * Create the calendar tree.
	 * 
	 * @param root the root parent resource
	 * @param date the date
	 * @param subject the creator
	 * @return the parent node
	 * @throws StructureException
	 */        
	public NavigationNodeResource getParent(Resource root, Date date, Subject subject) 
			throws StructureException;
}
