package net.cyklotron.cms.modules.views.forum;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.DiscussionResourceImpl;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * The discussion list screen class.
 */
public class MessageList
    extends BaseForumScreen
{


    public MessageList(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, ForumService forumService,
        WorkflowService workflowService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, forumService,
                        workflowService);
        // TODO Auto-generated constructor stub
    }
    // TODO: this components name should inclue forum's ID to make message list states'
    // for different forums separate
    String componentName = "message_tree";

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        long did = parameters.getLong("did", -1);
        if(did == -1)
        {
            throw new ProcessingException("Discussion id not found");
        }
        try
        {
            DiscussionResource discussion = DiscussionResourceImpl.getDiscussionResource(coralSession,did);
            templatingContext.put("discussion",discussion);

			String rootId = discussion.getIdString();
            TableState state = tableStateManager.getState(context, componentName);
            if(state.isNew())
            {
                state.setTreeView(true);
                state.setCurrentPage(0);
				state.setShowRoot(false);
                // TODO: configure default
                state.setPageSize(4);
                state.setSortColumnName("creation.time");
            }
			state.setRootId(rootId);
			state.setExpanded(rootId);

            TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
            TableTool helper = null;
            try
            {
                helper = new TableTool(state, null, model);
                templatingContext.put("table", helper);
            }
            catch(TableException e)
            {
                throw new ProcessingException("Cannot create TableTool", e);
            }
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("Resource not found",e);
        }
    }
}
