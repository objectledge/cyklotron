package net.cyklotron.cms.modules.actions.poll;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import net.cyklotron.cms.poll.AnswerResource;
import net.cyklotron.cms.poll.AnswerResourceImpl;
import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollResourceImpl;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.QuestionResource;
import net.labeo.modules.actions.BaseARLAction;
import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: RespondPoll.java,v 1.2 2005-01-24 10:26:58 pablo Exp $
 */
public class RespondPoll
    extends BaseARLAction
{
    /** logging facility */
    protected Logger log;

    protected PollService ps;
    
    public RespondPoll()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("poll");
        ps = (PollService)broker.getService(PollService.SERVICE_NAME);
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        HttpSession session = data.getRequest().getSession();
        if(session == null || session.isNew())
        {
            templatingContext.put("result", "new_session");
            return;
        }
        
        String instanceName = parameters.get("poll_instance","");
        templatingContext.put("result_scope", "poll_"+instanceName);

        Subject subject = coralSession.getUserSubject();
        int pid = parameters.getInt("pid", -1);
        if(pid == -1)
        {
            throw new ProcessingException("Poll id not found");
        }

        try
        {
            PollResource pollResource = PollResourceImpl.getPollResource(coralSession, pid);
            if(ps.hasVoted(data, pollResource))
            {
                templatingContext.put("result", "already_responded");
                templatingContext.put("already_voted", Boolean.TRUE);
                return;
            }
            Resource[] questionResources = coralSession.getStore().
                getResource(pollResource);
            // check wheteher all answers were chosen
            for(int i = 0; i < questionResources.length; i++)
            {
                QuestionResource questionResource = (QuestionResource)questionResources[i];
                long answer = parameters.get("question_"+questionResource.
                                                       getSequence()).asLong(-1);
                if(answer == -1)
                {
                    templatingContext.put("result","answer_not_found");
                    templatingContext.put("question_number",""+questionResource.getSequence());
                    return;
                }
            }
            for(int i = 0; i < questionResources.length; i++)
            {
                QuestionResource questionResource = (QuestionResource)questionResources[i];
                long answerId = parameters.get("question_"+questionResource.
                                                       getSequence()).asLong(-1);
                AnswerResource answer = AnswerResourceImpl.getAnswerResource(coralSession, answerId);
                int counter = answer.getVotesCount();
                counter++;
                answer.setVotesCount(counter);
                answer.update(subject);
                questionResource.setVotesCount(questionResource.getVotesCount()+1);
                questionResource.update(subject);
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
            log.error("Exception in poll,RespondPoll action", e);
            return;
        }

        if(instanceName.length() > 0)
        {
            String cookieKey = "poll_"+pid;
            Cookie cookie = new Cookie(cookieKey, "1");
            cookie.setMaxAge(30*24*3600);
            StringBuffer path = new StringBuffer();
            path.append(data.getRequest().getContextPath());
            if(!data.getRequest().getServletPath().startsWith("/"))
            {
                path.append('/');
            }
            String servletPath = data.getRequest().getServletPath();
            if(servletPath.endsWith("/"))
            {
                servletPath = servletPath.substring(0, servletPath.length() - 1);
            }
            path.append(servletPath);
            cookie.setPath(path.toString());
            data.getResponse().addCookie(cookie);
        }

        templatingContext.put("result", "responded_successfully");
        templatingContext.put("already_voted", Boolean.TRUE);
    }
}


