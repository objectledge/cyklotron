package net.cyklotron.cms.modules.views.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import net.cyklotron.cms.category.CategoryException;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.IndexResourceData;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.util.SeeableFilter;

/**
 * A screen for editing indexes.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EditIndex.java,v 1.6 2007-02-25 14:18:52 pablo Exp $
 */
public class EditIndex extends BaseSearchScreen
{

    private CategoryService categoryService;
    
    private CategoryQueryService categoryQueryService;
    
    private IntegrationService integrationService;
    
    public EditIndex(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,CategoryService categoryService,CategoryQueryService categoryQueryService,IntegrationService integrationService,
        SearchService searchService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, searchService);
        this.categoryService = categoryService;
        this.categoryQueryService = categoryQueryService; 
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
        Map selectedCategories = new HashMap();
        if(indexData.isNew())
        {
            indexData.init(coralSession, index, searchService, categoryQueryService);
            expandedResourcesIds = indexData.getBranchesSelectionState().getExpandedIds(
                coralSession, root.getId());
            selectedCategories = indexData.getCategoriesSelection().getEntities(coralSession);
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

            Set expandedCategoriesIds = new HashSet();
            for(Iterator i = selectedCategories.keySet().iterator(); i.hasNext();)
            {
                CategoryResource category = (CategoryResource)(i.next());
                CategoryResource[] cats = categoryService.getImpliedCategories(category, true);
                for(int j = 0; j < cats.length; j++)
                {
                    expandedCategoriesIds.add(cats[j].getIdObject());
                }
            }

            // global categories.
            prepareCategoriesTableTool(coralSession, templatingContext, i18nContext, "globaltable",
                expandedCategoriesIds, model, null);
            // local categories.
            prepareCategoriesTableTool(coralSession, templatingContext, i18nContext, "sitetable",
                expandedCategoriesIds, model, site);
            
            templatingContext.put("query_data", indexData);
        }
        catch(TableException e)
        {
            throw new ProcessingException("table tool setup failed", e);
        }
    }
    
    protected void prepareCategoriesTableTool(CoralSession coralSession, 
        TemplatingContext templatingContext, I18nContext i18nContext,String tableName,Set expandedIds,TableModel model,SiteResource site)
        throws ProcessingException
    {
            try
            {
                String rootId;
                TableState state;
                if(site == null){
                    state = tableStateManager.getState(context,"cms.search.index.categories.global");
                }else{
                    state = tableStateManager.getState(context,"cms.search.index.categories.local."+ site.getName());
                }
                rootId = categoryService.getCategoryRoot(coralSession, site).getIdString();
                
                if(state.isNew())
                {
                    state.setTreeView(true);
                    state.setCurrentPage(0);
                    state.setShowRoot(true);
                    state.setSortColumnName("name");
                    state.setRootId(rootId);
                    state.clearExpanded();
                    state.setExpanded(rootId);
                    for(Iterator i=expandedIds.iterator(); i.hasNext();)
                    {
                        state.setExpanded(((Long)(i.next())).toString());
                    }
                }
                List<TableFilter<Resource>> filters = new ArrayList<TableFilter<Resource>>();
                filters.add(new SeeableFilter());
                TableTool tableTool = new TableTool(state, filters, model);
                templatingContext.put(tableName, tableTool);
            }
            catch(CategoryException e)
            {
                throw new ProcessingException("failed to retrieve category root", e);
            }
            catch(TableException e)
            {
                throw new ProcessingException("failed to create table tool", e);
            }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("search"))
        {
            logger.debug("Application 'search' not enabled in site");
            return false;
        }
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

