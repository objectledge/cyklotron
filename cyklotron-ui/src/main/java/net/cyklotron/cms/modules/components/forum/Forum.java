package net.cyklotron.cms.modules.components.forum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.table.ARLTableModel;
import net.labeo.services.resource.table.NameComparator;
import net.labeo.services.table.ListTableModel;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.DefaultParameters;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.util.configuration.Parameters;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.DiscussionResourceImpl;
import net.cyklotron.cms.forum.ForumException;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.forum.ForumResourceImpl;
import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.forum.MessageResourceImpl;
import net.cyklotron.cms.modules.components.CMSComponentWrapper;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.util.ProtectedViewFilter;


/**
 * The discussion list screen class.
 */
public class Forum
    extends BaseForumComponent
{
    private static Map stateMap = new HashMap();

    static
    {
        stateMap.put("dl", "DiscussionList");
        stateMap.put("ml", "MessageList");
        stateMap.put("m", "Message");
        stateMap.put("am", "AddMessage");
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        Parameters componentConfig = getConfiguration();
        boolean stateFull = componentConfig.get("statefull").asBoolean(true);
        String thisComponentInstance = cmsDataFactory.getCmsData(context).getComponent().getInstanceName();
        templatingContext.put("result_scope", "forum_"+thisComponentInstance);
        String sessionKey = getSessionKey(data);

        Parameters parameters = null;
        if(stateFull)
        {
            parameters = (Parameters)httpContext.getSessionAttribute(sessionKey);
        }
        if(parameters == null)
        {
            parameters = new DefaultParameters();
            long did  = componentConfig.get("did").asLong(-1);
            if(did != -1)
            {
                parameters.add("did",did);
                parameters.add("state","ml");
            }
        }

        String componentInstance = parameters.get("ci","");
        if(componentInstance.equals(thisComponentInstance))
        {
            parameters = new DefaultParameters(parameters);
        }

        if(stateFull)
        {
            httpContext.setSessionAttribute(sessionKey, parameters);
        }
        // retrieve parameters from session...if null take the default values
        templatingContext.put("parameters",parameters);

        prepareState(data, context);
    }

    public String getComponentName()
    {
        return this.getClass().getName(); 
    }

    public Map getStateMap()
    {
        return stateMap;
    }    

    public String getSessionKey(RunData data)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        NavigationNodeResource node = cmsData.getNode();
        String thisComponentInstance = cmsData.getComponent().getInstanceName();
        if(node != null)
        {
            return getComponentName()+":"+thisComponentInstance+":"+node.getIdString(); 
        }
        else
        {
            return getComponentName()+":"+thisComponentInstance;
        }
    }
    
    protected void resetParameters(RunData data)
        throws ProcessingException
    {
        String sessionKey = getSessionKey(data);
        httpContext.setSessionAttribute(sessionKey, new DefaultParameters());
    }

    public void prepareDiscussionList(RunData data, Context context)
        throws ProcessingException
    {
        long fid = ((Parameters)context.get("parameters")).get("fid").asLong(-1);
        if(fid == -1)
        {
            if(getSite(context) != null)
            {
                try
                {
                    fid = forumService.getForum(getSite(context)).getId();
                }
                catch(ForumException e)
                {
                    resetParameters(data);
                    componentError(context, "Failed to obtain the forum for the site", e);
                    return;
                }
            }
            else
            {
                componentError(context, "No site selected");
            }
        }

        try
        {
            TableColumn[] columns = new TableColumn[1];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()()));

            String thisComponentInstance = cmsDataFactory.getCmsData(context).getComponent().getInstanceName();
            if(getSite(context) == null)
            {
                componentError(context, "No site selected");
                return;
            }
            String tableInstance = getComponentName()+":"+thisComponentInstance+":discussions:"+getSite(context).getIdString();

            ForumResource forum = ForumResourceImpl.getForumResource(coralSession,fid);
            templatingContext.put("forum",forum);

            Resource[] res = coralSession.getStore().getResource(forum, "discussions");
            if(res.length != 1)
            {
                throw new ProcessingException("discussions node not found in "+forum.getPath());
            }
            Resource[] discussions = coralSession.getStore().getResource(res[0]);
            templatingContext.put("discussions", discussions);
            TableState state = tableService.getGlobalState(data, tableInstance+":discussions");
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setPageSize(10);
                state.setSortColumnName("creation_time");
            }
            TableModel model = new ListTableModel(Arrays.asList(discussions), columns);
            ArrayList filters = new ArrayList();
            filters.add(new ProtectedViewFilter(coralSession.getUserSubject()));
            TableTool helper = new TableTool(state, model, filters);
            templatingContext.put("discussions_table", helper);

            res = coralSession.getStore().getResource(forum, "comments");
            if(res.length != 1)
            {
                throw new ProcessingException("comments node not found in "+forum.getPath());
            }
            Resource[] comments = coralSession.getStore().getResource(res[0]);
            templatingContext.put("comments", discussions);
            state = tableService.getGlobalState(data, tableInstance+":comments");
            if(state.isNew())
            {
            
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setPageSize(10);
                state.setSortColumnName("creation_time");
            }
            model = new ListTableModel(Arrays.asList(comments), columns);
            ArrayList filters2 = new ArrayList();
            filters2.add(new ProtectedViewFilter(coralSession.getUserSubject()));
            TableTool helper2 = new TableTool(state, model, filters2);
            
            templatingContext.put("comments_table", helper2);
        }
        catch(EntityDoesNotExistException e)
        {
            resetParameters(data);
            componentError(context, "Resource not found", e);
        }
        catch(TableException e)
        {
            resetParameters(data);
            componentError(context, "failed to initialize table toolkit", e);
        }
        catch(Exception e)
        {
            resetParameters(data);
            componentError(context, "Component exception", e);
        }
    }

    protected DiscussionResource getDiscussion(RunData data, Context context, boolean errorOnNull)
        throws ProcessingException
    {
        long did = ((Parameters)context.get("parameters")).get("did").asLong(-1);
        if(did == -1)
        {
        	if(errorOnNull)
        	{
            	resetParameters(data);
            	componentError(context, "Discussion id not found");
            }
			return null;
        }
        
        try
        {
            return DiscussionResourceImpl.getDiscussionResource(coralSession,did); 
        }
        catch(EntityDoesNotExistException e)
        {
            resetParameters(data);
            componentError(context, "Resource not found", e);
            return null;
        }
    }

    public void prepareMessageList(RunData data, Context context)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        try
        {
            DiscussionResource discussion = getDiscussion(data, context, true);
            if(discussion != null)
            {
                templatingContext.put("discussion",discussion);
                String thisComponentInstance = cmsDataFactory.getCmsData(context).getComponent().getInstanceName();
                String tableInstance = getComponentName()+":"+thisComponentInstance+":messages:"+discussion.getIdString();

                TableState state = tableService.getGlobalState(data, tableInstance);
                if(state.isNew())
                {
                    state.setViewType(TableConstants.VIEW_AS_TREE);
                    state.setMultiSelect(false);
                    String rootId = discussion.getIdString();
                    state.setRootId(rootId);
                    state.setCurrentPage(0);
                    state.setShowRoot(false);
                    state.setExpanded(rootId);

                    // TODO: configure default
                    state.setPageSize(4);
                    state.setSortColumnName("creation.time");
                }
                TableModel model;
                model = new ARLTableModel(i18nContext.getLocale()());

                ArrayList filters = new ArrayList();
                filters.add(new ProtectedViewFilter(subject));
                TableTool helper = null;
                helper = new TableTool(state, model, filters);
                templatingContext.put("table", helper);
            }
        }
        catch(TableException e)
        {
            resetParameters(data);
            componentError(context, "failed to initialize table toolkit", e);
        }
        catch(Exception e)
        {
            resetParameters(data);
            componentError(context, "Component exception", e);
        }
    }

    public void prepareMessage(RunData data, Context context)
        throws ProcessingException
    {
        long mid = ((Parameters)context.get("parameters")).get("mid").asLong(-1);
        if(mid == -1)
        {
            resetParameters(data);
            componentError(context, "Message id not found");
            return;
        }
        Subject subject = coralSession.getUserSubject();
        MessageResource message = null;
        try
        {
            message = MessageResourceImpl.getMessageResource(coralSession,mid);
        }
        catch(EntityDoesNotExistException e)
        {
            resetParameters(data);
            componentError(context, "Resource not found", e);
        }
        if(message.canView(subject))
        {
            templatingContext.put("message",message);
            templatingContext.put("children", Arrays.asList(coralSession.getStore().getResource(message)));
        }
        else
        {
            resetParameters(data);
            componentError(context, "User has no privileges to view this message");
        }
    }

    public void prepareAddMessage(RunData data, Context context)
        throws ProcessingException
    {
        long did = ((Parameters)context.get("parameters")).get("did").asLong(-1);
        long mid = ((Parameters)context.get("parameters")).get("mid").asLong(-1);
        long resid = ((Parameters)context.get("parameters")).get("resid").asLong(-1);

        if(mid == -1 && did == -1 && resid == -1)
        {
            resetParameters(data);
            componentError(context, "Discussion id nor message id nor resource id defined");
            return;
        }
        try
        {
            DiscussionResource discussion = getDiscussion(data, context, false);
            if(discussion != null)
            {
                templatingContext.put("discussion",discussion);
                templatingContext.put("parent",discussion);
            }
            if(mid != -1)
            {
                MessageResource message = MessageResourceImpl.getMessageResource(coralSession,mid);
                discussion = message.getDiscussion();
                templatingContext.put("parent_content",prepareContent(message.getContent()));
                templatingContext.put("discussion",discussion);
                templatingContext.put("parent",message);
            }
        }
        catch(EntityDoesNotExistException e)
        {
            resetParameters(data);
            componentError(context, "Resource not found", e);
        }
        catch(Exception e)
        {
            resetParameters(data);
            componentError(context, "Component exception", e);
        }
    }

    private String prepareContent(String content)
    {
        StringBuffer sb = new StringBuffer("");
        StringTokenizer st = new StringTokenizer(content, "\n", false);
        while (st.hasMoreTokens()) {
            sb.append(">");
            sb.append(st.nextToken());
            sb.append("\n");
        }
        return sb.toString();
    }

    public String getState(RunData data)
        throws ProcessingException
    {
        Context context = data.getContext();
        String thisComponentInstance = cmsDataFactory.getCmsData(context).getComponent().getInstanceName();
        String componentInstance = parameters.get("ci","");
        Parameters parameters;
        if(componentInstance.equals(thisComponentInstance))
        {
            //parameters = new DefaultParameters(parameters);
            parameters = parameters;
        }
        else
        {
            Parameters componentConfig = getConfiguration();
            boolean stateful = componentConfig.get("statefull").asBoolean(true);
            if(stateful)
            {
                String sessionKey = getSessionKey(data);
                parameters = (Parameters)httpContext.getSessionAttribute(sessionKey);
            }
            else
            {
                parameters = new DefaultParameters();
            }
        }        
        
        String state = parameters.get("state","");
	    if(state.equals(""))
	    {
	        state = parameters.get("did").isDefined() ? "ml" : "dl";
	    }
        String intState = (String)getStateMap().get(state);
        if(intState == null)
        {
            componentError(data.getContext(), "invalid component state '"+intState+"'");
            return "Default";
        }
        return intState;
    }
}
