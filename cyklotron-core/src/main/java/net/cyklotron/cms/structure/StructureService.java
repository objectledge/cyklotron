package net.cyklotron.cms.structure;

import java.util.Date;
import java.util.Set;

import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.documents.DocumentAliasResource;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.site.SiteResource;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: StructureService.java,v 1.13 2008-03-15 13:27:18 pablo Exp $
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
    
    /** daily calendar tree structure. */
    public static final String DAILY_CALENDAR_TREE_STRUCTURE = "D";
    
    /** monthly calendar tree structure. */
    public static final String MONTHLY_CALENDAR_TREE_STRUCTURE = "M";
    
    /** yearly calendar tree structure. */
    public static final String YEARLY_CALENDAR_TREE_STRUCTURE = "Y";
    
    /** no calendar tree structure. */
    public static final String NONE_CALENDAR_TREE_STRUCTURE = "N";
    
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
     * Adds a new document alias to the structure.
     * 
     * @param originalDocument the original document.
     * @param name name of the created alias.
     * @param parent the parent of the newly created alias.
     * @param subject alias creator. 
     * 
     * @throws StructureException when there is a problem with workflow transitions for the newly created node.
     * @throws InvalidResourceNameException when name parameter contains not allowed characters or is not unique among sibling nodes. 
     * @throws ValueRequiredException if some of the required parameters are passed with null values.
     */
    public DocumentAliasResource addDocumentAlias(CoralSession coralSession,
        DocumentNodeResource originalDocument, String name, String title,
        NavigationNodeResource parent, Subject subject)
        throws StructureException, ValueRequiredException, InvalidResourceNameException;
    
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
	 * @param mode the mode: (D)aily,(M)onthly,(Y)early
	 * @param subject the creator
	 * @return the parent node
	 * @throws StructureException
	 */        
	public NavigationNodeResource getParent(CoralSession coralSession, Resource root, 
	    Date date, String mode, Subject subject) 
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
    
    /**
     * Find original parent node for document in calendar tree structure.
     *  
     * @param node the node being inspected
     * @param mode the calendar tree mode
     * @return root node of calendar tree structure
     */
    public NavigationNodeResource getCalendarTreeRoot(NavigationNodeResource node, String mode);
    
    
    /**
     * Get calendar structure type.
     * 
     * @param node the node being inspected.
     * @return type of calendar structure type.
     */
    public String getTimeStructureType(NavigationNodeResource node);
    
    /**
     * Returns the existing aliases referring to a specified document node.
     * 
     * @param node a document node.
     * @return the aliases referring to the specified node.
     * @throws StructureException when alias tracking Coral relation cannot be accessed.
     */
    public Set<DocumentAliasResource> getAliases(CoralSession coralSession,
        DocumentNodeResource node)
        throws StructureException;
    
    /**
     * Returns the node where ProposeDocument screen is deployed in the site.
     * 
     * @param site the site.
     * @return the node, or null when not configured.
     */
    public NavigationNodeResource getProposeDocumentNode(CoralSession coralSession, SiteResource site)
        throws StructureException;

    /**
     * Sets the node where ProposeDocument screen is deployed in the site.
     * 
     * @param site the site.
     * @param node the node.
     */
    public void setProposeDocumentNode(CoralSession coralSession, SiteResource site,
        NavigationNodeResource node)
        throws StructureException;
    
    /**
     * Returns a set of ids of documents that have validityStart at or after the specified date.
     * <p>
     * By contract, documents without specified validity start date are always returned.
     * </p>
     * <p>
     * All times are rounded down to full minute.
     * </p>
     * 
     * @param date a date.
     * @param coralSession TODO
     * @retunrs all documents have validityStart at or after the specified date.
     * @throws StructureException
     */
    public Set<Long> getDocumentsValidAtOrAfter(Date date, CoralSession coralSession)
        throws StructureException;
}
