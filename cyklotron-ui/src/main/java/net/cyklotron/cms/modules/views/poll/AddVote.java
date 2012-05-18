package net.cyklotron.cms.modules.views.poll;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.QuestionResource;
import net.cyklotron.cms.poll.QuestionResourceImpl;
import net.cyklotron.cms.poll.util.Answer;
import net.cyklotron.cms.poll.util.Question;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 *
 */
public class AddVote
    extends BasePollScreen
{

    public AddVote(org.objectledge.context.Context context, Logger logger,
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
        boolean reset = parameters.getBoolean("reset", false);
        Map answers = null;
        if(reset)
        {
            answers = new HashMap();
            answers.put(0, new Answer("", -1));
            answers.put(1, new Answer("", -1));
            httpContext.setSessionAttribute(VOTE_KEY, answers);
        }
        answers = (Map)httpContext.getSessionAttribute(VOTE_KEY);
        if(answers == null)
        {
            answers = new HashMap();
        }
        templatingContext.put("answers", answers);
    }
}
