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
import net.cyklotron.cms.poll.util.Question;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddQuestion.java,v 1.3 2005-03-08 10:53:05 pablo Exp $
 */
public class AddQuestion
    extends BasePollAction
{

    
    public AddQuestion(Logger logger, StructureService structureService,
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
        savePoll(httpContext, parameters);
        Map questions = (Map)httpContext.getSessionAttribute(POLL_KEY);
        if(questions == null)
        {
            questions = new HashMap();
        }
        Question question = new Question("",-1);
        question.addAnswer("",-1);
        question.addAnswer("",-1);
        Integer newId = new Integer(questions.size());
        questions.put(newId,question);
        httpContext.setSessionAttribute(POLL_KEY,questions);
    }
}
