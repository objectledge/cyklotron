package net.cyklotron.cms.modules.actions.poll;

import java.util.HashMap;
import java.util.Map;

import net.cyklotron.cms.poll.util.Question;

import org.objectledge.pipeline.ProcessingException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddQuestion.java,v 1.1 2005-01-24 04:34:08 pablo Exp $
 */
public class AddQuestion
    extends BasePollAction
{

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        savePoll(data);
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
