package net.cyklotron.cms.modules.views.structure;

import java.util.ArrayList;
import java.util.List;

import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.table.NavigationTableModel;
import net.cyklotron.cms.util.ProtectedViewFilter;

/**
 * Base class for node list screens.
 */
public abstract class BaseNodeListScreen
extends BaseStructureScreen
{
    /** table service */
    protected TableService tableService;

    public BaseNodeListScreen()
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }

    public void prepareTableState(RunData data, Context context,
            NavigationNodeResource rootNode, NavigationNodeResource selectedNode)
        throws ProcessingException
    {
        TableState state = tableService.getLocalState(data, getStateName(data));
        if(state.isNew())
        {
            state.setRootId(rootNode.getIdString());
            state.setExpanded(rootNode.getIdString());

            state.setMultiSelect(false);
            state.setViewType(TableConstants.VIEW_AS_TREE);
            state.setShowRoot(true);
            state.setSortColumnName("sequence");
        }

        if(selectedNode != null)
        {
            String selectedNodeId = selectedNode.getIdString();
            state.setExpanded(selectedNodeId);
            state.setSelected(selectedNodeId);

            List expandedList = selectedNode.getParentNavigationNodes(true);
            for(int i = 0; i < expandedList.size(); i++)
            {
                state.setExpanded(((NavigationNodeResource)expandedList.get(i)).getIdString());
            }
        }

        TableModel model = new NavigationTableModel(i18nContext.getLocale()());
        try
        {
            ArrayList filters = new ArrayList();
            filters.add(new ProtectedViewFilter(coralSession.getUserSubject()));
            TableTool helper = new TableTool(state, model, filters);
            templatingContext.put("table", helper);
        }
        catch(TableException e)
        {
            throw new ProcessingException("Cannot create TableTool", e);
        }
    }

    protected abstract String getStateName(RunData data)
        throws ProcessingException;
}
