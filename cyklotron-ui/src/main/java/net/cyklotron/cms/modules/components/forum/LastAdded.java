package net.cyklotron.cms.modules.components.forum;

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.ForumNodeResource;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.util.CmsResourceListTableModel;
import net.cyklotron.cms.util.ProtectedViewFilter;


/**
 * The discussion list screen class.
 */
public class LastAdded
    extends BaseForumComponent
{
    protected IntegrationService integrationService;
    
    public LastAdded(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        TableStateManager tableStateManager, ForumService forumService,
        IntegrationService integrationService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder,
                        tableStateManager, forumService);
        this.integrationService = integrationService;
    }
    public static final String COMPONENT_NAME = "cms:component:forum,LastAdded";

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        Parameters componentConfig = getConfiguration();
        SiteResource site = getSite(context);
        
        String forumNode = componentConfig.get("forum_node","forum");
        try
        {	
			List posts = new ArrayList();
			ForumNodeResource forumNodeResource = forumService.getForum(coralSession, site);
			if(forumNode.equals("comments") || forumNode.equals("discussions"))
			{
				Resource[] resources = coralSession.getStore().getResource(forumNodeResource, forumNode);
				if(resources.length == 1)
				{
				   	forumNodeResource = (ForumNodeResource)resources[0];
				}
			}
        	List list = forumNodeResource.getLastlyAdded();
        	if(list != null)
        	{
        		for(int i = 0; i < list.size(); i++)
        	   	{
        	   		Resource resource = (Resource)list.get(i);
        	   		if(resource != null)
        	   		{
        	   			posts.add(resource);
        	   		}
        	   	}
        	}
			TableState state = tableStateManager.getState(context, "cms:components:forum,LastAdded");
			if(state.isNew())
			{
				state.setTreeView(false);
				state.setPageSize(10);
			}
			TableModel model = new CmsResourceListTableModel(context, integrationService, posts, i18nContext.getLocale());
            ArrayList filters = new ArrayList();
            filters.add(new ProtectedViewFilter(coralSession, coralSession.getUserSubject()));
            TableTool helper = new TableTool(state, filters, model);
			templatingContext.put("last_added_posts", helper);
			templatingContext.put("forum", forumService.getForum(coralSession, getSite(context)));
        }
        catch(Exception e)
        {
        	componentError(context,"failed to retrieve forum node",e);
        	return;
        }
    }
}
