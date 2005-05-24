package net.cyklotron.cms.modules.views.banner;

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.CreationTimeComparator;
import org.objectledge.coral.table.comparator.CreatorNameComparator;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.ListTableModel;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.BannersResource;
import net.cyklotron.cms.banner.PoolResource;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 *
 */
public class BannerConf
	extends PoolList
{
	/**
	 * Builder constructor. 
	 * 
	 * @param context
	 * @param logger
	 * @param preferencesService
	 * @param cmsDataFactory
	 * @param tableStateManager
	 * @param bannerService
	 */
	public BannerConf(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        BannerService bannerService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, bannerService);
    }
    
    /**
     * {@inheritDoc}
     */
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession) throws ProcessingException
    {
		try
        {
            BannersResource bannersRoot = getBannersRoot(coralSession);
            templatingContext.put("bannersRoot",bannersRoot);
            Resource[] resources = coralSession.getStore().getResource(bannersRoot);
            List<Resource> pools = new ArrayList<Resource>();
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof PoolResource)
                {
                    pools.add(resources[i]);
                }
            }
            TableColumn[] columns = new TableColumn[3];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()));
            columns[1] = new TableColumn("creator", new CreatorNameComparator(i18nContext.getLocale()));
            columns[2] = new TableColumn("creation_time", new CreationTimeComparator());
            TableState state = tableStateManager.getState(context, "cms:screens:banner,PoolList");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(pools, columns);
            templatingContext.put("table", new TableTool(state, null, model));
			Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
			long poolId = componentConfig.getLong("pid",-1);
	        if(poolId != -1)
	        {
				try
				{
					Resource pool = coralSession.getStore().getResource(poolId);
					templatingContext.put("pool",pool);
				}
				catch(EntityDoesNotExistException e)
				{
					//non existing pool may be configured
				}
	        }
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize column data", e);
        }
    }
}
