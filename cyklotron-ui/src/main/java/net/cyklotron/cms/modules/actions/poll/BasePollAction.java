package net.cyklotron.cms.modules.actions.poll;

import java.util.HashMap;
import java.util.Map;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.poll.PollConstants;
import net.cyklotron.cms.poll.PollException;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.util.Answer;
import net.cyklotron.cms.poll.util.Question;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.workflow.WorkflowService;

import org.objectledge.pipeline.ProcessingException;


/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: BasePollAction.java,v 1.1 2005-01-24 04:34:08 pablo Exp $
 */
public abstract class BasePollAction
    extends BaseCMSAction
    implements PollConstants
{
    /** logging facility */
    protected Logger log;

    /** poll service */
    protected PollService pollService;

    /** workflow service */
    protected WorkflowService workflowService;

    public BasePollAction()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("poll");
        pollService = (PollService)broker.getService(PollService.SERVICE_NAME);
        workflowService = (WorkflowService)broker.getService(WorkflowService.SERVICE_NAME);
    }

    /**
     *
     */
    protected void savePoll(RunData data)
    {
        Map questions = (Map)httpContext.getSessionAttribute(POLL_KEY);
        if(questions == null)
        {
            questions = new HashMap();
            httpContext.setSessionAttribute(POLL_KEY, questions);
        }
        int questionSize = parameters.getInt("question_size", -1);
        for(int i = 0; i< questionSize; i++)
        {
            String questionTitle = parameters.get("question_"+i+"_title","");
            Question question = (Question)questions.get(new Integer(i));
            question.setTitle(questionTitle);
            int answerSize = parameters.getInt("question_"+i+"_size", -1);
            for(int j = 0; j< answerSize; j++)
            {
                Answer answer = (Answer)question.getAnswers().get(new Integer(j));
                String answerTitle = parameters.get("question_"+i+"_answer_"+j+"_title","");
                answer.setTitle(answerTitle);
            }
        }
    }

    public PollsResource getPollsRoot(RunData data)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData(context);
        SiteResource site = cmsData.getSite();
        if(site == null)
        {
            site = cmsData.getGlobalComponentsDataSite();
        }
        if(site == null)
        {
            throw new ProcessingException("No site selected");
        }
        try
        {
            return pollService.getPollsRoot(site);
        }
        catch(PollException e)
        {
            throw new ProcessingException("failed to lookup polls root");
        }
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return coralSession.getUserSubject().hasRole(getPollsRoot(data).getAdministrator());
    }
}


