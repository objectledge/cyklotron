package net.cyklotron.cms.modules.views.forum;

import java.util.ArrayList;
import java.util.List;

import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.services.workflow.AutomatonResource;
import net.cyklotron.services.workflow.StateResource;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.query.QueryResults;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 *
 */
public class ModeratorTasks
    extends BaseForumScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            Subject subject = coralSession.getUserSubject();
            SiteResource site = getSite();
            ForumResource forum = forumService.getForum(site);
            ResourceClass messageClass = coralSession.getSchema().getResourceClass("cms.forum.message");
            AutomatonResource automaton = workflowService.getPrimaryAutomaton(site.getParent().getParent(), messageClass);
            StateResource state = workflowService.getState(automaton, "new");
            QueryResults results = coralSession.getQuery().
                executeQuery("FIND RESOURCE FROM cms.forum.message WHERE state = "+state.getIdString());
            Resource[] nodes = results.getArray(1);
            List messages = new ArrayList();
            
            for(int i = 0; i < nodes.length; i++)
            {
                MessageResource message = (MessageResource)nodes[i];
                if(message.getDiscussion().getForum().equals(forum))
                {
                	messages.add(message);
                }
            }
            templatingContext.put("messages", messages);
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured", e);
        }
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        try
        {
			return coralSession.getUserSubject().hasRole(getSite().getTeamMember());
        }
        catch(Exception e)
        {
        	throw new ProcessingException("Exception occured during access rights checking", e);
        }
    }
}
