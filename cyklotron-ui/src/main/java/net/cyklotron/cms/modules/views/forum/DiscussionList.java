package net.cyklotron.cms.modules.views.forum;

import java.util.Arrays;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.CreationTimeComparator;
import net.labeo.services.resource.table.CreatorNameComparator;
import net.labeo.services.resource.table.NameComparator;
import net.labeo.services.table.ListTableModel;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.forum.ForumResourceImpl;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;

/**
 * The discussion list screen class.
 */
public class DiscussionList
    extends BaseForumScreen
    implements Secure
{
    TableService tableService = null;

    PreferencesService preferencesService = null;

    public DiscussionList()
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
        preferencesService = (PreferencesService)broker.getService(PreferencesService.SERVICE_NAME);
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        Boolean fromComponent = (Boolean)httpContext.getSessionAttribute(FROM_COMPONENT);
        if(fromComponent != null && fromComponent.booleanValue())
        {
            try
            {
                templatingContext.put("from_component",fromComponent);
                Long nodeId = (Long)httpContext.getSessionAttribute(COMPONENT_NODE);
                String instance = (String)httpContext.getSessionAttribute(COMPONENT_INSTANCE);
                Parameters config;
                if(nodeId != null)
                {
                    NavigationNodeResource node = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, nodeId.longValue());
                    config= preferencesService.getNodePreferences(node);
                }
                else
                {
                    config = preferencesService.getSystemPreferences();
                }
                String app = config.get("component."+instance+".app");
                String comp = config.get("component."+instance+".class");
                config = config.getSubset("component."+instance+
                    ".config."+app+"."+comp.replace(',','.')+".");                
                templatingContext.put("component_configuration", config);
                templatingContext.put("component_node",nodeId);
                templatingContext.put("component_instance", instance);
            }
            catch(EntityDoesNotExistException e)
            {
                throw new ProcessingException("ARL Exception ",e);
            }
        }

        long fid = parameters.getLong("fid", -1);
        if(fid == -1)
        {
            throw new ProcessingException("Forum id not found");
        }
        try
        {
            TableColumn[] columns = new TableColumn[3];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()()));
            columns[1] = new TableColumn("creator", new CreatorNameComparator(i18nContext.getLocale()()));
            columns[2] = new TableColumn("creation_time", new CreationTimeComparator());

            ForumResource forum = ForumResourceImpl.getForumResource(coralSession,fid);
            templatingContext.put("forum",forum);
            // discussions
            Resource[] res = coralSession.getStore().getResource(forum, "discussions");
            if(res.length != 1)
            {
                throw new ProcessingException("discussions node not found in "+forum.getPath());
            }
            Resource[] discussions = coralSession.getStore().getResource(res[0]);
            templatingContext.put("discussions", discussions);
            TableState state = tableService.getLocalState(data, "cms:screens:forum,DiscussionList:discussions");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(Arrays.asList(discussions), columns);
            templatingContext.put("discussions_table", new TableTool(state, model, null));
            
            // comments
            res = coralSession.getStore().getResource(forum, "comments");
            if(res.length != 1)
            {
                throw new ProcessingException("comments node not found "+forum.getPath());
            }
            Resource[] comments = coralSession.getStore().getResource(res[0]);
            templatingContext.put("comments", comments);
            state = tableService.getLocalState(data, "cms:screens:forum,DiscussionList:comments");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            model = new ListTableModel(Arrays.asList(comments), columns);
            templatingContext.put("comments_table", new TableTool(state, model, null));
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("Resource not found",e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize table toolkit", e);
        }
    }
}
