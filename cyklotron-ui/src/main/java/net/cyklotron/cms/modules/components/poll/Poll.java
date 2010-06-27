package net.cyklotron.cms.modules.components.poll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.components.CMSComponentWrapper;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.skins.SkinService;

/**
 * Poll component.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: Poll.java,v 1.5 2005-05-16 08:39:36 pablo Exp $
 */

public class Poll extends SkinableCMSComponent
{
    private PollService pollService;

    public Poll(org.objectledge.context.Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        PollService pollService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        this.pollService = pollService;
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
            String state = getState(context);
            PollResource poll = null;
            Parameters componentConfig = getConfiguration();
            PollsResource pollsResource = pollService.getPollsRoot(coralSession, getSite(context));
            poll = pollService.getPoll(coralSession, pollsResource, componentConfig);
            if(hasVoted())
            {
                templatingContext.put("already_voted",Boolean.TRUE);
            }


            String instanceName = (String)(templatingContext.get(CMSComponentWrapper.INSTANCE_PARAM_KEY));
            templatingContext.put("result_scope", "poll_"+instanceName);

            if(!state.equals("Results"))
            {
                if(poll != null)
                {
                    templatingContext.put("poll", poll);
                    Map questions = new HashMap();
                    pollService.prepareMaps(coralSession, poll, questions, new HashMap(), new HashMap());
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
                pollService.prepareMaps(coralSession, poll, questions, resultMap, percentMap);
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

    public String getState(Context context)
        throws ProcessingException
    {
		Parameters parameters = RequestParameters.getRequestParameters(context);
		TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
		
        String instanceName = (String)(templatingContext.get(CMSComponentWrapper.INSTANCE_PARAM_KEY));
        String state = "default";
        String instance = parameters.get("poll_instance","");
        String action = parameters.get("poll_action","");
        if(hasVoted())
        {
            return "Results";
        }
        if(action.equals("results"))
        {
            if(instance.equals(instanceName))
            {
                state = "Results";
            }
        }
        return state;
    }

    public boolean hasVoted()
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);

        try
        {
            if(templatingContext.get("already_voted") != null && ((Boolean)templatingContext.get("already_voted")).booleanValue())
            {
                return true;
            }
            Parameters componentConfig = getConfiguration();
            PollsResource pollsResource = pollService.getPollsRoot(coralSession, getSite(context));
            PollResource poll = pollService.getPoll(coralSession, pollsResource, componentConfig);
            if(poll == null)
            {
                return false;
            }
            return pollService.hasVoted(httpContext, templatingContext, poll);
        }
        catch(Exception e)
        {
        	logger.error("Exception occured",e);
        	return false;
        }
    }

}
