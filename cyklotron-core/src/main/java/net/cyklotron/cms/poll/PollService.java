package net.cyklotron.cms.poll;

import java.util.Map;

import net.cyklotron.cms.site.SiteResource;

import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;

/**
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: PollService.java,v 1.3 2005-02-09 19:23:11 rafal Exp $
 */
public interface PollService
{
    /** The name of the service (<code>"poll"</code>). */
    public final static String SERVICE_NAME = "poll";

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
     * checks whether the poll has been voted by user 
     * 
     * @param httpContext the http context of the request.
     * @param poll the poll
     * @return <code>true</code> if already voted.
     * @throws PollException if anything goes wrong.
     */
    public boolean hasVoted(HttpContext httpContext, 
        TemplatingContext templatingContext, PollResource poll)
        throws PollException;

	/**
	 * @param poll
	 * @param questions
	 * @param resultMap
	 * @param percentMap
	 */
	public void prepareMaps(CoralSession coralSession, PollResource poll, Map questions, Map resultMap, Map percentMap);

    /**
     * Return the poll relation.
     * 
     * @param coralSession the coral session.
     * @return the poll relation.
     */
    public Relation getRelation(CoralSession coralSession);
}
