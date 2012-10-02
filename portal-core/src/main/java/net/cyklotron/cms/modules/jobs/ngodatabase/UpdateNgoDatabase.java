package net.cyklotron.cms.modules.jobs.ngodatabase;

import org.jcontainer.dna.Logger;
import org.objectledge.scheduler.Job;

import net.cyklotron.cms.ngodatabase.NgoDatabaseService;

/**
 * A job that download and update Polish NGO database.
 */
public class UpdateNgoDatabase
    extends Job
{

    /** The NGO database service. */
    private NgoDatabaseService ngoDatabaseService;

    private final Logger logger;

    /**
     * Constructor
     */
    public UpdateNgoDatabase(NgoDatabaseService ngoDatabaseService, Logger logger)
    {
        this.ngoDatabaseService = ngoDatabaseService;
        this.logger = logger;
    }

    // Job interface ////////////////////////////////////////////////////////

    /**
     * Performs the mainteance.
     */
    public void run(String[] arguments)
    {
        if(arguments.length == 1)
        {
            if(arguments[0].contains("incoming"))
            {
                logger.info("loading data from ngo database");
                ngoDatabaseService.updateIncoming();
                logger.info("done loading data from ngo database");
            }
            if(arguments[0].contains("outgoing"))
            {
                logger.info("exporting data for ngo database");
                ngoDatabaseService.updateOutgoing();
                logger.info("done exporting data for ngo database");
            }
        }
    }
}
