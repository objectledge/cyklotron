package net.cyklotron.cms.modules.actions.poll;

import java.util.HashMap;
import java.util.Map;

import org.objectledge.pipeline.ProcessingException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: RemoveQuestion.java,v 1.1 2005-01-24 04:34:08 pablo Exp $
 */
public class RemoveQuestion
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


