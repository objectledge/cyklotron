package net.cyklotron.cms.poll;

import java.util.Map;

import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.Service;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.RunData;

/**
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: PollService.java,v 1.1 2005-01-12 20:45:01 pablo Exp $
 */
public interface PollService
    extends Service
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
     * @throws PollException.
     */
    public PollsResource getPollsRoot(SiteResource site)
        throws PollException;

    /**
     * return the poll for poll pool with logic based on specified configuration.
     *
     * @param pollsResource the polls pool.
     * @param config the configuration.
     * @return the poll resource.
     * @throws PollException.
     */
    public PollResource getPoll(PollsResource pollsResource, Configuration config)
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
    public void checkPollState();

    /**
     * checks whether the vote has been voted by user 
     * 
     * @param data the rundata
     * @param poll the poll
     * @return <code>true</code> if already voted.
     * @throws PollException if anything goes wrong.
     */
    public boolean hasVoted(RunData data, PollResource poll)
		throws PollException;
    
	/**
	 * @param poll
	 * @param questions
	 * @param resultMap
	 * @param percentMap
	 */
	public void prepareMaps(PollResource poll, Map questions, Map resultMap, Map percentMap);
    
}
