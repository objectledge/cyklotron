package net.cyklotron.cms.structure;

import java.util.Date;

import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.site.SiteResource;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: StructureService.java,v 1.6 2005-06-06 11:26:42 rafal Exp $
 */
public interface StructureService
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
    public NavigationNodeResource getRootNode(CoralSession coralSession, SiteResource site)
        throws StructureException;

    /**
     * Adds new document node to the structure.
     * @param name the name of the node.
     * @param subject the creator.
     * @param parent the parent node.
     *
     * @return created resource.
     */
    public DocumentNodeResource addDocumentNode(CoralSession coralSession, 
        String name, String title, Resource parent, Subject subject)
        throws StructureException;

    /**
     * Delete the navigation node resource
     *
     * @param node the navigation node.
     * @param subject the subject who performs the action.
     */
    public void deleteNode(CoralSession coralSession, NavigationNodeResource node,
                           Subject subject)
        throws StructureException;

    /**
     * Update navigation node info.
     *
     * @param node the navigation node.
     * @param name node's name.
     * @param updateTimeStamp should custom_modification_time field be updated?
     * @param subject the subject who performs the action.
     * @return <code>true</code> if the update operation causes an automatic state transition.
     */
    public boolean updateNode(CoralSession coralSession, NavigationNodeResource node,
        String name, boolean updateTimeStamp, Subject subject)
        throws StructureException;

    /**
     * Update navigation node sequence info.
     *
     * @param node the navigation node.
     * @param sequence the sequence of the node.
     * @param subject the subject who performs the action.
     */
    public void updateNodeSequence(CoralSession coralSession, NavigationNodeResource node,
                                   int sequence, Subject subject)
        throws StructureException;

    /**
     * Move navigation node to the new location.
     *
     * @param node the navigation node.
     * @param newParent the new parent node.
     * @param subject the subject who performs the action.
     */
    public void moveNode(CoralSession coralSession, NavigationNodeResource node,
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
    public void enterState(CoralSession coralSession, NavigationNodeResource node, String state, Subject subject)
        throws StructureException;

    /**
     * Fire transition.
     *
     * @param node the navigation node.
     * @param transition the name of the transition.
     * @param subject the subject.
     */
    public void fireTransition(CoralSession coralSession, NavigationNodeResource node, String transition, Subject subject)
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
	public NavigationNodeResource getParent(CoralSession coralSession, Resource root, Date date, Subject subject) 
			throws StructureException;
}
