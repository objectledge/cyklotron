package net.cyklotron.cms.modules.components.poll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import net.cyklotron.cms.modules.components.CMSComponentWrapper;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Poll component.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: Poll.java,v 1.2 2005-01-25 11:24:26 pablo Exp $
 */

public class Poll extends SkinableCMSComponent
{
    private PollService pollService;

    public Poll()
    {
        pollService = (PollService)broker.getService(PollService.SERVICE_NAME);
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME))
            .getFacility(PollService.LOGGING_FACILITY);
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) throws ProcessingException
    {
        if(getSite(context) == null)
        {
            componentError(context, "No site selected");
            return;
        }

        try
        {
            String state = getState(data);
            PollResource poll = null;
            Parameters componentConfig = getConfiguration();
            PollsResource pollsResource = pollService.getPollsRoot(getSite(context));
            poll = pollService.getPoll(pollsResource, componentConfig);
            if(hasVoted(data))
            {
                templatingContext.put("already_voted",Boolean.TRUE);
            }


            String instanceName = (String)(templatingContext.get(CMSComponentWrapper.INSTANCE_PARAM_KEY));
            templatingContext.put("result_scope", "poll_"+instanceName);

            if(!state.equals("results"))
            {
                if(poll != null)
                {
                    templatingContext.put("poll", poll);
                    Map questions = new HashMap();
                    pollService.prepareMaps(poll, questions, new HashMap(), new HashMap());
                    templatingContext.put("questions", questions);
                    List questionKeys = new ArrayList();
                    for(int i = 0; i< questions.size(); i++)
                    {
                        questionKeys.add(new Integer(i));
                    }
                    templatingContext.put("questionKeys", questionKeys);
                }
            }
            else
            {
                templatingContext.put("poll", poll);
                Map questions = new HashMap();
                Map resultMap= new HashMap();
                Map percentMap= new HashMap();
                pollService.prepareMaps(poll, questions, resultMap, percentMap);
                templatingContext.put("questions", questions);
                List questionKeys = new ArrayList();
                for(int i = 0; i< questions.size(); i++)
                {
                    questionKeys.add(new Integer(i));
                }
                templatingContext.put("questionKeys", questionKeys);
                templatingContext.put("results", resultMap);
                templatingContext.put("percent", percentMap);
            }
        }
        catch(Exception e)
        {
			componentError(context, "Exception occured: "+e);
			return;			
        }
    }

    public String getState(RunData data)
        throws ProcessingException
    {
        
        String instanceName = (String)(templatingContext.get(CMSComponentWrapper.INSTANCE_PARAM_KEY));
        String state = "default";
        String instance = parameters.get("poll_instance","");
        String action = parameters.get("poll_action","");
        if(hasVoted(data))
        {
            return "results";
        }
        if(action.equals("results"))
        {
            if(instance.equals(instanceName))
            {
                state = "results";
            }
        }
        return state;
    }

    public boolean hasVoted(RunData data)
        throws ProcessingException
    {
        try
        {
            if(templatingContext.get("already_voted") != null && ((Boolean)templatingContext.get("already_voted")).booleanValue())
            {
                return true;
            }
            Parameters componentConfig = getConfiguration();
            PollsResource pollsResource = pollService.getPollsRoot(getSite(context));
            PollResource poll = pollService.getPoll(pollsResource, componentConfig);
            if(poll == null)
            {
                return false;
            }
            return pollService.hasVoted(data, poll);
        }
        catch(Exception e)
        {
        	log.error("Exception occured",e);
        	return false;
        }
    }

}
