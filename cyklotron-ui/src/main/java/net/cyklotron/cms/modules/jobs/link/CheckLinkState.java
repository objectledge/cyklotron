package net.cyklotron.cms.modules.jobs.link;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.scheduler.Job;

import net.cyklotron.cms.link.LinkService;

/**
 * A job that checks the start and expire date of the links
 *
 */
public class CheckLinkState
    extends Job
{
    // instance variables ////////////////////////////////////////////////////

    /** The link service. */
    private LinkService linkService;

    private CoralSessionFactory sessionFactory;
    
    /**
     *
     */
    public CheckLinkState(CoralSessionFactory sessionFactory, LinkService linkService)
    {
        this.linkService = linkService;
        this.sessionFactory = sessionFactory;
    }    
    /**
     * Performs the mainteance.
     */
    public void run(String[] args)
    {
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            linkService.checkLinkState(coralSession);
        }
        finally
        {
            coralSession.close();
        }
    }
}
