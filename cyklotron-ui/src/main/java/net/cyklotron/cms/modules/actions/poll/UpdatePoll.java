package net.cyklotron.cms.modules.actions.poll;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
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
import net.cyklotron.cms.poll.QuestionResourceImpl;
import net.cyklotron.cms.poll.util.Answer;
import net.cyklotron.cms.poll.util.Question;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.StateResource;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdatePoll.java,v 1.8 2005-10-10 13:46:00 rafal Exp $
 */
public class UpdatePoll
    extends BasePollAction
{

    public UpdatePoll(Logger logger, StructureService structureService,
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
        Subject subject = coralSession.getUserSubject();
        savePoll(httpContext, parameters);

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
            route(mvcContext, templatingContext, "poll.EditPoll", "invalid_title");
            return;
        }
        if(description.length() < 0 || description.length() > 255)
        {
            route(mvcContext, templatingContext, "poll.EditPoll", "invalid_description");
            return;
        }

        if(questions.size() == 0)
        {
            route(mvcContext, templatingContext, "poll.EditPoll", "no_question_definied");
            return;
        }

        for(int i = 0; i< questions.size(); i++)
        {
            Question question = (Question)questions.get(new Integer(i));
            if(question.getTitle().length() < 1)
            {
                route(mvcContext, templatingContext, "poll.EditPoll", "invalid_question");
                return;
            }
            if(question.getAnswers().size() < 2)
            {
                route(mvcContext, templatingContext, "poll.EditPoll", "too_few_answers");
                return;
            }
            for(int j = 0; j< question.getAnswers().size(); j++)
            {
                Answer answer = (Answer)question.getAnswers().get(new Integer(j));
                if(answer.getTitle().length() < 1)
                {
                    route(mvcContext, templatingContext, "poll.EditPoll", "invalid_answer");
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
            pollResource.update();

            Set doNotDeleteResources = new HashSet();
            for(int i = 0; i < questions.size(); i++)
            {
                Question question = (Question)questions.get(new Integer(i));
                QuestionResource questionResource = null;
                if(question.getId() < 1)
                {
                    questionResource = QuestionResourceImpl.
                        createQuestionResource(coralSession, question.getTitle(), pollResource);
                    questionResource.setSequence(i);
                    questionResource.setVotesCount(0);
                    questionResource.update();
                }
                else
                {
                    questionResource = QuestionResourceImpl.
                        getQuestionResource(coralSession, question.getId());
                    if(!questionResource.getName().equals(question.getTitle()))
                    {
                        coralSession.getStore().setName(questionResource,question.getTitle());
                    }
                    if(questionResource.getSequence() != i)
                    {
                        questionResource.setSequence(i);
                        questionResource.update();
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
                            createAnswerResource(coralSession, answer.getTitle(), questionResource);
                        answerResource.setSequence(j);
                        answerResource.setVotesCount(0);
                        answerResource.update();
                    }
                    else
                    {
                        answerResource = AnswerResourceImpl.
                            getAnswerResource(coralSession, answer.getId());
                        if(!answerResource.getName().equals(answer.getTitle()))
                        {
                            coralSession.getStore().setName(answerResource,answer.getTitle());
                        }
                        if(answerResource.getSequence() != j)
                        {
                            answerResource.setSequence(j);
                            answerResource.update();
                        }
                    }
                    doNotDeleteResources.add(answerResource);
                }
            }
            purifyDefinition(pollResource,doNotDeleteResources, coralSession);

            if(pollResource.getState().getName().equals("active") ||
               pollResource.getState().getName().equals("expired"))
            {
                StateResource[] states = workflowService.getStates(coralSession, workflowService.getAutomaton(coralSession, pollResource.getState()),false);
                int i = 0;
                for(;i < states.length; i++)
                {
                    if(states[i].getName().equals("ready"))
                    {
                        pollResource.setState(states[i]);
                        workflowService.enterState(coralSession, pollResource,states[i]);
                        pollResource.update();
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
            templatingContext.put("trace",new StackTrace(e));
            logger.error("PollException: ",e);
            return;
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("PollException: ",e);
            return;
        }
        catch(EntityInUseException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("PollException: ",e);
            return;
        }
        catch(InvalidResourceNameException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("PollException: ",e);
            return;
        }
        templatingContext.put("result","updated_successfully");
    }



    private void purifyDefinition(Resource definition, Set doNotDeleteResources, CoralSession coralSession)
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
            question.update();
        }
    }
}


