package net.cyklotron.cms.modules.views.poll;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.cyklotron.cms.poll.util.Question;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 *
 */
public class AddPoll
    extends BasePollScreen
{
    
    
    public AddPoll(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, PollService pollService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, pollService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        boolean reset = parameters.getBoolean("reset", false);
        Map questions = null;
        if(reset)
        {
            questions = new HashMap();
            Question question = new Question("",-1);
            question.addAnswer("",-1);
            question.addAnswer("",-1);
            Integer newId = new Integer(questions.size());
            questions.put(newId,question);
            httpContext.setSessionAttribute(POLL_KEY, questions);
        }
        questions = (Map)httpContext.getSessionAttribute(POLL_KEY);
        if(questions == null)
        {
            questions = new HashMap();
        }
        templatingContext.put("questions",questions);
        List questionKeys = new ArrayList();
        for(int i = 0; i< questions.size(); i++)
        {
            questionKeys.add(new Integer(i));
        }
        templatingContext.put("questionKeys",questionKeys);

        // time stuff
        Calendar calendar = Calendar.getInstance(i18nContext.getLocale());
        templatingContext.put("calendar",calendar);
        Calendar twoWeeksLater = Calendar.getInstance(i18nContext.getLocale());
        twoWeeksLater.add(Calendar.DAY_OF_MONTH,14);
        templatingContext.put("two_weeks_later",twoWeeksLater);
    }    
}
