package net.cyklotron.cms.modules.actions.fixes;

import java.util.ArrayList;
import java.util.List;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.poll.AnswerResource;
import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.QuestionResource;
import net.cyklotron.cms.poll.ResultResource;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.query.QueryResults;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Converts the navigation configurations to fit new navigation functionality.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: FixPollApplication.java,v 1.1 2005-01-24 04:34:29 pablo Exp $
 */
public class FixPollApplication extends BaseCMSAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        try
        {
        	 QueryResults results = coralSession.getQuery().
                 executeQuery("FIND RESOURCE FROM cms.poll.poll");
             Resource[] polls = results.getArray(1);
             for(int i = 0; i < polls.length; i++)
             {
             	 try
				 {
             	 	fixPoll((PollResource)polls[i], subject);
				 }
             	 catch(Exception e)
				 {
             	 	 throw e;
				 }
             }
             templatingContext.put("result", "success");
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
    }

    public void fixPoll(PollResource poll, Subject subject)
        throws Exception
    {
    	// remember children of poll...
    	Resource[] children = coralSession.getStore().getResource(poll);
    	Resource[] resources = coralSession.getStore().getResource(poll, "results");
    	if(resources.length == 0)
    	{
    		System.out.println("Poll '"+poll.getPath()+"' doesn't have results node");
    		return;
    	}
    	ResultResource result = (ResultResource)resources[0];
    	List list = result.getAnswers();
    	List answers = new ArrayList();
    	List questions = new ArrayList();
    	resources = coralSession.getStore().getResource(poll, "definition");
    	if(resources.length == 0)
    	{
    		System.out.println("Poll '"+poll.getPath()+"' doesn't have definition node");
    		return;
    	}
    	Resource[] questionResources = coralSession.getStore().getResource(resources[0]);
    	for(int i = 0; i < questionResources.length; i++)
    	{
    		QuestionResource questionResource = (QuestionResource)questionResources[i];
    		questionResource.setVotesCount(0);
    		questions.add(questionResource);
    		Resource[] answerResources = coralSession.getStore().getResource(questionResources[i]);
    		for(int j = 0; j < answerResources.length; j++)
    		{
    			AnswerResource answerResource = (AnswerResource)answerResources[j];
    			answerResource.setVotesCount(0);
    			answers.add(answerResource);
            }
    	}
    	if(list != null)
    	{
	    	for(int i =0; i < list.size(); i++)
	    	{
	    		AnswerResource answer = (AnswerResource)answers.get(i);
	    		answer.setVotesCount(answer.getVotesCount()+1);
	    		QuestionResource q = (QuestionResource)answer.getParent();
	    		q.setVotesCount(q.getVotesCount()+1);
	    	}
    	}
	    for(int i =0; i < answers.size(); i++)
    	{
    		AnswerResource answer = (AnswerResource)answers.get(i);
    		answer.update(subject);
    	}
	    for(int i =0; i < questions.size(); i++)
    	{
    		QuestionResource q = (QuestionResource)questions.get(i);
    		q.update(subject);
    	}
	    for(int i = 0; i < questionResources.length; i++)
    	{
	    	coralSession.getStore().setParent(questionResources[i], poll);
    	}
	    for(int i = 0; i < children.length; i++)
    	{
	    	coralSession.getStore().deleteResource(children[i]);
    	}
    }
}
