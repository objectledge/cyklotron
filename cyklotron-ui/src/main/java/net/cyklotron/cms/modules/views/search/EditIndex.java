package net.cyklotron.cms.modules.views.search;

import java.util.ArrayList;

import net.labeo.services.resource.Resource;
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

import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.IndexResourceData;
import net.cyklotron.cms.site.SiteResource;

/**
 * A screen for editing indexes.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EditIndex.java,v 1.3 2005-01-25 11:24:16 pablo Exp $
 */
public class EditIndex extends BaseSearchScreen
{
    /** table service for feed list display. */
    TableService tableService = null;

    public EditIndex()
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get index if it is defined
        IndexResource index = null;
        if(parameters.isDefined("index_id"))
        {
            index = getIndex(data);
            templatingContext.put("index", index);
        }
        // get index resource data
        if(parameters.get("from_list").asBoolean(false))
        {
            IndexResourceData.removeData(data, index);
        }
        IndexResourceData indexData = IndexResourceData.getData(data, index);
        templatingContext.put("index_data", indexData);

        // setup index data and table data
        SiteResource site = getSite();
        Resource root = site;
        String[] expandedResourcesIds = new String[0];
        if(indexData.isNew())
        {
            indexData.init(index, searchService);
            expandedResourcesIds= indexData.getBranchesSelectionState()
                .getExpandedIds(coralSession, root.getId());
        }
        else
        {
            indexData.update(data);
        }

        // get branches tree
        try
        {
            TableState state = tableService.getLocalState(data,
                "cms.search.index.branches."+site.getName());
            if(state.isNew())
            {
                state.setRootId(root.getIdString());
                state.setShowRoot(true);
                state.setExpanded(state.getRootId());
                state.setSortColumnName("name");
                state.setViewType(TableConstants.VIEW_AS_TREE);
                
            }
            
            // expand tree
            for(int i=0; i<expandedResourcesIds.length; i++)
            {
                state.setExpanded(expandedResourcesIds[i]);
            }

            TableModel model = new ARLTableModel(i18nContext.getLocale()());
            ArrayList filters = new ArrayList();
            filters.add(searchService.getBranchFilter(site));
            TableTool helper = new TableTool(state, model, filters);
            templatingContext.put("table", helper);
        }
        catch(TableException e)
        {
            throw new ProcessingException("table tool setup failed", e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        if(parameters.isDefined("index_id"))
        {
            return checkPermission(context, coralSession, "cms.search.index.modify");
        }
        else
        {
            return checkPermission(context, coralSession, "cms.search.index.add");
        }
    }
}

