package net.cyklotron.cms.modules.views.forum;

import net.labeo.Labeo;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.table.ARLTableModel;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.DiscussionResourceImpl;

/**
 * The discussion list screen class.
 */
public class MessageList
    extends BaseForumScreen
    implements Secure
{
    private TableService tableService;

    public MessageList()
    {
        tableService = (TableService)Labeo.getBroker().getService(TableService.SERVICE_NAME);
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
            TableState state = tableService.getLocalState(data, componentName);
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_TREE);
                state.setMultiSelect(false);
                state.setCurrentPage(0);
				state.setShowRoot(false);
                // TODO: configure default
                state.setPageSize(4);
                state.setSortColumnName("creation.time");
            }
			state.setRootId(rootId);
			state.setExpanded(rootId);

            TableModel model = new ARLTableModel(i18nContext.getLocale()());
            TableTool helper = null;
            try
            {
                helper = new TableTool(state, model, null);
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
