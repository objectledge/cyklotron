package net.cyklotron.cms.modules.jobs.ngodatabase;

import java.io.IOException;

import org.objectledge.scheduler.Job;

import net.cyklotron.cms.ngodatabase.LocationDatabaseService;
import net.cyklotron.cms.ngodatabase.NgoDatabaseService;

/**
 * A job that download and update Location database.
 */
public class UpdateLocationDatabase
    extends Job
{

    /** The Location database service. */
    private LocationDatabaseService locationDatabaseService;

    /**
     * Constructor
     */
    public UpdateLocationDatabase(LocationDatabaseService locationDatabaseService)
    {
        this.locationDatabaseService = locationDatabaseService;
    }

    // Job interface ////////////////////////////////////////////////////////

    /**
     * Performs the mainteance.
     */
    public void run(String[] arguments)
    {
        locationDatabaseService.update();
    }
}
