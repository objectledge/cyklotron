package net.cyklotron.cms.modules.views.forum;

import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.services.workflow.AutomatonResource;
import net.cyklotron.services.workflow.StateResource;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * A screen for forum application configuration.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawe� Potempski</a>
 * @version $Id: EditConfiguration.java,v 1.1 2005-01-24 04:34:44 pablo Exp $
 */
public class EditConfiguration 
    extends BaseForumScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        try 
        {
            SiteResource site = getSite();
            ForumResource forum = forumService.getForum(site);
            templatingContext.put("forum",forum);
            NavigationNodeResource forumNode = forum.getForumNode();
            if(forumNode != null)
            {
                templatingContext.put("forum_node", forumNode);
            }
            Resource[] comments = coralSession.getStore().getResource(forum, "comments");
            Resource[] discussions = coralSession.getStore().getResource(forum, "discussions");
            templatingContext.put("comments",comments[0]);
            templatingContext.put("discussions",discussions[0]);
            
            ResourceClass resourceClass = coralSession.getSchema().getResourceClass("cms.forum.discussion");
            AutomatonResource automaton = workflowService.getPrimaryAutomaton(site.getParent().getParent(), resourceClass);
            StateResource[] states = workflowService.getStates(automaton,false);
            templatingContext.put("states", states);
        }
        catch(Exception e)
        {
            throw new ProcessingException("cannot get forum root", e);
        }
    }
}
