package net.cyklotron.cms.modules.views.poll;

import java.util.ArrayList;
import java.util.List;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PollsResourceImpl;

/**
 *
 */
public class PollList
    extends BasePollScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        int psid = parameters.getInt("psid", -1);
        if(psid == -1)
        {
            throw new ProcessingException("Polls root id not found");
        }
        try
        {
            PollsResource pollsRoot = PollsResourceImpl.getPollsResource(coralSession, psid);
            templatingContext.put("pollsRoot",pollsRoot);
            Resource[] resources = coralSession.getStore().getResource(pollsRoot);
            List polls = new ArrayList();
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof PollResource)
                {
                    polls.add(resources[i]);
                }
            }
            templatingContext.put("polls",polls);
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
