package net.cyklotron.cms.forum;

import java.util.List;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.workflow.StateResource;

import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

/**
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ForumService.java,v 1.3 2005-02-09 19:23:18 rafal Exp $
 */
public interface ForumService 
{
    /** The name of the service (<code>"search"</code>). */
    public final static String SERVICE_NAME = "forum2";

    /**
     * The logging facility where the service issues it's informational
     * messages.
     */
    public static final String LOGGING_FACILITY = "forum2";

    /**
     * Creates forum associated with a site.
     *
     * @param site the site.
     * @param mailboxOwner the owner of the mailbox where mailng lists will
     * reside.
     * @return a ForumResource object.
     */
    public ForumResource createForum(CoralSession coralSession, SiteResource site, Subject mailboxOwner)
        throws ForumException;

    /**
     * Creates a new discussion.
     *
     * @param forum the forum to create discussion in.
     * @param path the pathanme of the discussion relative to forum.
     */
    public DiscussionResource createDiscussion(CoralSession coralSession, ForumResource forum, String path)
        throws ForumException;

    /**
     * Creates a new commentary.
     *
     * @param forum the forum to create discussion in.
     * @param path the pathanme of the discussion relative to forum.
     * @param resource the resource the commentary is on.
     */
    public CommentaryResource createCommentary(CoralSession coralSession, ForumResource forum, String path,
                                               Resource resource)
        throws ForumException;
    
    /**
     * Returns a discussion within a given forum.
     *
     * @param forum the forum.
     * @param path the pathname of the discussion relative to forum.
     * @return a discussion resource, or <code>null</code> if not found.
     */
    public DiscussionResource getDiscussion(CoralSession coralSession, ForumResource forum, String path)
        throws ForumException;

    /**
     * Return the forum of a specific site.
     *
     * @param site the site resource.
     * @return the forum root resource.
     * @throws ForumException if the operation fails..
     */
    public ForumResource getForum(CoralSession coralSession, SiteResource site)
        throws ForumException;
    
    /**
     * Return the messages as a flat list.
     *
     * @param discussion the discussion resource.
     * @return the messages list.
     * @throws ForumException if the operation fails.
     */    
    public List listMessages(CoralSession coralSession, DiscussionResource discussion)
        throws ForumException;
        
    /**
     * Returns the initial state of commentaries created on demand.
     */
    public StateResource getInitialCommentaryState(CoralSession coralSession, ForumResource forum)
        throws ForumException;

    /**
     * Sets the initial state of commentaries created on demand.
     */
    public void setInitialCommentaryState(CoralSession coralSession, ForumResource forum, StateResource state)
        throws ForumException;
        
    public void messageAccepted(CoralSession coralSession, MessageResource message)
    	throws ForumException;
    	
	public void addLastAdded(CoralSession coralSession, ForumNodeResource node, MessageResource message)
		throws ForumException;
}
