package net.cyklotron.cms.modules.actions.poll;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.poll.AnswerResource;
import net.cyklotron.cms.poll.AnswerResourceImpl;
import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollResourceImpl;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.QuestionResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: RespondPoll.java,v 1.4 2005-02-21 16:28:24 zwierzem Exp $
 */
public class RespondPoll
    extends BasePollAction
{

    
    public RespondPoll(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PollService pollService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, pollService, workflowService);
        // TODO Auto-generated constructor stub
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        HttpSession session = httpContext.getRequest().getSession();
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
            if(pollService.hasVoted(httpContext, templatingContext, pollResource))
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
                long answer = parameters.getLong("question_"+questionResource.
                                                       getSequence(),-1);
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
                long answerId = parameters.getLong("question_"+questionResource.
                                                       getSequence(),-1);
                AnswerResource answer = AnswerResourceImpl.getAnswerResource(coralSession, answerId);
                int counter = answer.getVotesCount();
                counter++;
                answer.setVotesCount(counter);
                answer.update();
                questionResource.setVotesCount(questionResource.getVotesCount()+1);
                questionResource.update();
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
            logger.error("Exception in poll,RespondPoll action", e);
            return;
        }

        if(instanceName.length() > 0)
        {
            String cookieKey = "poll_"+pid;
            Cookie cookie = new Cookie(cookieKey, "1");
            cookie.setMaxAge(30*24*3600);
            StringBuilder path = new StringBuilder();
            path.append(httpContext.getRequest().getContextPath());
            if(!httpContext.getRequest().getServletPath().startsWith("/"))
            {
                path.append('/');
            }
            String servletPath = httpContext.getRequest().getServletPath();
            if(servletPath.endsWith("/"))
            {
                servletPath = servletPath.substring(0, servletPath.length() - 1);
            }
            path.append(servletPath);
            cookie.setPath(path.toString());
            httpContext.getResponse().addCookie(cookie);
        }

        templatingContext.put("result", "responded_successfully");
        templatingContext.put("already_voted", Boolean.TRUE);
    }
}


