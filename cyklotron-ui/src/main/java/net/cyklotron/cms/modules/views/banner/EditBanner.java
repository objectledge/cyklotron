package net.cyklotron.cms.modules.views.banner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.table.TableService;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannerResourceImpl;
import net.cyklotron.cms.banner.MediaBannerResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesMapResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.site.SiteResource;

/**
 *
 * @version $Id: EditBanner.java,v 1.2 2005-01-25 11:24:01 pablo Exp $
 */
public class EditBanner
    extends BaseBannerScreen
{
    /** table service */
    TableService ts;

    /** files service */
    FilesService filesService;

    public EditBanner()
    {
        ts = (TableService)broker.getService(TableService.SERVICE_NAME);
        filesService = (FilesService)broker.getService(FilesService.SERVICE_NAME);
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
            Calendar startDate = Calendar.getInstance(i18nContext.getLocale()());
            startDate.setTime(banner.getStartDate());
            templatingContext.put("start_date",startDate);
            Calendar endDate = Calendar.getInstance(i18nContext.getLocale()());
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
                    FilesMapResource mediaNode = filesService.getFilesRoot(site);
                    parameters.set("dirs",mediaNode.getIdString());
                    Resource mediaResource = ((MediaBannerResource)banner).getMedia();
                    if(parameters.get("reset").asBoolean(false))
                    {
                        if(parameters.get("dir").asLong(-1) == -1)
                        {
                            parameters.set("dir",mediaResource.getParent(.getIdString()));
                        }
                        if(parameters.get("file").asLong(-1) == -1)
                        {
                            parameters.set("file",mediaResource.getIdString());
                        }
                    }
                }
            }
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
