package net.cyklotron.cms.modules.views.poll;

import java.util.Arrays;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.ResourceListTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.poll.PollException;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PoolResource;
import net.cyklotron.cms.poll.PoolResourceImpl;
import net.cyklotron.cms.poll.VoteResource;
import net.cyklotron.cms.poll.VoteResourceImpl;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 *
 */
public class VoteList
    extends BasePollScreen
{
    
    public VoteList(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, PollService pollService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, pollService);
    }

    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            PollsResource votesRoot = (PollsResource)pollService.getPollsParent(coralSession,
                getSite(), pollService.VOTES_ROOT_NAME);
            Resource[] voteResources = coralSession.getStore().getResource(votesRoot);            
            templatingContext.put("votes", Arrays.asList(voteResources));
            
            TableState state = tableStateManager.getState(context, "cms:screens:poll,VoteList");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model = new ResourceListTableModel(Arrays.asList(voteResources), i18nContext.getLocale());
            templatingContext.put("table", new TableTool(state, null, model));
            
        }
        catch(PollException e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
            logger.error("PollException: ", e);
            return;
        }
        catch(TableException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
