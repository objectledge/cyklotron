package net.cyklotron.cms.modules.actions.poll;

import java.util.Map;

import net.cyklotron.cms.poll.util.Question;

import org.objectledge.pipeline.ProcessingException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ChangeSequence.java,v 1.1 2005-01-24 04:34:08 pablo Exp $
 */
public class ChangeSequence
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
        int aid = parameters.getInt("aid", -1);
        int offset = parameters.getInt("offset", 0);
        if(offset == 0)
        {
            throw new ProcessingException("Offset not found");
        }
        
        Map questions = (Map)httpContext.getSessionAttribute(POLL_KEY);
        if(questions == null || questions.size() <= qid)
        {
            throw new ProcessingException("Question id exceed questions length");
        }
        
        if(aid == -1)
        {
            move(questions, qid, offset);
        }
        else
        {
            Map answers = ((Question)questions.get(new Integer(qid))).getAnswers();
            move(answers, aid, offset);
        }
    }

    private void move(Map map, int id, int offset)
    {
        Integer key1 = new Integer(id);
        Integer key2 = null;
        Object obj1 = null;
        Object obj2 = null;
        
        switch (offset)
        {
        case -1:
            key2 = new Integer(id - 1);
            obj1 = map.get(key1);
            obj2 = map.get(key2);
            map.put(key1,obj2);
            map.put(key2,obj1);
            return ;
        case 1:
            key2 = new Integer(id + 1);
            obj1 = map.get(key1);
            obj2 = map.get(key2);
            map.put(key1,obj2);
            map.put(key2,obj1);
            return ;
        case -2:
            obj1 = map.get(key1);
            for(int i = id - 1; i >=0; i--)
            {
                map.put(new Integer(i + 1),map.get(new Integer(i)));
            }
            map.put(new Integer(0),obj1);
            return;
        case 2:
            obj1 = map.get(key1);
            for(int i = id + 1; i < map.size(); i++)
            {
                map.put(new Integer(i - 1),map.get(new Integer(i)));
            }
            map.put(new Integer(map.size()-1),obj1);
            return;
        }
    }
    
}


