package net.cyklotron.cms.modules.jobs.banner;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.scheduler.Job;

import net.cyklotron.cms.banner.BannerService;

/**
 * A job that checks the start and expire date of the banner
 *
 */
public class CheckBannerState
    extends Job
{
    // instance variables ////////////////////////////////////////////////////

    /** The banner service. */
    private BannerService bannerService;

    private CoralSessionFactory sessionFactory;

    // initialization ///////////////////////////////////////////////////////
    
    /**
     *
     */
    public CheckBannerState(CoralSessionFactory sessionFactory, BannerService bannerService)
    {
        this.bannerService = bannerService;
        this.sessionFactory = sessionFactory;
    }

    // Job interface ////////////////////////////////////////////////////////
    
    /**
     * Performs the mainteance.
     */
    public void run(String[] arguments)
    {
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            bannerService.checkBannerState(coralSession);
        }
        finally
        {
            coralSession.close();
        }
    }
}
