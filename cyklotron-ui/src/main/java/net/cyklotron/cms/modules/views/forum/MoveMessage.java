package net.cyklotron.cms.modules.views.forum;

import java.util.ArrayList;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.DiscussionResourceImpl;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.forum.MessageResourceImpl;
import net.cyklotron.cms.forum.MessageTableModel;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.util.ProtectedViewFilter;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * The discussion list screen class.
 */
public class MoveMessage
    extends BaseForumScreen
{

    public MoveMessage(org.objectledge.context.Context context, Logger logger,
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
        
        long mid = parameters.getLong("mid", -1);
        if(mid == -1)
        {
            throw new ProcessingException("message id not found");
        }
        
        long did = parameters.getLong("did", -1);
        if(did == -1)
        {
            throw new ProcessingException("Discusion id not found");
        }
        
        
        
        try
        {
            DiscussionResource discussion = DiscussionResourceImpl.getDiscussionResource(coralSession, did);
            templatingContext.put("discussion", discussion);
            
            MessageResource message = MessageResourceImpl.getMessageResource(coralSession, mid);
            templatingContext.put("message", message);   
            
            String tableInstance = "cms:screen:forum:ForumMoveMessage:"+discussion.getIdString();
            TableState state = tableStateManager.getState(context, tableInstance);
            if(state.isNew())
            {
                state.setTreeView(true);
                String rootId = discussion.getIdString();
                state.setRootId(rootId);
                state.setCurrentPage(0);
                state.setShowRoot(false);
                state.setExpanded(rootId);
                state.setAllExpanded(parameters.getBoolean("expand_all", false));
                state.setPageSize(50);
                state.setSortColumnName("creation.time");
                state.setAscSort(false);
            }
            
            Resource res = message;
            while(!discussion.equals(res.getParent()))
            {
                res = res.getParent();
                state.setExpanded(res.getIdString()); 
            }
            
            TableModel model = new MessageTableModel(coralSession, i18nContext.getLocale());
            ArrayList<TableFilter> filters = new ArrayList<TableFilter>();
            filters.add(new ProtectedViewFilter(coralSession, coralSession.getUserSubject()));
            TableTool helper = new TableTool(state, filters, model);
            
            templatingContext.put("table",helper);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("resource not fount ", e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize table toolkit: ", e);
        }
        catch(Exception e)
        {
            throw new ProcessingException("Component exception: ", e);
        }
    }
    
    public boolean checkAccessRights(Context context) 
    throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("forum"))
        {
            logger.debug("Application 'forum' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        Permission modify = coralSession.getSecurity().getUniquePermission("cms.forum.modify");
        long did = parameters.getLong("did", -1);
        if(did != -1)
        {
            try
            {
                Resource discussion = coralSession.getStore().getResource(did);
                return coralSession.getUserSubject().hasPermission(discussion, modify);
            }   
            catch(Exception e)
            {
                throw new ProcessingException("failed to check access rights", e);
            }    
        }
        return false;
    }
}
