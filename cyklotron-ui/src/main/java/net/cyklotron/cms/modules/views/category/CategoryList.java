package net.cyklotron.cms.modules.views.category;

import java.util.Iterator;
import java.util.Set;

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

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.CategoryException;
import net.cyklotron.cms.category.CategoryInfoTool;
import net.cyklotron.cms.site.SiteResource;

/**
 * Screen showing available categories, presented as tree.
 * This screen is not protected because everyone should be able to see defined categories.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CategoryList.java,v 1.1 2005-01-24 04:34:27 pablo Exp $
 */
public class CategoryList
    extends BaseCategoryScreen
{
    protected TableService tableService;

    public CategoryList()
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            String rootId = categoryService.getCategoryRoot(cmsDataFactory.getCmsData(context).getSite()).getIdString();
            prepareTableTool(data, rootId, "table");
            templatingContext.put("category_tool", new CategoryInfoTool(data));
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve category root information", e);
        }

    }

    protected String getTableStateBaseName()
    {
        return "cms:screens:category,CategoryList";
    }

    protected TableState prepareTableTool(RunData data, String rootId, String tableToolName)
        throws ProcessingException
    {
        TableState state = tableService.getLocalState(data, getTableStateBaseName()+rootId);
        if(state.isNew())
        {
            state.setViewType(TableConstants.VIEW_AS_TREE);
            state.setCurrentPage(0);
            state.setMultiSelect(false);
            state.setShowRoot(true);
            state.setSortColumnName("name");

            state.setRootId(rootId);
            state.setExpanded(rootId);
        }

        TableModel model = new ARLTableModel(i18nContext.getLocale()());
        try
        {
            TableTool helper = new TableTool(state, model, null);
            data.getContext().put(tableToolName, helper);
        }
        catch(TableException e)
        {
            throw new ProcessingException("Cannot create TableTool", e);
        }

        return state;
    }

    protected void prepareTableTools(RunData data, Set expandedCategoriesIds)
        throws ProcessingException
    {
        // prepare category tree or trees
		prepareGlobalCategoriesTableTool(data, expandedCategoriesIds);
		SiteResource site = getSiteResource(data);
		prepareSiteCategoriesTableTool(data, expandedCategoriesIds, site);
    }

	protected void prepareGlobalCategoriesTableTool(RunData data, Set expandedCategoriesIds)
		throws ProcessingException
	{
		// global categories
		try
		{
			String rootId = categoryService.getCategoryRoot(null).getIdString();
			TableState state = prepareTableTool(data, rootId, "globaltable");
			setExpanded(state, expandedCategoriesIds);
		}
		catch(CategoryException e)
		{
			throw new ProcessingException("failed to retrieve global category root", e);
		}
	}

	protected void prepareSiteCategoriesTableTool(RunData data, Set expandedCategoriesIds, SiteResource site)
		throws ProcessingException
	{
		// site categories
		if(site != null)
		{
			try
			{
				String rootId = categoryService.getCategoryRoot(site).getIdString();
				TableState state = prepareTableTool(data, rootId, "sitetable");
				setExpanded(state, expandedCategoriesIds);
			}
			catch(CategoryException e)
			{
				throw new ProcessingException("failed to retrieve site category root", e);
			}
		}
	}
    
    protected SiteResource getSiteResource(RunData data)
     	throws ProcessingException
    {
		CmsData cmsData = cmsDataFactory.getCmsData(context);
    	SiteResource site = cmsData.getSite();
		if(site == null)
		{
			site = cmsData.getGlobalComponentsDataSite();
		}
		return site;
    }

    private final void setExpanded(TableState state, Set expandedIds)
    {
        if(state.isNew())
        {
            for(Iterator i=expandedIds.iterator(); i.hasNext();)
            {
                state.setExpanded(((Long)(i.next())).toString());
            }
        }
    }
}
