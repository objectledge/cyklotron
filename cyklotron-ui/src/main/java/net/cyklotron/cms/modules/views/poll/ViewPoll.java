package net.cyklotron.cms.modules.views.poll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.poll.AnswerResource;
import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollResourceImpl;
import net.cyklotron.cms.poll.QuestionResource;
import net.cyklotron.cms.poll.util.Answer;
import net.cyklotron.cms.poll.util.Question;


/**
 *
 */
public class ViewPoll
    extends BasePollScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        int pid = parameters.getInt("pid", -1);
        if(pid == -1)
        {
            throw new ProcessingException("Poll id not found");
        }
        try
        {
            PollResource poll = PollResourceImpl.getPollResource(coralSession, pid);
            templatingContext.put("poll",poll);
            Map questions = new HashMap();
            Map resultMap= new HashMap();
            Map percentMap= new HashMap();
            pollService.prepareMaps(poll, questions, resultMap, percentMap);
            templatingContext.put("questions",questions);
            List questionKeys = new ArrayList();
            for(int i = 0; i< questions.size(); i++)
            {
                questionKeys.add(new Integer(i));
            }
            templatingContext.put("questionKeys",questionKeys);
            templatingContext.put("results",resultMap);
            templatingContext.put("percent",percentMap);
            templatingContext.put("questions",questions);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("PollException: ",e);
            return;
        }
    }
}
