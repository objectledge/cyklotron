/*
 * Created on Oct 15, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.views.category.query;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.cyklotron.cms.category.CategoryInfoTool;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryResourceData;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.modules.views.category.CategoryList;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.table.ResourceListTableModel;
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
 * @author fil
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryEdit.java,v 1.2 2005-01-24 10:27:47 pablo Exp $
 */
public class CategoryQueryEdit 
    extends CategoryList
{
    protected CategoryQueryService categoryQueryService;
    
    protected TableService tableService;
    
    public CategoryQueryEdit()
    {
        tableService = (TableService) broker.getService(TableService.SERVICE_NAME);
        categoryQueryService =
            (CategoryQueryService) broker.getService(CategoryQueryService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) 
        throws ProcessingException
    {
        // get query if it is defined
        CategoryQueryResource query = null;
        if (parameters.isDefined(CategoryQueryUtil.QUERY_PARAM))
        {
            query = CategoryQueryUtil.getQuery(coralSession, data);
            templatingContext.put("query", query);
        }
        // get pool resource data
        if (parameters.get("from_list").asBoolean(false))
        {
            CategoryQueryResourceData.removeData(data, query);
        }
        CategoryQueryResourceData queryData = CategoryQueryResourceData.getData(data, query);
        templatingContext.put("query_data", queryData);

        Set expandedCategoriesIds = new HashSet();
        // setup pool data and table data
        if (queryData.isNew())
        {
            queryData.init(coralSession, query);
            // prepare expanded categories - includes inherited ones
            Map initialState = queryData.getCategoriesSelection().getResources(coralSession);
            for(Iterator i=initialState.keySet().iterator(); i.hasNext();)
            {
                CategoryResource category = (CategoryResource)(i.next());
                CategoryResource[] cats = categoryService.getImpliedCategories(category, true);
                for(int j=0; j<cats.length; j++)
                {
                    expandedCategoriesIds.add(cats[j].getIdObject());
                }
            }
        }
        else
        {
            queryData.update(data);
        }
        
        // categories
        prepareGlobalCategoriesTableTool(data, expandedCategoriesIds);
        String[] siteNames = queryData.getSiteNames();
        if(siteNames.length == 1)
        {
            try
            {
				SiteResource site = siteService.getSite(siteNames[0]);
				prepareSiteCategoriesTableTool(data, expandedCategoriesIds, site);
            }
            catch (SiteException e)
            {
            	throw new ProcessingException("Cannot find selected site", e);
            }
		}
        // resource classes   
        templatingContext.put("category_tool", new CategoryInfoTool(data));
        
		// prepare sites list
		SiteResource[] sites = siteService.getSites();
		SiteResource site = getSite();
		Subject current = coralSession.getUserSubject();
		TableState state = tableService.getLocalState(data, "cms:screens:category,query,CategoryQuery:siteList");
		if(state.isNew())
		{
			state.setViewType(TableConstants.VIEW_AS_LIST);
			state.setSortColumnName("name");
		}
		try
		{
			TableModel model = new ResourceListTableModel(sites, i18nContext.getLocale()());
			templatingContext.put("site_list", new TableTool(state, model, null));
		}
		catch(TableException e)
		{
			throw new ProcessingException("failed to initialize table", e);
		}
    }

    protected String getTableStateBaseName()
    {
        return "cms:screens:category,query,CategoryQuery:categoryList";
    }
    
    public boolean checkAccessRights(Context context) throws ProcessingException
    {
        if (parameters.isDefined(CategoryQueryUtil.QUERY_PARAM))
        {
            return checkPermission(context, coralSession, "cms.category.query.pool.modify");
        }
        else
        {
            return checkPermission(context, coralSession, "cms.category.query.pool.add");
        }
    }    
}
