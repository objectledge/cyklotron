package net.cyklotron.cms.modules.actions.banner;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.banner.BannerException;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.BannersResource;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.workflow.WorkflowService;

import org.objectledge.pipeline.ProcessingException;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BaseBannerAction.java,v 1.1 2005-01-24 04:34:40 pablo Exp $
 */
public abstract class BaseBannerAction
    extends BaseCMSAction
{
    /** logging facility */
    protected Logger log;

    /** banner service */
    protected BannerService bannerService;

    /** workflow service */
    protected WorkflowService workflowService;

    public BaseBannerAction()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("banner");
        bannerService = (BannerService)broker.getService(BannerService.SERVICE_NAME);
        workflowService = (WorkflowService)broker.getService(WorkflowService.SERVICE_NAME);
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


