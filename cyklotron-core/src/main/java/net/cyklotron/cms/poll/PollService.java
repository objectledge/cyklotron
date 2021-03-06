package net.cyklotron.cms.poll;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.cyklotron.cms.site.SiteResource;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.web.HttpContext;

/**
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: PollService.java,v 1.4 2005-02-09 22:20:13 rafal Exp $
 */
public interface PollService
{
    /** The name of the service (<code>"poll"</code>). */
    public final static String SERVICE_NAME = "poll";
    
    /** The name of polls root (<code>"polls"</code>). */
    public final static String POLLS_ROOT_NAME = "polls";
    
    /** The name of pools root (<code>"pools"</code>). */
    public final static String POOLS_ROOT_NAME = "pools";
    
    /** The name of votes root (<code>"votes"</code>). */
    public final static String VOTES_ROOT_NAME = "votes";

    /**
     * The logging facility where the service issues it's informational
     * messages.
     */
    public static final String LOGGING_FACILITY = "poll";

    /**
     * return the polls root node.
     *
     * @param site the site resource.
     * @return the pools root resource.
     * @throws PollException if the operation fails.
     */
    public PollsResource getPollsRoot(CoralSession coralSession, SiteResource site)
        throws PollException;

    /**
     * return the polls root node.
     *
     * @param site the site resource.
     * @param name the polls resource type name.
     * @return the pools root resource.
     * @throws PollException if the operation fails.
     */
    public PollsResource getPollsParent(CoralSession coralSession, SiteResource site, String name)
    throws PollException;

    /**
     * return the polls root node.
     *
     * @param psid polls root resource id.
     * @param name the polls resource type name.
     * @return the pools root resource.
     * @throws PollException if the operation fails.
     * @throws EntityDoesNotExistException if the operation fails.
     */
    public PollsResource getPollsParent(CoralSession coralSession, int psid, String name)
    throws PollException, EntityDoesNotExistException;

    /**
     * return the poll for poll pool with logic based on specified configuration.
     *
     * @param pollsResource the polls pool.
     * @param config the configuration.
     * @return the poll resource.
     * @throws PollException if the operation fails.
     */
    public PollResource getPoll(CoralSession coralSession, PollsResource pollsResource, Parameters config)
        throws PollException;

    /**
     * return the vote resource for configuration.
     *
     * @param config the configuration.
     * @return the vote resource.
     * @throws PollException if the operation fails.
     */
    public VoteResource getVote(CoralSession coralSession, Parameters config)
        throws PollException;

    /**
     * return the poll content for indexing purposes.
     *
     * @param pollResource the poll.
     * @return the poll content.
     */
    public String getPollContent(PollResource pollResource);
    
    /**
     * execute logic of the job to check expiration date.
     */
    public void checkPollState(CoralSession coralSession);

    /**
     * Checks whether the given resource has been voted on by the user.
     * 
     * @param httpContext the HTTP context of the request.
     * @param resource the resource.
     * @return <code>true</code> if already voted.
     */
    public boolean hasVoted(HttpContext httpContext, Resource resource);
    
    /**
     * Tracks the event that the given resource has been voted on by the user.
     * 
     * <p>The information is kept in HTTP session and cookies.</p>
     * @param httpContext the HTTP context of the request.
     * @param resource the resource.
     * @return <code>true</code> if already voted.
     */
    public void trackVote(HttpContext httpContext, Resource resource);

	/**
	 * @param poll
	 * @param questions
	 * @param resultMap
	 * @param percentMap
	 */
	public void prepareMaps(CoralSession coralSession, PollResource poll, Map questions, Map resultMap, Map percentMap);

	   /**
     * @param vote
     * @param answers
     * @param resultMap
     * @param percentMap
     */
    public void prepareVoteMaps(CoralSession coralSession, VoteResource vote, Map answers, Map resultMap, Map percentMap, Map ballotsMap);

    /**
     * Return email set for given VoteResource.
     * 
     * @param coralSession the coral session.
     * @param vote the VoteResource.
     */    
    public Set<String> getBallotsEmails(CoralSession coralSession, VoteResource vote);

    /**
     * Add email address to ballotsEmailsMap.
     * 
     * @param coralSession the coral session.
     * @param vote the VoteResource.
     * @param email the email string.
     */
    public void addBallotEmail(CoralSession coralSession, VoteResource vote, String email);
	
    /**
     * Return the poll relation.
     * 
     * @param coralSession the coral session.
     * @return the poll relation.
     */
    public Relation getRelation(CoralSession coralSession);
    
    // Votes
    
    /**
     * Returns confirmation ticket template
     * 
     * @param vote a VoteResource
     * @param locale locale for looking up fallback template when vote has none defined.
     */
    public Template getVoteConfiramationTicketTemplate(VoteResource vote, Locale locale)
        throws ProcessingException;
    
    /***
     * Returns confirmation ticket template contents
     * 
     * @param vote a VoteResource
     * @param locale TODO
     * @param locale locale for looking up fallback template when vote has none defined.  
     * @return template contents, or default template contents when none defined previously.
     */
    public String getVoteConfiramationTicketContents(VoteResource vote, Locale locale)
        throws ProcessingException;
    
    /***
     * Sets confirmation ticket template contents
     * 
     * @param vote a VoteResource
     * @return template contents.
     */
    public void setVoteConfiramationTicketContents(VoteResource vote, String contents)
        throws ProcessingException;
    
    /**
     * Returns vote base URL.
     * <p>
     * Returns the base URL that should be used for voting actions / ajax views. If {@code <voteBaseUrl>} is defined in service configuration,
     * it will be returned. Otherwise the returned URL will be relative one, built from context and servlet path of the current request.
     * </p>
     * 
     * @param vote base URL or {@code null}.
     */
    public String getVoteBaseUrl(HttpContext httpContext);
    
    /**
     * Returns vote host.
     * <p>
     * Returns the base URL that should be used for voting actions / ajax views. If {@code <voteBaseUrl>} is defined in service configuration,
     * host from this URL will be returned. Otherwise the host from HttpContext is returned.
     * </p>
     * 
     * @param vote base URL or {@code null}.
     */
    public String getVoteHost(HttpContext httpContext);
}
