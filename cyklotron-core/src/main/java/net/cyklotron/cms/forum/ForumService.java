package net.cyklotron.cms.forum;

import java.util.List;

import net.labeo.services.Service;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.services.workflow.StateResource;

/**
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ForumService.java,v 1.1 2005-01-12 20:45:07 pablo Exp $
 */
public interface ForumService 
    extends Service
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
     * @param subject the subject that performs the operation.
     * @return a ForumResource object.
     */
    public ForumResource createForum(SiteResource site, Subject mailboxOwner, 
                                     Subject subject)
        throws ForumException;

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
        throws ForumException;

    /**
     * Creates a new commentary.
     *
     * @param forum the forum to create discussion in.
     * @param path the pathanme of the discussion relative to forum.
     * @param resource the resource the commentary is on.
     * @param admin the discussion's administrator.
     * @param subject the subject that performs the operation.
     */
    public CommentaryResource createCommentary(ForumResource forum, String path,
                                               Resource resource,
                                               Subject admin, Subject subject)
        throws ForumException;
    
    /**
     * Returns a discussion within a given forum.
     *
     * @param forum the forum.
     * @param path the pathname of the discussion relative to forum.
     * @return a discussion resource, or <code>null</code> if not found.
     */
    public DiscussionResource getDiscussion(ForumResource forum, String path)
        throws ForumException;

    /**
     * Return the forum of a specific site.
     *
     * @param site the site resource.
     * @return the forum root resource.
     * @throws ForumException.
     */
    public ForumResource getForum(SiteResource site)
        throws ForumException;
    
    /**
     * Return the messages as a flat list.
     *
     * @param discussion the discussion resource.
     * @param subject the subject.
     * @return the messages list.
     * @throws ForumException.
     */    
    public List listMessages(DiscussionResource discussion, Subject subject)
        throws ForumException;
        
    /**
     * Returns the initial state of commentaries created on demand.
     */
    public StateResource getInitialCommentaryState(ForumResource forum)
        throws ForumException;

    /**
     * Sets the initial state of commentaries created on demand.
     */
    public void setInitialCommentaryState(ForumResource forum, StateResource state, Subject subject)
        throws ForumException;
        
    public void messageAccepted(MessageResource message, Subject subject)
    	throws ForumException;
    	
	public void addLastAdded(ForumNodeResource node, MessageResource message, Subject subject)
		throws ForumException;
}
