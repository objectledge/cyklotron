package net.cyklotron.cms.modules.views.category;

import java.util.Iterator;
import java.util.Set;

import org.jcontainer.dna.Logger;
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

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryException;
import net.cyklotron.cms.category.CategoryInfoTool;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * Screen showing available categories, presented as tree.
 * This screen is not protected because everyone should be able to see defined categories.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CategoryList.java,v 1.5 2005-08-10 05:31:12 rafal Exp $
 */
public class CategoryList
    extends BaseCategoryScreen
{
    
    
    public CategoryList(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryService categoryService,
        SiteService siteService, IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        categoryService, siteService, integrationService);
        
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            String rootId = categoryService.getCategoryRoot(coralSession, cmsDataFactory.getCmsData(context).getSite()).getIdString();
            prepareTableTool(coralSession, templatingContext, i18nContext, rootId, "table", false);
            templatingContext.put("category_tool", new CategoryInfoTool(context,integrationService, categoryService));
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

    protected TableState prepareTableTool(CoralSession coralSession, TemplatingContext templatingContext,
        I18nContext i18nContext, String rootId, String tableToolName, boolean reset)
        throws ProcessingException
    {
        TableState state = tableStateManager.getState(context, getTableStateBaseName()+rootId);
        if(state.isNew() || reset)
        {
            state.setTreeView(true);
            state.setCurrentPage(0);
            state.setShowRoot(true);
            state.setSortColumnName("name");
            state.setRootId(rootId);
            state.clearExpanded();
            state.setExpanded(rootId);
        }

        TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
        try
        {
            TableTool helper = new TableTool(state, null, model);
            templatingContext.put(tableToolName, helper);
        }
        catch(TableException e)
        {
            throw new ProcessingException("Cannot create TableTool", e);
        }

        return state;
    }

    protected void prepareTableTools(CoralSession coralSession,
        TemplatingContext templatingContext, I18nContext i18nContext, Set expandedCategoriesIds,
        boolean reset)
        throws ProcessingException
    {
        // prepare category tree or trees
		prepareGlobalCategoriesTableTool(coralSession, templatingContext, i18nContext, expandedCategoriesIds, reset);
		SiteResource site = getSiteResource();
		prepareSiteCategoriesTableTool(coralSession, templatingContext, i18nContext, expandedCategoriesIds, site, reset);
    }

	protected void prepareGlobalCategoriesTableTool(CoralSession coralSession, 
        TemplatingContext templatingContext, I18nContext i18nContext, Set expandedCategoriesIds,
        boolean reset )
		throws ProcessingException
	{
		// global categories
		try
		{
			String rootId = categoryService.getCategoryRoot(coralSession, null).getIdString();
			TableState state = prepareTableTool(coralSession,
                templatingContext, i18nContext, rootId, "globaltable", reset);
			setExpanded(state, expandedCategoriesIds, reset);
		}
		catch(CategoryException e)
		{
			throw new ProcessingException("failed to retrieve global category root", e);
		}
	}

	protected void prepareSiteCategoriesTableTool(CoralSession coralSession, 
        TemplatingContext templatingContext, I18nContext i18nContext,
        Set expandedCategoriesIds, SiteResource site, boolean reset)
		throws ProcessingException
	{
		// site categories
		if(site != null)
		{
			try
			{
				String rootId = categoryService.getCategoryRoot(coralSession, site).getIdString();
				TableState state = prepareTableTool(coralSession, templatingContext,
                    i18nContext, rootId, "sitetable", reset);
				setExpanded(state, expandedCategoriesIds, reset);
			}
			catch(CategoryException e)
			{
				throw new ProcessingException("failed to retrieve site category root", e);
			}
		}
	}
    
    protected SiteResource getSiteResource()
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

    private final void setExpanded(TableState state, Set expandedIds, boolean reset)
    {
        if(state.isNew() || reset)
        {
            for(Iterator i=expandedIds.iterator(); i.hasNext();)
            {
                state.setExpanded(((Long)(i.next())).toString());
            }
        }
    }
}
