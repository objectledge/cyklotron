package net.cyklotron.cms.modules.actions.poll;

import java.util.Map;

import net.cyklotron.cms.poll.util.Question;

import org.objectledge.pipeline.ProcessingException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddAnswer.java,v 1.1 2005-01-24 04:34:08 pablo Exp $
 */
public class AddAnswer
    extends BasePollAction
{
    
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        savePoll(data);
        int qid = parameters.getInt("qid", -1);
        if(qid == -1)
        {
            throw new ProcessingException("Question position not found");
        }
        Map questions = (Map)httpContext.getSessionAttribute(POLL_KEY);
        Question question = (Question)questions.get(new Integer(qid));
        question.addAnswer("",-1);
    }
}


