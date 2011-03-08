package net.cyklotron.cms.modules.views.poll;

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.CreationTimeComparator;
import org.objectledge.coral.table.comparator.CreatorNameComparator;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.ListTableModel;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.VoteResource;
import net.cyklotron.cms.preferences.PreferencesService;


public class VotingConf
    extends BasePollScreen
{
    public VotingConf(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager, PollService pollService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, pollService);
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            PollsResource votesRoot = getPollsParent(coralSession, pollService.VOTES_ROOT_NAME);
            Resource[] resources = coralSession.getStore().getResource(votesRoot);
            List<Resource> votes = new ArrayList<Resource>();
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof VoteResource)
                {
                    votes.add(resources[i]);
                }
            }
            TableColumn[] columns = new TableColumn[3];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()));
            columns[1] = new TableColumn("creator", new CreatorNameComparator(i18nContext.getLocale()));
            columns[2] = new TableColumn("creation_time", new CreationTimeComparator());
            TableState state = tableStateManager.getState(context, "cms:screens:poll,VoteList");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(0);
                state.setSortColumnName("name");
            }
            TableModel model = new ListTableModel(votes, columns);
            templatingContext.put("table", new TableTool(state, null, model));
            
            Parameters screenConfig = getScreenConfig();
            boolean enableResults = screenConfig.getBoolean("enable_results", Boolean.FALSE);
            templatingContext.put("enable_results", enableResults);
            
            long voteId = screenConfig.getLong("vote_id", -1);
            if(voteId != -1)
            {
                try
                {
                    Resource vote = coralSession.getStore().getResource(voteId);
                    templatingContext.put("vote", vote);
                }
                catch(EntityDoesNotExistException e)
                {
                    //non existing vote may be configured
                }
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occurred", e);
        }
    }
}
