package net.cyklotron.cms.modules.views.poll;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.poll.PollConstants;
import net.cyklotron.cms.poll.PollException;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

/**
 * poll application base screen
 */
public abstract class BasePollScreen
    extends BaseCMSScreen
    implements PollConstants
{
    /** poll service */
    protected PollService pollService;

    public BasePollScreen(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        PollService pollService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.pollService = pollService;
    }

    public PollsResource getPollsRoot(CoralSession coralSession)
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
            return pollService.getPollsRoot(coralSession, site);
        }
        catch(PollException e)
        {
            throw new ProcessingException("failed to lookup polls root");
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return coralSession.getUserSubject().hasRole(getPollsRoot(coralSession).getAdministrator());
    }
}
