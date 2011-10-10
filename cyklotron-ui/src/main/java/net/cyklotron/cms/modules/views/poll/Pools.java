package net.cyklotron.cms.modules.views.poll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.CreationTimeComparator;
import org.objectledge.coral.table.comparator.CreatorNameComparator;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.ListTableModel;
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
import net.cyklotron.cms.poll.util.PublicationTimePollComparator;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * Stateful screen for forum application.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawe� Potempski</a>
 * @version $Id: Pools.java,v 1.5 2008-03-27 17:41:09 rafal Exp $
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
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        try
        {
        	CmsData cmsData = getCmsData();
            PollsResource poolsRoot = pollService.getPollsParent(coralSession, cmsData.getSite(), pollService.POOLS_ROOT_NAME);
            Resource[] resources = coralSession.getStore().getResource(poolsRoot);
            List<PoolResource> pools = new ArrayList<PoolResource>();
            HashMap hasPolls = new HashMap();
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof PoolResource)
                {
                    pools.add((PoolResource)resources[i]);
                }
            }
            TableColumn[] columns = new TableColumn[3];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()));
            columns[1] = new TableColumn("creator", new CreatorNameComparator(i18nContext.getLocale()));
            columns[2] = new TableColumn("creation_time", new CreationTimeComparator());
            TableState state = tableStateManager.getState(context, "cms:screens:poll,PoolsPoolList");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
                state.setSortColumnName("creation_time");
                state.setAscSort(false);
            }
            TableModel model = new ListTableModel(pools, columns);
            templatingContext.put("table", new TableTool(state, null, model));
            templatingContext.put("pools",pools);
        }
        catch(Exception e)
        {
            screenError(getNode(), context, "Screen exception", e);
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
            List<Resource> polls = new ArrayList<Resource>(Arrays.asList(pollResources));
            Collections.sort(polls, new PublicationTimePollComparator());
            templatingContext.put("polls",polls);
            Map<PollResource, Map> questionsX = new HashMap<PollResource, Map>();
            Map<PollResource, Map> resultMapX= new HashMap<PollResource, Map>();
            Map<PollResource, Map> percentMapX= new HashMap<PollResource, Map>();
            Map<PollResource, List> questionKeysX = new HashMap<PollResource, List>();
            for(int p = 0; p < pollResources.length; p++)
            {
                Map questions = new HashMap();
                Map resultMap= new HashMap();
                Map percentMap= new HashMap();
                PollResource pollResource = (PollResource)pollResources[p];
                pollService.prepareMaps(coralSession, pollResource, questions, resultMap, percentMap);
                List<Integer> questionKeys = new ArrayList<Integer>();
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
            screenError(getNode(), context, "Screen exception", e);
        }
    }
}
