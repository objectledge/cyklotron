package net.cyklotron.cms.modules.views.poll;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollResourceImpl;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PoolResource;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;


/**
 *
 */
public class EditPoll
    extends BasePollScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
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

            //calendar support
            Calendar startDate = Calendar.getInstance(i18nContext.getLocale()());
            startDate.setTime(poll.getStartDate());
            templatingContext.put("start_date",startDate);
            Calendar endDate = Calendar.getInstance(i18nContext.getLocale()());
            endDate.setTime(poll.getEndDate());
            templatingContext.put("end_date",endDate);
            List days = new ArrayList(31);
            for(int i = 1; i <=31; i++)
            {
                days.add(new Integer(i));
            }
            templatingContext.put("days",days);
            List months = new ArrayList(12);
            for(int i = 0; i <=11; i++)
            {
                months.add(new Integer(i));
            }
            templatingContext.put("months",months);
            List years = new ArrayList(20);
            for(int i = 2000; i <=2020; i++)
            {
                years.add(new Integer(i));
            }
            templatingContext.put("years",years);

            Map questions = (Map)httpContext.getSessionAttribute(POLL_KEY);
            boolean reset = parameters.getBoolean("reset", false);
            if(questions == null || reset)
            {
                questions = new HashMap();
                httpContext.setSessionAttribute(POLL_KEY, questions);
                pollService.prepareMaps(poll, questions, new HashMap(), new HashMap());
            }
            templatingContext.put("questions",questions);
            List questionKeys = new ArrayList();
            for(int i = 0; i< questions.size(); i++)
            {
                questionKeys.add(new Integer(i));
            }
            templatingContext.put("questionKeys",questionKeys);

            PollsResource pollsRoot = (PollsResource)poll.getParent();
            templatingContext.put("pollsRoot",pollsRoot);
            Resource[] resources = coralSession.getStore().getResource(pollsRoot);
            List pools = new ArrayList();
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof PoolResource)
                {
                    pools.add(resources[i]);
                }
            }
            templatingContext.put("pools",pools);

            // cross reference set
            HashSet poolSet = new HashSet();
            Resource[] poolResources = pollsRoot.getBindings().getInv(poll);
            for(int i = 0; i < poolResources.length; i++)
            {
                poolSet.add(poolResources[i]);
            }
            templatingContext.put("poolSet",poolSet);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("PollException: ",e);
            return;
        }
    }
}
