package net.cyklotron.cms.modules.actions.poll;

import java.util.Date;
import java.util.Map;

import net.cyklotron.cms.poll.AnswerResource;
import net.cyklotron.cms.poll.AnswerResourceImpl;
import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollResourceImpl;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PollsResourceImpl;
import net.cyklotron.cms.poll.PoolResource;
import net.cyklotron.cms.poll.PoolResourceImpl;
import net.cyklotron.cms.poll.QuestionResource;
import net.cyklotron.cms.poll.QuestionResourceImpl;
import net.cyklotron.cms.poll.util.Answer;
import net.cyklotron.cms.poll.util.Question;
import net.cyklotron.services.workflow.TransitionResource;
import net.cyklotron.services.workflow.WorkflowException;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.generic.CrossReference;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: CreatePoll.java,v 1.1 2005-01-24 04:34:08 pablo Exp $
 */
public class CreatePoll
    extends BasePollAction
{

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        savePoll(data);

        int psid = parameters.getInt("psid", -1);
        if(psid == -1)
        {
            throw new ProcessingException("Polls root id not found");
        }
        Map questions = (Map)httpContext.getSessionAttribute(POLL_KEY);
        if(questions == null)
        {
            throw new ProcessingException("Questions map not found");
        }

        String title = parameters.get("title","");
        String description = parameters.get("description","");
        if(title.length() < 1 || title.length() > 64)
        {
            route(data, "poll,AddPoll", "invalid_title");
            return;
        }
        if(description.length() < 0 || description.length() > 255)
        {
            route(data, "poll,AddPoll", "invalid_description");
            return;
        }
        if(questions.size() == 0)
        {
            route(data, "poll,EditPoll", "no_question_definied");
            return;
        }

        for(int i = 0; i< questions.size(); i++)
        {
            Question question = (Question)questions.get(new Integer(i));
            if(question.getTitle().length() < 1)
            {
                route(data, "poll,AddPoll", "invalid_question");
                return;
            }
            if(question.getAnswers().size() < 2)
            {
                route(data, "poll,AddPoll", "too_few_answers");
                return;
            }
            for(int j = 0; j< question.getAnswers().size(); j++)
            {
                Answer answer = (Answer)question.getAnswers().get(new Integer(j));
                if(answer.getTitle().length() < 1)
                {
                    route(data, "poll,AddPoll", "invalid_answer");
                    return;
                }
            }
        }

        try
        {
            PollsResource pollsRoot = PollsResourceImpl.getPollsResource(coralSession, psid);
            PollResource pollResource = PollResourceImpl.createPollResource(coralSession, title, pollsRoot, subject);
            pollResource.setDescription(description);

            // time stuff
            long startTime = parameters.getLong("start_time", 0);
            long endTime = parameters.getLong("end_time", 0);
            Date start = new Date(startTime);
            Date end = new Date(endTime);

            pollResource.setStartDate(start);
            pollResource.setEndDate(end);

            Resource workflowRoot = pollsRoot.getParent().getParent().getParent().getParent();
            workflowService.assignState(workflowRoot, pollResource, subject);
            String transitionName = parameters.get("transition","");
            if(transitionName.length() != 0)
            {
                TransitionResource[] transitions = workflowService.getTransitions(pollResource.getState());
                for(int i = 0; i<transitions.length; i++)
                {
                    if(transitions[i].getName().equals(transitionName))
                    {
                        pollResource.setState(transitions[i].getTo());
                        workflowService.enterState(pollResource, transitions[i].getTo());
                        break;
                    }
                }
            }
            pollResource.update(subject);
            for(int i = 0; i< questions.size(); i++)
            {
                Question question = (Question)questions.get(new Integer(i));
                QuestionResource questionResource = QuestionResourceImpl.
                    createQuestionResource(coralSession, question.getTitle(), pollResource, subject);
                questionResource.setSequence(i);
                questionResource.setVotesCount(0);
                questionResource.update(subject);
                for(int j = 0; j< question.getAnswers().size(); j++)
                {
                    Answer answer = (Answer)question.getAnswers().get(new Integer(j));
                    AnswerResource answerResource = AnswerResourceImpl.
                        createAnswerResource(coralSession, answer.getTitle(), questionResource, subject);
                    answerResource.setSequence(j);
                    answerResource.setVotesCount(0);
                    answerResource.update(subject);
                }
            }

            long poolId = parameters.getLong("pool_id", -1);
            if(poolId != -1)
            {
                PoolResource poolResource = PoolResourceImpl.getPoolResource(coralSession, poolId);
                CrossReference refs = pollsRoot.getBindings();
                refs.put(poolResource, pollResource);
                pollsRoot.setBindings(refs);
                pollsRoot.update(subject);
            }
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("PollException: ",e);
            return;
        }
        catch(ValueRequiredException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("PollException: ",e);
            return;
        }
        catch(WorkflowException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("LinkException: ",e);
            return;
        }
        templatingContext.put("result","added_successfully");
    }
}


