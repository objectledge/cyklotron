package net.cyklotron.cms.modules.actions.poll;

import java.util.HashMap;
import java.util.Map;

import net.cyklotron.cms.poll.util.Question;

import org.objectledge.pipeline.ProcessingException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: RemoveAnswer.java,v 1.1 2005-01-24 04:34:08 pablo Exp $
 */
public class RemoveAnswer
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
        int aid = parameters.getInt("aid", -1);
        if(qid == -1 || aid == -1)
        {
            throw new ProcessingException("Question id nor Answer id not found");
        }
        
        Map questions = (Map)httpContext.getSessionAttribute(POLL_KEY);
        if(questions == null || questions.size() <= qid)
        {
            throw new ProcessingException("Question id exceed questions length");
        }
        Question question = (Question)questions.get(new Integer(qid));
        Map answers = question.getAnswers();
        if(answers == null || answers.size() <= aid)
        {
            throw new ProcessingException("Answer id exceed answers length");
        }
        Map newAnswers = new HashMap();
        for(int i = 0; i < aid; i++)
        {
            Integer key = new Integer(i);
            newAnswers.put(key, answers.get(key));
        }
        for(int i = aid + 1; i < answers.size(); i++)
        {
            newAnswers.put(new Integer(i-1), answers.get(new Integer(i)));
        }
        question.setAnswers(newAnswers);
    }
}


