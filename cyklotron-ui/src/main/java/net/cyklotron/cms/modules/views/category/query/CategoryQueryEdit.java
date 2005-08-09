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

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.ResourceListTableModel;
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
import net.cyklotron.cms.category.CategoryInfoTool;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryResourceData;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.category.CategoryList;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * @author fil
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryEdit.java,v 1.4.2.1 2005-08-09 08:19:00 pablo Exp $
 */
public class CategoryQueryEdit 
    extends CategoryList
{
    protected CategoryQueryService categoryQueryService;
    
    public CategoryQueryEdit(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryService categoryService,
        SiteService siteService, IntegrationService integrationService,
        CategoryQueryService categoryQueryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        categoryService, siteService, integrationService);
        this.categoryQueryService = categoryQueryService;
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) 
        throws ProcessingException
    {
        // get query if it is defined
        CategoryQueryResource query = null;
        if (parameters.isDefined(CategoryQueryUtil.QUERY_PARAM))
        {
            query = CategoryQueryUtil.getQuery(coralSession, parameters);
            templatingContext.put("query", query);
        }
        // get pool resource data
        if (parameters.getBoolean("from_list",false))
        {
            CategoryQueryResourceData.removeData(httpContext, query);
        }
        CategoryQueryResourceData queryData = CategoryQueryResourceData.getData(httpContext, query);
        templatingContext.put("query_data", queryData);

        Set expandedCategoriesIds = new HashSet();
        // setup pool data and table data
        if (queryData.isNew())
        {
            queryData.init(coralSession, query, categoryQueryService, integrationService);
            // prepare expanded categories - includes inherited ones
            Map initialState = queryData.getCategoriesSelection().getEntities(coralSession);
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
            queryData.update(parameters);
        }
        
        // categories
        prepareGlobalCategoriesTableTool(coralSession, templatingContext, i18nContext
            , expandedCategoriesIds, false);
        String[] siteNames = queryData.getSiteNames();
        if(siteNames.length == 1)
        {
            try
            {
				SiteResource site = siteService.getSite(coralSession, siteNames[0]);
				prepareSiteCategoriesTableTool(coralSession, templatingContext, i18nContext, expandedCategoriesIds, site, false);
            }
            catch (SiteException e)
            {
            	throw new ProcessingException("Cannot find selected site", e);
            }
		}
        // resource classes   
        templatingContext.put("category_tool", new CategoryInfoTool(context,integrationService, categoryService));
        
		// prepare sites list
		SiteResource[] sites = siteService.getSites(coralSession);
		SiteResource site = getSite();
		Subject current = coralSession.getUserSubject();
		TableState state = tableStateManager.getState(context, "cms:screens:category,query,CategoryQuery:siteList");
		if(state.isNew())
		{
			state.setTreeView(false);
			state.setSortColumnName("name");
		}
		try
		{
			TableModel model = new ResourceListTableModel(sites, i18nContext.getLocale());
			templatingContext.put("site_list", new TableTool(state,null, model));
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
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
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
