package net.cyklotron.cms.modules.actions.poll;

import java.util.HashMap;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.poll.PollConstants;
import net.cyklotron.cms.poll.PollException;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.util.Answer;
import net.cyklotron.cms.poll.util.Question;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;


/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: BasePollAction.java,v 1.2 2005-01-24 10:26:58 pablo Exp $
 */
public abstract class BasePollAction
    extends BaseCMSAction
    implements PollConstants
{
    /** poll service */
    protected PollService pollService;

    /** workflow service */
    protected WorkflowService workflowService;

    public BasePollAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PollService pollService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory);
        this.pollService = pollService;
        this.workflowService = workflowService;
    }

    /**
     *
     */
    protected void savePoll(HttpContext httpContext, Parameters parameters)
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

    public PollsResource getPollsRoot(Context context, CoralSession coralSession)
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
            return pollService.getPollsRoot(coralSession, site);
        }
        catch(PollException e)
        {
            throw new ProcessingException("failed to lookup polls root");
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return coralSession.getUserSubject().hasRole(getPollsRoot(context, coralSession).getAdministrator());
    }
}


