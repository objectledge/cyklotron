package net.cyklotron.cms.modules.components.forum;

import java.util.ArrayList;
import java.util.List;

import net.labeo.services.resource.Resource;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.forum.ForumNodeResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.util.CmsResourceListTableModel;
import net.cyklotron.cms.util.ProtectedViewFilter;


/**
 * The discussion list screen class.
 */
public class LastAdded
    extends BaseForumComponent
{
    public static final String COMPONENT_NAME = "cms:component:forum,LastAdded";

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        Parameters componentConfig = getConfiguration();
        SiteResource site = getSite(context);
        
        String forumNode = componentConfig.get("forum_node","forum");
        try
        {	
			List posts = new ArrayList();
			ForumNodeResource forumNodeResource = forumService.getForum(site);
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
			TableState state = tableService.getGlobalState(data, "cms:components:forum,LastAdded");
			if(state.isNew())
			{
				state.setViewType(TableConstants.VIEW_AS_LIST);
				state.setPageSize(10);
			}
			TableModel model = new CmsResourceListTableModel(posts, i18nContext.getLocale()());
            ArrayList filters = new ArrayList();
            filters.add(new ProtectedViewFilter(coralSession.getUserSubject()));
            TableTool helper = new TableTool(state, model, filters);
			templatingContext.put("last_added_posts", helper);
			templatingContext.put("forum", forumService.getForum(getSite(context)));
        }
        catch(Exception e)
        {
        	componentError(context,"failed to retrieve forum node",e);
        	return;
        }
    }
}
