package net.cyklotron.cms.modules.views.banner;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.banner.BannerConstants;
import net.cyklotron.cms.banner.BannerException;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.BannersResource;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

import org.objectledge.pipeline.ProcessingException;

/**
 * poll application base screen
 */
public class BaseBannerScreen
    extends BaseCMSScreen
    implements BannerConstants
{
    /** logging facility */
    protected Logger log;

    /** banner service */
    protected BannerService bannerService;

    /** banner service */
    protected PreferencesService preferencesService;

    public BaseBannerScreen()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("banner");
        bannerService = (BannerService)broker.getService(BannerService.SERVICE_NAME);
        preferencesService = (PreferencesService)broker.getService(PreferencesService.SERVICE_NAME);
    }

    protected BannersResource getBannersRoot(RunData data)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        SiteResource site;
        site = cmsData.getSite();
        if(site == null)
        {
            site = cmsData.getGlobalComponentsDataSite();
        }
        if(site == null)
        {
            throw new ProcessingException("No site selected");
        }
        try
        {
            return bannerService.getBannersRoot(site);
        }
        catch(BannerException e)
        {
            throw new ProcessingException("failed to lookup banners root");
        }
    }

    public boolean checkAccess(RunData data)
    {
        try
        {
            return coralSession.getUserSubject().hasRole(getBannersRoot(data).getAdministrator());
        }
        catch(ProcessingException e)
        {
            log.error("Subject has no rights to view this screen");
            return false;
        }
    }

}
