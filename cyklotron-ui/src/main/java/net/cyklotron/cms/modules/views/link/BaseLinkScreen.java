package net.cyklotron.cms.modules.views.link;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.link.LinkConstants;
import net.cyklotron.cms.link.LinkException;
import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;

import org.objectledge.pipeline.ProcessingException;

/**
 * link application base screen
 * @version $Id: BaseLinkScreen.java,v 1.2 2005-01-24 10:27:19 pablo Exp $
 */
public class BaseLinkScreen extends BaseCMSScreen implements LinkConstants
{
    /** logging facility */
    protected Logger log;

    /** link service */
    protected LinkService linkService;

    /** preferences service */
    protected PreferencesService preferencesService;

    /** structure service */
    StructureService structureService;

    public BaseLinkScreen()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(LinkService.LOGGING_FACILITY);
        linkService = (LinkService)broker.getService(LinkService.SERVICE_NAME);
        preferencesService = (PreferencesService)broker.getService(PreferencesService.SERVICE_NAME);
        structureService = (StructureService)broker.getService(StructureService.SERVICE_NAME);
    }

    public LinkRootResource getLinkRoot(RunData data)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        SiteResource site = cmsData.getSite();
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
            return linkService.getLinkRoot(site);
        }
        catch(LinkException e)
        {
            throw new ProcessingException("failed to lookup links root");
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        try
        {
            return coralSession.getUserSubject().hasRole(getLinkRoot(data).getAdministrator());
        }
        catch(ProcessingException e)
        {
            log.error("Subject has no rights to view this screen");
            return false;
        }
    }
}
