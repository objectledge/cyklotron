package net.cyklotron.cms.modules.views.banner;

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
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
import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.BannersResource;
import net.cyklotron.cms.banner.BannersResourceImpl;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 *
 */
public class BannerList
    extends BaseBannerScreen
{
    
    
    public BannerList(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, BannerService bannerService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, bannerService);
        // TODO Auto-generated constructor stub
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        int bsid = parameters.getInt("bsid", -1);
        if(bsid == -1)
        {
            throw new ProcessingException("Banners root id not found");
        }
        try
        {
            BannersResource bannersRoot = BannersResourceImpl.getBannersResource(coralSession, bsid);
            templatingContext.put("bannersRoot",bannersRoot);
            Resource[] resources = coralSession.getStore().getResource(bannersRoot);
            List banners = new ArrayList();
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof BannerResource)
                {
                    banners.add(resources[i]);
                }
            }
            templatingContext.put("banners",banners);

            TableColumn[] columns = new TableColumn[3];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()));
            columns[1] = new TableColumn("creator", new CreatorNameComparator(i18nContext.getLocale()));
            columns[2] = new TableColumn("creation_time", new CreationTimeComparator());
            TableState state = tableStateManager.getState(context, "cms:screens:link,PoolList");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(banners, columns);
            templatingContext.put("table", new TableTool(state, null, model));
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("failed to lookup resource", e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize column data", e);
        }
    }
}
