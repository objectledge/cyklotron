package net.cyklotron.cms.modules.views.search;

import java.util.ArrayList;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
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
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.IndexResourceData;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.site.SiteResource;

/**
 * A screen for editing indexes.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EditIndex.java,v 1.4 2005-01-26 09:00:39 pablo Exp $
 */
public class EditIndex extends BaseSearchScreen
{

    public EditIndex(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SearchService searchService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, searchService);
        // TODO Auto-generated constructor stub
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get index if it is defined
        IndexResource index = null;
        if(parameters.isDefined("index_id"))
        {
            index = getIndex(coralSession, parameters);
            templatingContext.put("index", index);
        }
        // get index resource data
        if(parameters.getBoolean("from_list",false))
        {
            IndexResourceData.removeData(httpContext, index);
        }
        IndexResourceData indexData = IndexResourceData.getData(httpContext, index);
        templatingContext.put("index_data", indexData);

        // setup index data and table data
        SiteResource site = getSite();
        Resource root = site;
        String[] expandedResourcesIds = new String[0];
        if(indexData.isNew())
        {
            indexData.init(coralSession, index, searchService);
            expandedResourcesIds= indexData.getBranchesSelectionState()
                .getExpandedIds(coralSession, root.getId());
        }
        else
        {
            indexData.update(parameters);
        }

        // get branches tree
        try
        {
            TableState state = tableStateManager.getState(context,
                "cms.search.index.branches."+site.getName());
            if(state.isNew())
            {
                state.setRootId(root.getIdString());
                state.setShowRoot(true);
                state.setExpanded(state.getRootId());
                state.setSortColumnName("name");
                state.setTreeView(true);
            }
            
            // expand tree
            for(int i=0; i<expandedResourcesIds.length; i++)
            {
                state.setExpanded(expandedResourcesIds[i]);
            }

            TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
            ArrayList filters = new ArrayList();
            filters.add(searchService.getBranchFilter(site));
            TableTool helper = new TableTool(state, filters, model);
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
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
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

