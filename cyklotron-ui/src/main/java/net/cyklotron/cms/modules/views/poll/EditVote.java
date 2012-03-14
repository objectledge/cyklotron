package net.cyklotron.cms.modules.views.poll;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollResourceImpl;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PoolResource;
import net.cyklotron.cms.poll.VoteResource;
import net.cyklotron.cms.poll.VoteResourceImpl;
import net.cyklotron.cms.preferences.PreferencesService;


/**
 *
 */
public class EditVote
    extends BasePollScreen
{
    
    public EditVote(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, PollService pollService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, pollService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        int vid = parameters.getInt("vid", -1);
        if(vid == -1)
        {
            throw new ProcessingException("Vote id not found");
        }
        try
        {
            VoteResource vote = VoteResourceImpl.getVoteResource(coralSession, vid);
            templatingContext.put("vote", vote);

            Map answers = (Map)httpContext.getSessionAttribute(VOTE_KEY);
            boolean reset = parameters.getBoolean("reset", false);
            if(answers == null || reset)
            {
                answers = new HashMap();
                httpContext.setSessionAttribute(VOTE_KEY, answers);
                pollService.prepareVoteMaps(coralSession, vote, answers, new HashMap(), new HashMap(), new HashMap());
            }
            templatingContext.put("answers", answers);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("VoteException: ",e);
            return;
        }
    }
}
