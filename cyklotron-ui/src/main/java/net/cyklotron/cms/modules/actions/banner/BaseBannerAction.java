package net.cyklotron.cms.modules.actions.banner;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.banner.BannerException;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.BannersResource;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BaseBannerAction.java,v 1.2 2005-01-24 10:27:29 pablo Exp $
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

    
    
    public BaseBannerAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, BannerService bannerService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory);
        this.bannerService = bannerService;
        this.workflowService = workflowService;
    }

    protected BannersResource getBannersRoot(Context context, CoralSession coralSession)
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
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        try
        {
            return coralSession.getUserSubject().hasRole(getBannersRoot(context, coralSession).getAdministrator());
        }
        catch(ProcessingException e)
        {
            log.error("Subject has no rights to view this screen");
            return false;
        }
    }
}


