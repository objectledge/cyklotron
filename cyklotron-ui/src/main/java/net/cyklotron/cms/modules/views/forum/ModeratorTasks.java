package net.cyklotron.cms.modules.views.forum;

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.workflow.AutomatonResource;
import net.cyklotron.cms.workflow.StateResource;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 */
public class ModeratorTasks
    extends BaseForumScreen
{
    
    
    public ModeratorTasks(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, ForumService forumService,
        WorkflowService workflowService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, forumService,
                        workflowService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            Subject subject = coralSession.getUserSubject();
            SiteResource site = getSite();
            ForumResource forum = forumService.getForum(coralSession, site);
            ResourceClass messageClass = coralSession.getSchema().getResourceClass("cms.forum.message");
            AutomatonResource automaton = workflowService.getPrimaryAutomaton(coralSession, site.getParent().getParent(), messageClass);
            StateResource state = workflowService.getState(coralSession, automaton, "new");
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
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
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
