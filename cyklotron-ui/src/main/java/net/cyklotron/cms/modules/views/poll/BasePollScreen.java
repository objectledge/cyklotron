package net.cyklotron.cms.modules.views.poll;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.poll.PollConstants;
import net.cyklotron.cms.poll.PollException;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

import org.objectledge.pipeline.ProcessingException;

/**
 * poll application base screen
 */
public class BasePollScreen
    extends BaseCMSScreen
    implements PollConstants, Secure
{
    /** logging facility */
    protected Logger log;

    /** poll service */
    protected PollService pollService;

    /** preferences service */
    protected PreferencesService preferencesService;

    public BasePollScreen()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("poll");
        pollService = (PollService)broker.getService(PollService.SERVICE_NAME);
        preferencesService = (PreferencesService)broker.getService(PreferencesService.SERVICE_NAME);
    }

    public PollsResource getPollsRoot(RunData data)
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
            return pollService.getPollsRoot(site);
        }
        catch(PollException e)
        {
            throw new ProcessingException("failed to lookup polls root");
        }
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return coralSession.getUserSubject().hasRole(getPollsRoot(data).getAdministrator());
    }
}
