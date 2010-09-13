package net.cyklotron.cms.modules.jobs.ngodatabase;

import java.io.IOException;

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
        try
        {
            ngoDatabaseService.downloadSource();
            ngoDatabaseService.update();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

    }
}
