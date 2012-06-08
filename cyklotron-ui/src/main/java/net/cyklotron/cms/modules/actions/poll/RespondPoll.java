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

import net.cyklotron.cms.CmsData;
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
 * @version $Id: RespondPoll.java,v 1.7 2007-02-25 14:14:49 pablo Exp $
 */
public class RespondPoll
    extends BasePollAction
{

    
    public RespondPoll(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PollService pollService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, pollService, workflowService);
        
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
            if(pollService.hasVoted(httpContext, pollResource))
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

            if(instanceName.length() > 0)
            {
                pollService.trackVote(httpContext, pollResource);
            }
            
            templatingContext.put("result", "responded_successfully");
            templatingContext.put("already_voted", Boolean.TRUE);
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
            logger.error("Exception in poll,RespondPoll action", e);
        }
    }
	
    public boolean checkAccessRights(Context context)
    	throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("poll"))
        {
            logger.debug("Application 'poll' not enabled in site");
            return false;
        }
	    return true;
	}
}


