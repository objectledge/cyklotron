package net.cyklotron.cms.modules.views.category.query;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryListConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.ARLTableModel;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Category Query List component configuration screen.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryListComponentConf.java,v 1.3 2005-01-25 11:24:15 pablo Exp $ 
 */
public class CategoryQueryListComponentConf extends BaseCMSScreen
{
	private CategoryQueryService categoryQueryService;
	private TableService tableService;

	public CategoryQueryListComponentConf()
	{
		categoryQueryService = (CategoryQueryService)broker.getService(CategoryQueryService.SERVICE_NAME);
		tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
	}

	public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
		throws ProcessingException
	{
		CategoryQueryListConfiguration config =
			new CategoryQueryListConfiguration(prepareComponentConfig(parameters, templatingContext));
		templatingContext.put("conf", config);

		CmsData cmsData = getCmsData();
		SiteResource site = cmsData.getSite();
		if(site == null)
		{
			site = cmsData.getGlobalComponentsDataSite();
		}
		if(site == null)
		{
			throw new ProcessingException("No site selected");
		}
		
		// get currently selected pool
		try
		{
			String queryPoolName = config.getQueryPoolName();
			if(queryPoolName != null)
			{
				Resource[] res = coralSession.getStore().getResource(
					categoryQueryService.getCategoryQueryPoolRoot(site), queryPoolName);
				if(res.length == 1)
				{
					CategoryQueryPoolResource queryPool = (CategoryQueryPoolResource)res[0];
					templatingContext.put("selected_pool", queryPool);
				}
				else if(res.length > 1)
				{
					throw new ProcessingException(
						"too many category query pools with the same name");
				}
			}
		}
		catch (CategoryQueryException e)
		{
			templatingContext.put("result","exception");
			log.error("CategoryQueryException: ",e);
			return;
		}
		
		// setup table tool with pools
		try
		{
			Resource queryRoot = categoryQueryService.getCategoryQueryPoolRoot(site);
			TableState state = tableService.getGlobalState(data, "cms:category,query,CategoryQueryListComponentConf:"+site.getIdString());
			if(state.isNew())
			{
				state.setRootId(queryRoot.getIdString());
				state.setTreeView(false);
				state.setShowRoot(false);
			}
			TableTool table = new TableTool(state, new ARLTableModel(i18nContext.getLocale()()), null);
			templatingContext.put("table", table);
		}
		catch(Exception e)
		{
			throw new ProcessingException("failed to retrieve information", e);    
		}
	}
    
	public boolean checkAccessRights(Context context)
		throws ProcessingException
	{
		CmsData cmsData = getCmsData();
		if(cmsData.getNode() != null)
		{
			return cmsData.getNode().canModify(coralSession.getUserSubject());
		}
		else
		{
			return checkAdministrator(coralSession);
		}
	}
}
