package net.cyklotron.cms.modules.views.poll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollResourceImpl;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;


/**
 *
 */
public class PollResults
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
            Map questions = new HashMap();
            Map resultMap= new HashMap();
            Map percentMap= new HashMap();
            PollResource pollResource = PollResourceImpl.getPollResource(coralSession, pid);
            pollService.prepareMaps(pollResource, questions, resultMap, percentMap);
            List questionKeys = new ArrayList();
            for(int i = 0; i< questions.size(); i++)
            {
                questionKeys.add(new Integer(i));
            }
            templatingContext.put("questionKeys",questionKeys);
            templatingContext.put("poll",pollResource);
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
