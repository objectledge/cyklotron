package net.cyklotron.cms.modules.views.forum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.DiscussionResourceImpl;
import net.cyklotron.cms.forum.ForumException;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.forum.MessageResourceImpl;
import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.cyklotron.cms.util.ProtectedViewFilter;
import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.table.ARLTableModel;
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
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Stateful screen for forum application.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawe� Potempski</a>
 * @version $Id: Forum.java,v 1.1 2005-01-24 04:34:44 pablo Exp $
 */
public class Forum
    extends BaseSkinableScreen
{
    /** forum serivce. */
    protected ForumService forumService;

    /** table service for hit list display. */
    protected TableService tableService;

    /** logging facility */
    protected Logger log;
    
    private Set allowedStates = new HashSet();

    public Forum()
    {
        super();
        forumService = (ForumService)broker.getService(ForumService.SERVICE_NAME);
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
            getFacility(ForumService.LOGGING_FACILITY);
        
        allowedStates.add("Discussions");
        allowedStates.add("Messages");
        allowedStates.add("Message");
        allowedStates.add("NewMessage");
        allowedStates.add("NewDiscussion");
    }
    
    public String getState(RunData data)
        throws ProcessingException
    {
        String state = parameters.get("state","Discussions");
        if(!allowedStates.contains(state))
        {
            return null;
        }
        return state;
    }

    public void prepareDiscussions(RunData data, Context context)
        throws ProcessingException
    {
        try
        {
            TableColumn[] columns = new TableColumn[3];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()()));
            columns[1] = new TableColumn("creator", new CreatorNameComparator(i18nContext.getLocale()()));
            columns[2] = new TableColumn("creation_time", new CreationTimeComparator());

            ForumResource forum = forumService.getForum(getSite());
            templatingContext.put("forum",forum);
            
            Resource[] res = coralSession.getStore().getResource(forum, "discussions");
            if(res.length != 1)
            {
                screenError(getNode(), context, "discussions node not found in "+forum.getPath());
            }
            Resource[] discussions = coralSession.getStore().getResource(res[0]);
            TableState state = tableService.getLocalState(data, "cms:screens:forum,Forum:discussions");
            if(state.isNew())
            {
            
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setPageSize(10);
                state.setSortColumnName("creation_time");
                state.setSortDir(TableConstants.SORT_DESC);
            }
            TableModel model = new ListTableModel(Arrays.asList(discussions), columns);
            ArrayList filters = new ArrayList();
            filters.add(new ProtectedViewFilter(coralSession.getUserSubject()));
            TableTool helper = new TableTool(state, model, filters);
            
            templatingContext.put("discussions_table", helper);

            res = coralSession.getStore().getResource(forum, "comments");
            if(res.length != 1)
            {
                screenError(getNode(), context, "comments node not found in "+forum.getPath());
            }
            Resource[] comments = coralSession.getStore().getResource(res[0]);
            
            TableState state2 = tableService.getLocalState(data, "cms:screens:forum,Forum:comments");
            if(state2.isNew())
            {
            
                state2.setViewType(TableConstants.VIEW_AS_LIST);
                state2.setPageSize(10);
                state2.setSortColumnName("creation_time");
                state2.setSortDir(TableConstants.SORT_DESC);
            }
            TableModel model2 = new ListTableModel(Arrays.asList(comments), columns);
            ArrayList filters2 = new ArrayList();
            filters2.add(new ProtectedViewFilter(coralSession.getUserSubject()));
            TableTool helper2 = new TableTool(state2, model2,filters2);
            templatingContext.put("comments_table", helper2);
        }
        catch(ForumException e)
        {
            screenError(getNode(), context, "resource not fount "+e);
        }
        catch(TableException e)
        {
            screenError(getNode(), context, "resource not fount "+e);
        }
    }
    
    public void prepareMessages(RunData data, Context context)
        throws ProcessingException
    {
        long did = parameters.getLong("did", -1);
        if(did == -1)
        {
            screenError(getNode(), context, "discussion id not found");
            return;
        }
        Subject subject = coralSession.getUserSubject();
        try
        {
            DiscussionResource discussion = DiscussionResourceImpl.getDiscussionResource(coralSession,did);
            templatingContext.put("discussion",discussion);
            
            String tableInstance = "cms:screen:forum:ForumMessages:"+getNode().getIdString()+":"+discussion.getIdString();

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
                state.setPageSize(10);
                state.setSortColumnName("creation.time");
            }
            TableModel model = new ARLTableModel(i18nContext.getLocale()());
            ArrayList filters = new ArrayList();
            filters.add(new ProtectedViewFilter(subject));
            TableTool helper = new TableTool(state, model, filters);
            
            templatingContext.put("table",helper);
        }
        catch(EntityDoesNotExistException e)
        {
            screenError(getNode(), context, "resource not fount "+e);
        }
        catch(TableException e)
        {
            screenError(getNode(), context, "failed to initialize table toolkit: "+e);
        }
        catch(Exception e)
        {
            screenError(getNode(), context, "Component exception: "+e);
        }
    }

    public void prepareMessage(RunData data, Context context)
        throws ProcessingException
    {
        long mid = parameters.getLong("mid", -1);
        if(mid == -1)
        {
            screenError(getNode(), context, "Message id not found");
            return;
        }
        try
        {
            MessageResource message = MessageResourceImpl.getMessageResource(coralSession,mid);
            templatingContext.put("message",message);
            templatingContext.put("children", Arrays.asList(coralSession.getStore().getResource(message)));
        }
        catch(EntityDoesNotExistException e)
        {
            screenError(getNode(), context, "Resource not found"+e);
        }
    }
    
    public void prepareNewMessage(RunData data, Context context)
        throws ProcessingException
    {
        long did = parameters.getLong("did", -1);
        long mid = parameters.getLong("mid", -1);
        if(mid == -1 && did == -1)
        {
            screenError(getNode(), context, "Discussion id nor Message id not found");
        }
        try
        {
            if(did != -1)
            {
                DiscussionResource discussion = DiscussionResourceImpl.getDiscussionResource(coralSession,did);
                templatingContext.put("discussion",discussion);
                templatingContext.put("parent",discussion);
            }
            else
            {
                MessageResource message = MessageResourceImpl.getMessageResource(coralSession,mid);
                DiscussionResource discussion = message.getDiscussion();
                templatingContext.put("parent_content",prepareContent(message.getContent()));
                templatingContext.put("discussion",discussion);
                templatingContext.put("parent",message);
            }
        }
        catch(EntityDoesNotExistException e)
        {
            screenError(getNode(), context, "Resource not found"+e);
        }
    }
    
    public void prepareNewDiscussion(RunData data, Context context)
        throws ProcessingException
    {
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
}
