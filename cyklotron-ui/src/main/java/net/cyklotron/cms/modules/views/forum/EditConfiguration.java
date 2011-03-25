package net.cyklotron.cms.modules.views.forum;

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.schema.ResourceClass;
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
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.workflow.AutomatonResource;
import net.cyklotron.cms.workflow.StateResource;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * A screen for forum application configuration.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawe� Potempski</a>
 * @version $Id: EditConfiguration.java,v 1.4 2005-03-08 11:02:49 pablo Exp $
 */
public class EditConfiguration 
    extends BaseForumScreen
{
    
    public EditConfiguration(org.objectledge.context.Context context, Logger logger,
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
            SiteResource site = getSite();
            ForumResource forum = forumService.getForum(coralSession, site);
            templatingContext.put("forum",forum);
            NavigationNodeResource forumNode = forum.getForumNode();
            if(forumNode != null)
            {
                templatingContext.put("forum_node", forumNode);
            }
            Resource[] comments = coralSession.getStore().getResource(forum, "comments");
            Resource[] discussionsRoot = coralSession.getStore().getResource(forum, "discussions");
            Resource[] resources = coralSession.getStore().getResource(discussionsRoot[0]);
            List discussions = new ArrayList();
            for(int i = 0; i < resources.length; i++)
            {
                discussions.add(resources[i]);
            }
            
            templatingContext.put("comments",comments[0]);
            templatingContext.put("discussionsRoot", discussionsRoot[0]);
            templatingContext.put("discussions", discussions);
            
            ResourceClass resourceClass = coralSession.getSchema().getResourceClass("cms.forum.discussion");
            AutomatonResource automaton = workflowService.getPrimaryAutomaton(coralSession, site.getParent().getParent(), resourceClass);
            StateResource[] states = workflowService.getStates(coralSession, automaton,false);
            templatingContext.put("states", states);
        }
        catch(Exception e)
        {
            throw new ProcessingException("cannot get forum root", e);
        }
    }
}
