package net.cyklotron.cms.modules.actions.poll;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.poll.AnswerResource;
import net.cyklotron.cms.poll.AnswerResourceImpl;
import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollResourceImpl;
import net.cyklotron.cms.poll.QuestionResource;
import net.cyklotron.cms.poll.QuestionResourceImpl;
import net.cyklotron.cms.poll.util.Answer;
import net.cyklotron.cms.poll.util.Question;
import net.cyklotron.services.workflow.StateResource;
import net.cyklotron.services.workflow.WorkflowException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdatePoll.java,v 1.1 2005-01-24 04:34:08 pablo Exp $
 */
public class UpdatePoll
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

        int pid = parameters.getInt("pid", -1);
        if(pid == -1)
        {
            throw new ProcessingException("Poll id not found");
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
            route(data, "poll,EditPoll", "invalid_title");
            return;
        }
        if(description.length() < 0 || description.length() > 255)
        {
            route(data, "poll,EditPoll", "invalid_description");
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
                route(data, "poll,EditPoll", "invalid_question");
                return;
            }
            if(question.getAnswers().size() < 2)
            {
                route(data, "poll,EditPoll", "too_few_answers");
                return;
            }
            for(int j = 0; j< question.getAnswers().size(); j++)
            {
                Answer answer = (Answer)question.getAnswers().get(new Integer(j));
                if(answer.getTitle().length() < 1)
                {
                    route(data, "poll,EditPoll", "invalid_answer");
                    return;
                }
            }
        }

        try
        {
            PollResource pollResource = PollResourceImpl.getPollResource(coralSession, pid);
            if(!pollResource.getName().equals(title))
            {
                coralSession.getStore().setName(pollResource, title);
            }
            if(!pollResource.getDescription().equals(description))
            {
                pollResource.setDescription(description);
            }

            // time stuff
            long startTime = parameters.getLong("start_time", 0);
            long endTime = parameters.getLong("end_time", 0);
            Date start = new Date(startTime);
            Date end = new Date(endTime);
            pollResource.setStartDate(start);
            pollResource.setEndDate(end);
            pollResource.update(subject);

            Set doNotDeleteResources = new HashSet();
            for(int i = 0; i < questions.size(); i++)
            {
                Question question = (Question)questions.get(new Integer(i));
                QuestionResource questionResource = null;
                if(question.getId() < 1)
                {
                    questionResource = QuestionResourceImpl.
                        createQuestionResource(coralSession, question.getTitle(), pollResource, subject);
                    questionResource.setSequence(i);
                    questionResource.setVotesCount(0);
                    questionResource.update(subject);
                }
                else
                {
                    questionResource = QuestionResourceImpl.
                        getQuestionResource(coralSession, question.getId());
                    if(!questionResource.getName().equals(question.getTitle()));
                    {
                        coralSession.getStore().setName(questionResource,question.getTitle());
                    }
                    if(questionResource.getSequence() != i)
                    {
                        questionResource.setSequence(i);
                        questionResource.update(subject);
                    }
                }
                doNotDeleteResources.add(questionResource);
                for(int j = 0; j< question.getAnswers().size(); j++)
                {
                    Answer answer = (Answer)question.getAnswers().get(new Integer(j));
                    AnswerResource answerResource = null;
                    if(answer.getId() < 1)
                    {
                        answerResource = AnswerResourceImpl.
                            createAnswerResource(coralSession, answer.getTitle(), questionResource, subject);
                        answerResource.setSequence(j);
                        answerResource.setVotesCount(0);
                        answerResource.update(subject);
                    }
                    else
                    {
                        answerResource = AnswerResourceImpl.
                            getAnswerResource(coralSession, answer.getId());
                        if(!answerResource.getName().equals(answer.getTitle()));
                        {
                            coralSession.getStore().setName(answerResource,answer.getTitle());
                        }
                        if(answerResource.getSequence() != j)
                        {
                            answerResource.setSequence(j);
                            answerResource.update(subject);
                        }
                    }
                    doNotDeleteResources.add(answerResource);
                }
            }
            purifyDefinition(pollResource,doNotDeleteResources, subject);

            if(pollResource.getState().getName().equals("active") ||
               pollResource.getState().getName().equals("expired"))
            {
                StateResource[] states = workflowService.getStates(workflowService.getAutomaton(pollResource.getState()),false);
                int i = 0;
                for(;i < states.length; i++)
                {
                    if(states[i].getName().equals("ready"))
                    {
                        pollResource.setState(states[i]);
                        workflowService.enterState(pollResource,states[i]);
                        pollResource.update(subject);
                        break;
                    }
                }
                if(i == states.length)
                {
                    templatingContext.put("result","state_not_found");
                    return;
                }
            }

        }
        catch(WorkflowException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("PollException: ",e);
            return;
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
        catch(EntityInUseException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("PollException: ",e);
            return;
        }
        templatingContext.put("result","updated_successfully");
    }



    private void purifyDefinition(Resource definition, Set doNotDeleteResources, Subject subject)
        throws EntityInUseException
    {
        // delete all useless resources in poll tree
        Resource[] questions = coralSession.getStore().getResource(definition);
        for(int j = 0; j < questions.length; j++)
        {
        	QuestionResource question = (QuestionResource)questions[j];
            Resource[] answers = coralSession.getStore().getResource(questions[j]);
            for(int k = 0; k < answers.length; k++)
            {
                if(!doNotDeleteResources.contains(answers[k]))
                {
                	question.setVotesCount(question.getVotesCount()-((AnswerResource)answers[k]).getVotesCount());
                    coralSession.getStore().deleteResource(answers[k]);
                }
            }
            if(!doNotDeleteResources.contains(questions[j]))
            {
                coralSession.getStore().deleteResource(questions[j]);
            }
            question.update(subject);
        }
    }
}


