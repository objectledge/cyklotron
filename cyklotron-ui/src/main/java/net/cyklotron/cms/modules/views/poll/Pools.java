package net.cyklotron.cms.modules.views.poll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PoolResource;
import net.cyklotron.cms.poll.PoolResourceImpl;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * Stateful screen for forum application.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawe� Potempski</a>
 * @version $Id: Pools.java,v 1.2 2005-01-26 09:00:30 pablo Exp $
 */
public class Pools
    extends BaseSkinableScreen
{
    /** forum serivce. */
    protected PollService pollService;

    
    public Pools(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        StructureService structureService, StyleService styleService, SkinService skinService,
        MVCFinder mvcFinder, TableStateManager tableStateManager,
        PollService pollService)
    {
        super(context, logger, preferencesService, cmsDataFactory, structureService, styleService,
                        skinService, mvcFinder, tableStateManager);
        this.pollService = pollService;
    }
    
    public String getState()
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        long poolId = parameters.getLong("pool_id", -1);
        if(poolId == -1)
        {
            return "PoolList";
        }
        return "PoolView";
    }

    public void preparePoolList(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        try
        {
        	CmsData cmsData = getCmsData();
            PollsResource pollsRoot = pollService.getPollsRoot(coralSession, cmsData.getSite());
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
    
    public void preparePoolView(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
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
            Resource[] pollResources = pollService.getRelation(coralSession).get(pool);
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
                pollService.prepareMaps(coralSession, pollResource, questions, resultMap, percentMap);
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
