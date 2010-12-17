package net.cyklotron.cms.poll.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An simple container
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 */
public class Question
{
    private String title;

    private Map answers;

    private long id;
    
    /**
     * Creates an empty containter.
     *
     */
    public Question(String title, long id)
    {
        this.title = title;
        this.id = id;
        answers = new HashMap();
        
    }
    
    public void addAnswer(String title, long id)
    {
        answers.put(new Integer(answers.size()),new Answer(title,id));
    }
    
    public Map getAnswers()
    {
        return answers;
    }

    public void setAnswers(Map answers)
    {
        this.answers = answers;
    }
    
    public List getAnswerKeys()
    {
        List keys = new ArrayList();
        for(int i = 0; i < answers.size(); i++)
        {
            keys.add(new Integer(i));
        }
        return keys;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getTitle()
    {
        return title;
    }

    public long getId()
    {
        return id;
    }
    
}
