/*
 * Created on Oct 23, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.jobs.periodicals;

import java.util.Date;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.scheduler.Job;

import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsService;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ProcessPeriodicals 
    extends Job
{
    /** The link service. */
    private PeriodicalsService periodicalsService;

    private CoralSessionFactory sessionFactory;
    
    private Logger logger;
    
    /**
     *
     */
    public ProcessPeriodicals(CoralSessionFactory sessionFactory, PeriodicalsService periodicalsService
        , Logger logger)
    {
        this.periodicalsService = periodicalsService;
        this.sessionFactory = sessionFactory;
        this.logger = logger;
    }    
    /**
     * Performs the mainteance.
     */
    public void run(String[] args)
    {
        CoralSession coralSession = sessionFactory.getRootSession();
        Date now = new Date();
        try
        {
            periodicalsService.processPeriodicals(coralSession, now);
        }
        catch(PeriodicalsException e)
        {
            logger.error("failed to process periodicals", e);
        }
        finally
        {
            coralSession.close();
        }
    }
}
