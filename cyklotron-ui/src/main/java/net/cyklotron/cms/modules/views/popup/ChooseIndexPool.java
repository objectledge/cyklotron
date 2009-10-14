package net.cyklotron.cms.modules.views.popup;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.PathTreeElement;
import org.objectledge.table.generic.PathTreeTableModel;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * Choose category screen.
 *
 * @author <a href="pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ChooseIndexPool.java,v 1.4 2005-12-14 11:45:49 pablo Exp $
 */
public class ChooseIndexPool extends BaseCMSScreen
{
    protected SearchService searchService;
    
	protected SiteService siteService;
    
    public ChooseIndexPool(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SearchService searchService,
        SiteService siteService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.searchService = searchService;
		this.siteService = siteService;
    }
    
 	public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
		throws ProcessingException
	{
		try
		{
			SiteResource site = getSite();
			TableState state = tableStateManager.
				getState(context, "cms:category,ChooseIndexPool:"+site.getName());
			if(state.isNew())
			{
                String rootId = Integer.toString("/".hashCode());
				state.setTreeView(true);
				state.setRootId(rootId);
				state.setShowRoot(true);
				state.setExpanded(rootId);
				state.setPageSize(0);
				state.setSortColumnName("element");
			}
			TableColumn[] cols = new TableColumn[1];
			cols[0] = new TableColumn("element", PathTreeElement.getComparator("name", i18nContext.getLocale()));
			PathTreeTableModel model = new PathTreeTableModel(cols);
			model.bind("/", new PathTreeElement("/", "label"));
			bindSites(coralSession, model);
			templatingContext.put("table", new TableTool(state, null, model));
		}
		catch(Exception e)
		{
			throw new ProcessingException("failed to load information", e);
		}
	}

    
    
	protected void bindSites(CoralSession coralSession, PathTreeTableModel model)
		throws ProcessingException
	{
		SiteResource[] sites = siteService.getSites(coralSession);
		for(int i = 0; i < sites.length; i++)
		{
			model.bind("/"+sites[i].getName(), new PathTreeElement(sites[i].getName(),"site"));
			bindPools(coralSession, model, sites[i]);
		}
	}
    
	protected void bindPools(CoralSession coralSession, PathTreeTableModel model, SiteResource site)
		throws ProcessingException
	{
		try
		{
			Resource root = searchService.getPoolsRoot(coralSession, site);
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
