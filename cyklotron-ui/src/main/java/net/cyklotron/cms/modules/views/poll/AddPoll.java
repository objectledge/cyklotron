package net.cyklotron.cms.modules.views.poll;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.poll.util.Question;

/**
 *
 */
public class AddPoll
    extends BasePollScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
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
        Calendar calendar = Calendar.getInstance(i18nContext.getLocale()());
        templatingContext.put("calendar",calendar);
        Calendar twoWeeksLater = Calendar.getInstance(i18nContext.getLocale()());
        twoWeeksLater.add(Calendar.DAY_OF_MONTH,14);
        templatingContext.put("two_weeks_later",twoWeeksLater);
    }    
}
