package net.cyklotron.cms.modules.actions.poll;

import java.util.Date;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.relation.RelationModification;
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
import net.cyklotron.cms.poll.PollException;
import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollResourceImpl;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PoolResource;
import net.cyklotron.cms.poll.PoolResourceImpl;
import net.cyklotron.cms.poll.QuestionResource;
import net.cyklotron.cms.poll.QuestionResourceImpl;
import net.cyklotron.cms.poll.util.Answer;
import net.cyklotron.cms.poll.util.Question;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.TransitionResource;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: CreatePoll.java,v 1.7 2005-10-10 13:46:00 rafal Exp $
 */
public class CreatePoll
    extends BasePollAction
{

    public CreatePoll(Logger logger, StructureService structureService,
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
            route(mvcContext, templatingContext, "poll.AddPoll", "invalid_title");
            return;
        }
        if(description.length() < 0 || description.length() > 255)
        {
            route(mvcContext, templatingContext, "poll.AddPoll", "invalid_description");
            return;
        }
        if(questions.size() == 0)
        {
            route(mvcContext, templatingContext, "poll.AddPoll", "no_question_definied");
            return;
        }

        for(int i = 0; i< questions.size(); i++)
        {
            Question question = (Question)questions.get(new Integer(i));
            if(question.getTitle().length() < 1)
            {
                route(mvcContext, templatingContext, "poll.AddPoll", "invalid_question");
                return;
            }
            if(question.getAnswers().size() < 2)
            {
                route(mvcContext, templatingContext, "poll.AddPoll", "too_few_answers");
                return;
            }
            for(int j = 0; j< question.getAnswers().size(); j++)
            {
                Answer answer = (Answer)question.getAnswers().get(new Integer(j));
                if(answer.getTitle().length() < 1)
                {
                    route(mvcContext, templatingContext, "poll.AddPoll", "invalid_answer");
                    return;
                }
            }
        }

        try
        {
            SiteResource site = cmsDataFactory.getCmsData(context).getSite(); 
            PollsResource pollsRoot = pollService.getPollsParent(coralSession, site, pollService.POLLS_ROOT_NAME);
            PollResource pollResource = PollResourceImpl.createPollResource(coralSession, title,
                pollsRoot);
            pollResource.setDescription(description);

            // time stuff
            long startTime = parameters.getLong("start_time", 0);
            long endTime = parameters.getLong("end_time", 0);
            Date start = new Date(startTime);
            Date end = new Date(endTime);

            pollResource.setStartDate(start);
            pollResource.setEndDate(end);

            Resource workflowRoot = pollsRoot.getParent().getParent().getParent().getParent().getParent();
            workflowService.assignState(coralSession, workflowRoot, pollResource);
            String transitionName = parameters.get("transition","");
            if(transitionName.length() != 0)
            {
                TransitionResource[] transitions = workflowService.getTransitions(coralSession, pollResource.getState());
                for(int i = 0; i<transitions.length; i++)
                {
                    if(transitions[i].getName().equals(transitionName))
                    {
                        pollResource.setState(transitions[i].getTo());
                        workflowService.enterState(coralSession, pollResource, transitions[i].getTo());
                        break;
                    }
                }
            }
            pollResource.update();
            for(int i = 0; i< questions.size(); i++)
            {
                Question question = (Question)questions.get(new Integer(i));
                QuestionResource questionResource = QuestionResourceImpl.createQuestionResource(
                    coralSession, question.getTitle(), pollResource);
                questionResource.setSequence(i);
                questionResource.setVotesCount(0);
                questionResource.update();
                for(int j = 0; j < question.getAnswers().size(); j++)
                {
                    Answer answer = (Answer)question.getAnswers().get(new Integer(j));
                    AnswerResource answerResource = AnswerResourceImpl.createAnswerResource(
                        coralSession, answer.getTitle(), questionResource);
                    answerResource.setSequence(j);
                    answerResource.setVotesCount(0);
                    answerResource.update();
                }
            }

            long poolId = parameters.getLong("pool_id", -1);
            if(poolId != -1)
            {
                PoolResource poolResource = PoolResourceImpl.getPoolResource(coralSession, poolId);
                Relation refs = pollService.getRelation(coralSession);
                RelationModification diff = new RelationModification();
                diff.add(poolResource, pollResource);
                coralSession.getRelationManager().updateRelation(refs, diff);
            }
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("PollException: ",e);
            return;
        }
        catch(PollException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("PollException: ",e);
            return;
        }
        catch(WorkflowException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("LinkException: ",e);
            return;
        }
        catch(InvalidResourceNameException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("LinkException: ",e);
            return;
        }
        templatingContext.put("result","added_successfully");
    }
}


