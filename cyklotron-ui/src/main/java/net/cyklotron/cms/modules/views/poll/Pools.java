package net.cyklotron.cms.modules.views.poll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.cyklotron.cms.poll.AnswerResource;
import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PoolResource;
import net.cyklotron.cms.poll.PoolResourceImpl;
import net.cyklotron.cms.poll.QuestionResource;
import net.cyklotron.cms.poll.util.Answer;
import net.cyklotron.cms.poll.util.Question;
import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Resource;
import net.labeo.services.table.TableService;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Stateful screen for forum application.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawe� Potempski</a>
 * @version $Id: Pools.java,v 1.1 2005-01-24 04:34:26 pablo Exp $
 */
public class Pools
    extends BaseSkinableScreen
{
    /** forum serivce. */
    protected PollService pollService;

    /** table service for hit list display. */
    protected TableService tableService;

    /** logging facility */
    protected Logger log;
    
    public Pools()
    {
        super();
        pollService = (PollService)broker.getService(PollService.SERVICE_NAME);
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
            getFacility(ForumService.LOGGING_FACILITY);
    }
    
    public String getState(RunData data)
        throws ProcessingException
    {
        long poolId = parameters.getLong("pool_id", -1);
        if(poolId == -1)
        {
            return "PoolList";
        }
        return "PoolView";
    }

    public void preparePoolList(RunData data, Context context)
        throws ProcessingException
    {
        try
        {
        	CmsData cmsData = getCmsData();
            PollsResource pollsRoot = pollService.getPollsRoot(cmsData.getSite());
            Resource[] resources = coralSession.getStore().getResource(pollsRoot);
            List pools = new ArrayList();
            HashMap hasPolls = new HashMap();
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof PoolResource)
                {
                    pools.add(resources[i]);
                }
            }
            templatingContext.put("pools",pools);
        }
        catch(Exception e)
        {
            screenError(getNode(), context, "Screen exception: "+e);
        }
    }
    
    public void preparePoolView(RunData data, Context context)
        throws ProcessingException
    {
        long poolId = parameters.getLong("pool_id", -1);
        if(poolId == -1)
        {
            screenError(getNode(), context, "pool id not found");
            return;
        }
        try
        {
            PoolResource pool = PoolResourceImpl.getPoolResource(coralSession, poolId);
            templatingContext.put("pool",pool);
            PollsResource pollsRoot = (PollsResource)pool.getParent();
            Resource[] pollResources = pollsRoot.getBindings().get(pool);
            templatingContext.put("polls",Arrays.asList(pollResources));
            Map questionsX = new HashMap();
            Map resultMapX= new HashMap();
            Map percentMapX= new HashMap();
            Map questionKeysX = new HashMap();
            for(int p = 0; p < pollResources.length; p++)
            {
                Map questions = new HashMap();
                Map resultMap= new HashMap();
                Map percentMap= new HashMap();
                PollResource pollResource = (PollResource)pollResources[p];
                pollService.prepareMaps(pollResource, questions, resultMap, percentMap);
                List questionKeys = new ArrayList();
                for(int i = 0; i< questions.size(); i++)
                {
                    questionKeys.add(new Integer(i));
                }
                questionKeysX.put(pollResource,questionKeys);
                resultMapX.put(pollResource,resultMap);
                percentMapX.put(pollResource,percentMap);
                questionsX.put(pollResource,questions);
            }
            templatingContext.put("questionKeysX",questionKeysX);
            templatingContext.put("resultsX",resultMapX);
            templatingContext.put("percentX",percentMapX);
            templatingContext.put("questionsX",questionsX);
        }
        catch(Exception e)
        {
            screenError(getNode(), context, "Screen exception: "+e);
        }
    }
}
