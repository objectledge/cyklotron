package net.cyklotron.cms.structure;

import java.util.Date;

import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.site.SiteResource;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: StructureService.java,v 1.12 2007-11-18 21:23:43 rafal Exp $
 */
public interface StructureService
{
    /** The name of the service (<code>"structure"</code>). */
    public final static String SERVICE_NAME = "structure";

    /** The logging facility where the service issues it's informational
     * messages. */
    public static final String LOGGING_FACILITY = "structure";
    
    /** Minimum valid node priority. */
    public static final int MIN_PRIORITY = 0;
    
    /** Maximum valid node priority. */
    public static final int MAX_PRIORITY = 9;

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
     * @throws InvalidResourceNameException if the name contains illegal characters.
     */
    public DocumentNodeResource addDocumentNode(CoralSession coralSession, 
        String name, String title, Resource parent, Subject subject)
        throws StructureException, InvalidResourceNameException;

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
     * @throws InvalidResourceNameException if the name contains illegal characters.
     */
    public boolean updateNode(CoralSession coralSession, NavigationNodeResource node,
        String name, boolean updateTimeStamp, Subject subject)
        throws StructureException, InvalidResourceNameException;

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
    
    /**
     * Returns the default node priority.
     * 
     * @return the default node priority.
     */
    public int getDefaultPriority();

    /**
     * Returns the minimum node priority allowed to a subject.
     * 
     * @param coralSession coral session.
     * @param node a node.
     * @param subject a subject.
     * @return minimum allowed document priority.
     */
    public int getMinPriority(CoralSession coralSession, NavigationNodeResource node,
        Subject subject);
    
    /**
     * Returns the maximum node priority allowed to a subject.
     * 
     * @param coralSession coral session.
     * @param node a node.
     * @param subject a subject.
     * @return maximum allowed node priority.
     */
    public int getMaxPriority(CoralSession coralSession, NavigationNodeResource node,
        Subject subject);
    
    /**
     * Returns node priority clamped to allowed range for the subject.
     * 
     * @param coralSession coral session.
     * @param node a node.
     * @param subject a subject. 
     * @param requested a requested priority.
     * @return priority clamped to allowed range.
     */
    public int getAllowedPriority(CoralSession coralSession, NavigationNodeResource node,
        Subject subject, int requested);

    /**
     * Move node to another site.
     * 
     * @param coralSession the coral session.
     * @param srcNode the source node.
     * @param dstNode the destination node.
     */
    public void moveToArchive(CoralSession coralSession, NavigationNodeResource srcNode, 
        NavigationNodeResource dstNode)
        throws StructureException;
    
    /**
     * Get negative category.
     * 
     * @return negative category
     */
    public CategoryResource getNegativeCategory();

    /**
     * Get positive category.
     * 
     * @return positive category
     */
    public CategoryResource getPositiveCategory();
    
    /**
     * Get whether unclassified nodes should be presented on editorial task screen.
     * 
     * @return showUnclassifiedNodes option
     */
    public boolean isShowUnclassifiedNodes();
    
    /**
     * Tell whether site is preconfigured to show unclassified nodes.
     * 
     * @param site the site resource.
     * @return <code>true</code> if showing unclassified nodes is enabled for given site.
     */
    public boolean showUnclassifiedInSite(SiteResource site);
}
