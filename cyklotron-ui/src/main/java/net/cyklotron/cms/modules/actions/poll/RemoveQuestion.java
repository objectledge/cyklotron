package net.cyklotron.cms.modules.actions.poll;

import java.util.HashMap;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: RemoveQuestion.java,v 1.2 2005-01-25 07:15:06 pablo Exp $
 */
public class RemoveQuestion
    extends BasePollAction
{
    

    public RemoveQuestion(Logger logger, StructureService structureService,
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
        savePoll(httpContext, parameters);
        int qid = parameters.getInt("qid", -1);
        if(qid == -1)
        {
            throw new ProcessingException("Question id not found");
        }
        
        Map questions = (Map)httpContext.getSessionAttribute(POLL_KEY);
        if(questions == null || questions.size() <= qid)
        {
            throw new ProcessingException("Question id exceed questions length");
        }

        Map newQuestions = new HashMap();
        for(int i = 0; i < qid; i++)
        {
            Integer key = new Integer(i);
            newQuestions.put(key, questions.get(key));
        }
        for(int i = qid + 1; i < questions.size(); i++)
        {
            newQuestions.put(new Integer(i-1), questions.get(new Integer(i)));
        }
        
        httpContext.setSessionAttribute(POLL_KEY,newQuestions);
    }
}


