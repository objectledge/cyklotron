package net.cyklotron.cms.poll.internal;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.Cookie;

import net.cyklotron.cms.poll.AnswerResource;
import net.cyklotron.cms.poll.PollException;
import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PollsResourceImpl;
import net.cyklotron.cms.poll.PoolResource;
import net.cyklotron.cms.poll.PoolResourceImpl;
import net.cyklotron.cms.poll.QuestionResource;
import net.cyklotron.cms.poll.util.Answer;
import net.cyklotron.cms.poll.util.Question;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.services.workflow.ProtectedTransitionResource;
import net.cyklotron.services.workflow.WorkflowException;
import net.cyklotron.services.workflow.WorkflowService;
import net.labeo.services.BaseService;
import net.labeo.services.InitializationError;
import net.labeo.services.defaults.PersonalDataService;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.generic.CrossReference;
import net.labeo.services.resource.query.QueryResults;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.RunData;

/**
 * Implementation of Poll Service
 *
 * @author <a href="mailto:publo@ngo.pl">Pawel Potempski</a>
 * @version $Id: PollServiceImpl.java,v 1.1 2005-01-12 20:44:30 pablo Exp $
 */
public class PollServiceImpl
    extends BaseService
    implements PollService
{
    // instance variables ////////////////////////////////////////////////////

    /** logging facility */
    private LoggingFacility log;

    /** resource service */
    private ResourceService resourceService;

    /** site serive */
    private SiteService siteService;

    /** workflow service */
    private WorkflowService workflowService;
    
    /** pds */
    private PersonalDataService pds;

    /** master admin */
    private Subject subject;


    // initialization ////////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public void init()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
            getFacility(LOGGING_FACILITY);
        resourceService = (ResourceService)broker.getService(ResourceService.SERVICE_NAME);
        siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
        workflowService = (WorkflowService)broker.getService(WorkflowService.SERVICE_NAME);
        pds = (PersonalDataService)broker.getService(PersonalDataService.SERVICE_NAME);
        try
        {
            subject = resourceService.getSecurity().getSubject(Subject.ROOT);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new InitializationError("Couldn't find root subject");
        }
    }

    /**
     * return the polls root resource.
     *
     * @param site the site resource.
     * @return the polls root resource.
     * @throws PollException.
     */
    public PollsResource getPollsRoot(SiteResource site)
        throws PollException
    {
        Resource[] applications = resourceService.getStore().getResource(site, "applications");
        if(applications == null || applications.length != 1)
        {
            throw new PollException("Applications root for site: "+site.getName()+" not found");
        }
        Resource[] roots = resourceService.getStore().getResource(applications[0], "polls");
        if(roots.length == 1)
        {
            return (PollsResource)roots[0];
        }
        if(roots.length == 0)
        {
            try
            {
                PollsResource pollsRoot = PollsResourceImpl.createPollsResource(resourceService, "polls", applications[0], subject);
                pollsRoot.setBindings(new CrossReference());
                pollsRoot.update(subject);
                return pollsRoot;
            }
            catch(ValueRequiredException e)
            {
                throw new PollException("Couldn't create polls root node");
            }
        }
        throw new PollException("Too much polls root resources for site: "+site.getName());
    }

    /**
     * return the poll for poll pool with logic based on specified configuration.
     *
     * @param pollsResource the polls pool.
     * @param config the configuration.
     * @return the poll resource.
     * @throws PollException.
     */
    public PollResource getPoll(PollsResource pollsResource, Configuration config)
        throws PollException
    {
        long poolId = config.get("pool_id").asLong(-1);
        if(poolId != -1)
        {
            PoolResource poolResource = null;
            try
            {
                poolResource = PoolResourceImpl.getPoolResource(resourceService, poolId);
                return getPoll(poolResource);
            }
            catch(EntityDoesNotExistException e)
            {
                throw new PollException("Pool not found",e);
            }
        }
        return null;
    }

    /**
     * return the poll content for indexing purposes.
     *
     * @param pollResource the poll.
     * @return the poll content.
     */
    public String getPollContent(PollResource pollResource)
    {
        return "";
    }



    /**
     * execute logic of the job to check expiration date.
     */
    public void checkPollState()
    {
		try
		{
			Resource readyState = resourceService.getStore()
				.getUniqueResourceByPath("/cms/workflow/automata/poll.poll/states/ready");
			Resource activeState = resourceService.getStore()
				.getUniqueResourceByPath("/cms/workflow/automata/poll.poll/states/active");				
			QueryResults results = resourceService.getQuery().
				executeQuery("FIND RESOURCE FROM cms.poll.poll WHERE state = "+readyState.getIdString());
			Resource[] nodes = results.getArray(1);
			log.debug("CheckPollState "+nodes.length+" ready polls found");
			for(int i = 0; i < nodes.length; i++)
			{
				checkPollState((PollResource)nodes[i]);
			}
			results = resourceService.getQuery()
				.executeQuery("FIND RESOURCE FROM cms.poll.poll WHERE state = "+activeState.getIdString());
			nodes = results.getArray(1);
			log.debug("CheckPollState "+nodes.length+" active polls found");
			for(int i = 0; i < nodes.length; i++)
			{
				checkPollState((PollResource)nodes[i]);
			}
		}
		catch(Exception e)
		{
			log.error("CheckBannerState exception ",e);
		}
    	
    	/*
        SiteResource[] sites = siteService.getSites();
        for(int i=0; i < sites.length; i++)
        {
            try
            {
                PollsResource pollsRoot = getPollsRoot(sites[i]);
                Resource[] polls = resourceService.getStore().getResource(pollsRoot);
                for(int j = 0; j < polls.length; j++)
                {
                    if(polls[j] instanceof PollResource)
                    {
                        checkPollState((PollResource)polls[j]);
                    }
                }
            }
            catch(PollException e)
            {
                //simple the site has no poll application
                //do nothing.
            }
        }
        */
    }

    public boolean hasVoted(RunData data, PollResource poll)
	throws PollException
	{
	    try
	    {
	        if(data.getContext().get("already_voted") != null && ((Boolean)data.getContext().get("already_voted")).booleanValue())
	        {
	            return true;
	        }
	        if(poll == null)
	        {
	            return false;
	        }
	        String cookieKey = "poll_"+poll.getIdString();
	        Cookie[] cookies = data.getRequest().getCookies();
	        if(cookies != null)
	        {
	            for(int i=0; i<cookies.length; i++)
	            {
	                if(cookies[i].getName().equals(cookieKey))
	                {
	                    return true;
	                }
	            }
	        }
	        return false;
	    }
	    catch(Exception e)
	    {
	    	throw new PollException("exception occured", e);
	    }
	}
    
	/**
	 * @param poll
	 * @param questions
	 * @param resultMap
	 * @param percentMap
	 */
	public void prepareMaps(PollResource poll, Map questions, Map resultMap, Map percentMap) {
		Resource[] questionResources = resourceService.getStore().getResource(poll);
		for(int i = 0; i < questionResources.length; i++)
		{
		    QuestionResource questionResource = (QuestionResource)questionResources[i];
		    Question question = new Question(questionResource.getName(),questionResource.getId());
		    questions.put(new Integer(questionResource.getSequence()),question);
		    Resource[] answerResources = resourceService.getStore().getResource(questionResources[i]);
		    for(int j = 0; j < answerResources.length; j++)
		    {
		        AnswerResource answerResource = (AnswerResource)answerResources[j];
		        Answer answer = new Answer(answerResource.getName(),answerResource.getId());
		        question.getAnswers().put(new Integer(answerResource.getSequence()),answer);
		        Long id = answerResource.getIdObject();
		        resultMap.put(id, new Integer(answerResource.getVotesCount()));
		        if(questionResource.getVotesCount() > 0)
		        {
		        	percentMap.put(id,new Float(answerResource.getVotesCount()/questionResource.getVotesCount()*100));
		        }
		        else
		        {
		        	percentMap.put(id,new Float(0));
		        }
		    }
		}
	}
    
    
    
    /**
     * return the poll for poll pool with logic based on specified configuration.
     *
     * @param pollsResource the polls pool.
     * @param config the configuration.
     * @return the poll resource.
     * @throws PollException.
     */
    private PollResource getPoll(PoolResource poolResource)
        throws PollException
    {
        Resource[] polls = ((PollsResource)poolResource.getParent()).getBindings().get(poolResource);
        ArrayList active = new ArrayList();
        PollResource pollResource = null;
        PollResource temp = null;
        for(int i =0; i < polls.length; i++)
        {
            temp = (PollResource)polls[i];
            if(temp.getState().getName().equals("active"))
            {
                active.add(temp);
            }
        }
        if(active.size()==0)
        {
            return null;
        }
        pollResource = (PollResource)active.get(0);
        for(int i=1; i < active.size(); i++)
        {
            temp = (PollResource)active.get(i);
            if(temp.getEndDate().before(pollResource.getEndDate()))
            {
                pollResource = temp;
            }
        }
        return pollResource;
    }

    
    // private methods


    
    /**
     * check state of the poll and expire it if the end date was reached.
     */
    private void checkPollState(PollResource pollResource)
    {
        try
        {
            Date today = Calendar.getInstance().getTime();
            ProtectedTransitionResource[] transitions = workflowService.getAllowedTransitions(pollResource, subject);
            String state = pollResource.getState().getName();
            ProtectedTransitionResource transition = null;

            if(state.equals("ready"))
            {
                if(today.after(pollResource.getEndDate()))
                {
                    for(int i = 0; i < transitions.length; i++)
                    {
                        if(transitions[i].getName().equals("expire_ready"))
                        {
                            transition = transitions[i];
                            break;
                        }
                    }
                    workflowService.performTransition(pollResource, transition, subject);
                    return;
                }
                if(today.after(pollResource.getStartDate()))
                {
                    for(int i = 0; i < transitions.length; i++)
                    {
                        if(transitions[i].getName().equals("activate"))
                        {
                            transition = transitions[i];
                            break;
                        }
                    }
                    workflowService.performTransition(pollResource, transition, subject);
                    return;
                }
            }
            if(state.equals("active"))
            {
                if(today.after(pollResource.getEndDate()))
                {
                    for(int i = 0; i < transitions.length; i++)
                    {
                        if(transitions[i].getName().equals("expire_active"))
                        {
                            transition = transitions[i];
                            break;
                        }
                    }
                    workflowService.performTransition(pollResource, transition, subject);
                    return;
                }
            }
        }
        catch(WorkflowException e)
        {
            log.error("Poll Job Exception",e);
        }

    }

}

