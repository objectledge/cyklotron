package net.cyklotron.cms.modules.views.banner;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.banner.BannerException;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.BannersResource;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

/**
 * poll application base screen
 */
public abstract class BaseBannerScreen
    extends BaseCMSScreen
{
    /** banner service */
    protected BannerService bannerService;

    public BaseBannerScreen(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        BannerService bannerService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.bannerService = bannerService;
    }

    protected BannersResource getBannersRoot(CoralSession coralSession)
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
            return bannerService.getBannersRoot(coralSession, site);
        }
        catch(BannerException e)
        {
            throw new ProcessingException("failed to lookup banners root");
        }
    }

    public boolean checkAccessRights(Context context)
        throws Exception
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("banner"))
        {
            logger.debug("Application 'banner' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        try
        {
            return coralSession.getUserSubject().hasRole(getBannersRoot(coralSession).getAdministrator());
        }
        catch(ProcessingException e)
        {
            logger.error("Subject has no rights to view this screen");
            return false;
        }
    }

}
