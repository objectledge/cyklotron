package net.cyklotron.cms.modules.views.banner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannerResourceImpl;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.MediaBannerResource;
import net.cyklotron.cms.banner.PoolResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesMapResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

/**
 *
 * @version $Id: EditBanner.java,v 1.4 2005-12-29 12:04:49 pablo Exp $
 */
public class EditBanner
    extends BaseBannerScreen
{
    /** files service */
    FilesService filesService;
    
    public EditBanner(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, BannerService bannerService,
        FilesService filesService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, bannerService);
        this.filesService = filesService;
    }


    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        int bid = parameters.getInt("bid", -1);
        if(bid == -1)
        {
            throw new ProcessingException("Banner id not found");
        }
        BannerResource banner = null;
        try
        {
            banner = BannerResourceImpl.getBannerResource(coralSession, bid);
            templatingContext.put("banner",banner);

            // calendar support
            Calendar startDate = Calendar.getInstance(i18nContext.getLocale());
            startDate.setTime(banner.getStartDate());
            templatingContext.put("start_date",startDate);
            Calendar endDate = Calendar.getInstance(i18nContext.getLocale());
            endDate.setTime(banner.getEndDate());
            templatingContext.put("end_date",endDate);
            List days = new ArrayList(31);
            for(int i = 1; i <=31; i++)
            {
                days.add(new Integer(i));
            }
            templatingContext.put("days",days);
            List months = new ArrayList(12);
            for(int i = 0; i <=11; i++)
            {
                months.add(new Integer(i));
            }
            templatingContext.put("months",months);
            List years = new ArrayList(20);
            for(int i = 2000; i <=2020; i++)
            {
                years.add(new Integer(i));
            }
            templatingContext.put("years",years);


            if(banner instanceof MediaBannerResource)
            {
                SiteResource site = getSite();
                if(site != null)
                {
                    FilesMapResource mediaNode = filesService.getFilesRoot(coralSession, site);
                    parameters.set("dirs",mediaNode.getIdString());
                    Resource mediaResource = ((MediaBannerResource)banner).getMedia();
                    if(parameters.getBoolean("reset",false))
                    {
                        if(parameters.getLong("dir",-1) == -1)
                        {
                            parameters.set("dir",mediaResource.getParent().getIdString());
                        }
                        if(parameters.getLong("file",-1) == -1)
                        {
                            parameters.set("file",mediaResource.getIdString());
                        }
                    }
                }
            }
            Resource[] resources = coralSession.getStore().getResource(banner.getParent());
            List pools = new ArrayList();
            Map selectionMap = new HashMap();
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof PoolResource)
                {
                    pools.add(resources[i]);
                    List banners = ((PoolResource)resources[i]).getBanners();
                    selectionMap.put(resources[i], new Boolean(false));
                    if(banners != null)
                    {
                        for(int j = 0; j < banners.size(); j++)
                        {
                            if(banner.equals(banners.get(j)))
                            {
                                selectionMap.put(resources[i], new Boolean(true));
                                break;
                            }
                        }
                    }
                }
            }
            Collections.sort(pools, new NameComparator(i18nContext.getLocale()));
            templatingContext.put("pools", pools);
            templatingContext.put("pools_map", selectionMap);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("Cannot create TableTool", e);
        }
        catch(FilesException e)
        {
            throw new ProcessingException("Cannot create TableTool", e);
        }
    }
}
