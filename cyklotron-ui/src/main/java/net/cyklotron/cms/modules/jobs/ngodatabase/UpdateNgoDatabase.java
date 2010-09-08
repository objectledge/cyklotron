package net.cyklotron.cms.modules.jobs.ngodatabase;

import org.objectledge.scheduler.Job;
import net.cyklotron.cms.ngodatabase.NgoDatabaseService;

/**
 * A job that download and update Polish NGO database.
 *
 */
public class UpdateNgoDatabase
    extends Job
{
    // instance variables ////////////////////////////////////////////////////

    /** The NGO database service. */
    private NgoDatabaseService ngoDatabaseService;

    // initialization ///////////////////////////////////////////////////////
    
    /**
     *  Constructor
     */
    public UpdateNgoDatabase(NgoDatabaseService ngoDatabaseService)
    {
        this.ngoDatabaseService = ngoDatabaseService;
    }

    // Job interface ////////////////////////////////////////////////////////
    
    /**
     * Performs the mainteance.
     */
    public void run(String[] arguments)
    {
        ngoDatabaseService.downloadDataSource();
        ngoDatabaseService.update();
    }
}
