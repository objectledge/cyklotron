package net.cyklotron.cms.modules.views.popup;

import net.labeo.services.resource.Resource;
import net.labeo.services.table.PathTreeElement;
import net.labeo.services.table.PathTreeTableModel;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * Choose category screen.
 *
 * @author <a href="pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ChooseIndexPool.java,v 1.2 2005-01-25 11:23:55 pablo Exp $
 */
public class ChooseIndexPool extends BaseCMSScreen
{
    protected SearchService searchService;
    
	protected SiteService siteService;
    
    protected TableService tableService;
    
    public ChooseIndexPool()
    {
        searchService = (SearchService)broker.
            getService(SearchService.SERVICE_NAME);
		siteService = (SiteService)broker.
					getService(SiteService.SERVICE_NAME);
        tableService = (TableService)broker.
            getService(TableService.SERVICE_NAME); 
    }
    
 	public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
		throws ProcessingException
	{
		try
		{
			SiteResource site = getSite();
			TableState state = tableService.
				getGlobalState(data, "cms:category,ChooseIndexPool:"+site.getName());
			if(state.isNew())
			{
				state.setViewType(TableConstants.VIEW_AS_TREE);
				state.setRootId("0");
				state.setShowRoot(true);
				state.setExpanded("0");
				state.setPageSize(0);
				state.setMultiSelect(false);
				state.setSortColumnName("element");
			}
			TableColumn[] cols = new TableColumn[1];
			cols[0] = new TableColumn("element", PathTreeElement.getComparator("name", i18nContext.getLocale()()));
			PathTreeTableModel model = new PathTreeTableModel(cols);
			model.bind("/", new PathTreeElement("/", "label"));
			bindSites(model);
			templatingContext.put("table", new TableTool(state, model, null));
		}
		catch(Exception e)
		{
			throw new ProcessingException("failed to load information", e);
		}
	}

    
    
	protected void bindSites(PathTreeTableModel model)
		throws ProcessingException
	{
		SiteResource[] sites = siteService.getSites();
		for(int i = 0; i < sites.length; i++)
		{
			model.bind("/"+sites[i].getName(), new PathTreeElement(sites[i].getName(),"site"));
			bindPools(model, sites[i]);
		}
	}
    
	protected void bindPools(PathTreeTableModel model, SiteResource site)
		throws ProcessingException
	{
		try
		{
			Resource root = searchService.getPoolsRoot(site);
			Resource[] pools = coralSession.getStore().getResource(root); 
			for(int i = 0; i < pools.length; i++)
			{	
				if(pools[i] instanceof PoolResource)
				{
					String pool = pools[i].getName();
					PathTreeElement elm = new PathTreeElement(pool,"pool");
					elm.set("pool", pools[i]);
					model.bind("/"+site.getName()+"/"+pool, elm);
				}
			}
		}
		catch(Exception e)
		{
			throw new ProcessingException("Exception occurred",e);
		}
	}
    
    
    
}
