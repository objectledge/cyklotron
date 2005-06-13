package net.cyklotron.cms.modules.jobs.search;

import java.util.Date;

import net.cyklotron.cms.CmsNodeResourceImpl;
import net.cyklotron.cms.search.SearchService;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;

/**
 * Manages ManageIndexes timestamping
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ManageIndexesTimestamp.java,v 1.4 2005-06-13 11:08:31 rafal Exp $
 */
public class ManageIndexesTimestamp
{
    private static String INDEXING_TIMESTAMP_RES_NAME = "indexing_timestamp";
    
    // instance variables --------------------------------------------------------------------------

    /** logging facility */
    protected Logger log;

    /** The search service. */
    private SearchService searchService;

    /** The resource service. */
    private CoralSession coralSession;

    public ManageIndexesTimestamp(Logger log, SearchService searchService,
        CoralSession coralSession)
    {
        this.log = log;
        this.searchService = searchService;
        this.coralSession = coralSession;
        //SchedulerService schedulerService = null;
        
        /*ScheduledJob job = schedulerService. getJob("name");
        job.*/
    }
    
    // local ---------------------------------------------------------------------------------------

    public static final Date ZERO_DATE = new Date(0L);

    private Resource timestampResource;
    
    // public interface ----------------------------------------------------------------------------

    /**
     * Get the time stamp.
     */
    public Date getTimeStamp()
    {
        this.timestampResource = getTimestampResource(coralSession);
        Date ts = null;
        if(timestampResource == null)
        {
            ts =  ZERO_DATE;
            createTimestampResource(coralSession);
        }
        else
        {
            ts = timestampResource.getModificationTime();
            timestampResource.update();
        }
        return ts;
    }

    // implementation ------------------------------------------------------------------------------
    
    private Resource getTimestampResource(CoralSession coralSession)
    {
        Resource searchGlobalRoot = searchService.getXRefsResource(coralSession);
        Resource[] res = 
            coralSession.getStore().getResource(searchGlobalRoot, INDEXING_TIMESTAMP_RES_NAME);
        if(res.length == 0)
        {
            return null;
        }
        else if(res.length > 1)
        {
            log.error("ManageIndexes: multiple "+INDEXING_TIMESTAMP_RES_NAME
                +" resources, deleting them but leaving the lately modified");
            // get lately modified
            Resource latelyModified = res[0];
            for (int i = 1; i < res.length; i++)
            {
                if(res[i].getModificationTime().before(latelyModified.getModificationTime()))
                {
                    latelyModified = res[i];
                }
            }
            // delete other
            for (int i = 0; i < res.length; i++)
            {
                if(!res[i].equals(latelyModified))
                {
                    try
                    {
                        coralSession.getStore().deleteResource(res[i]);
                    }
                    catch(EntityInUseException e)
                    {
                        log.error("ManageIndexes: error deleting one of "+
                            INDEXING_TIMESTAMP_RES_NAME+" resources", e);
                    }
                }
            }
            // return the latest
            return latelyModified;
        }
        else
        {
            return res[0];
        }
    }

    private void createTimestampResource(CoralSession coralSession)
    {
        Resource searchGlobalRoot = searchService.getXRefsResource(coralSession);
        Resource[] res = 
            coralSession.getStore().getResource(searchGlobalRoot, INDEXING_TIMESTAMP_RES_NAME);
        if(res.length == 0)
        {
            try
            {
                Resource timestamp = CmsNodeResourceImpl.createCmsNodeResource(coralSession,
                    INDEXING_TIMESTAMP_RES_NAME, searchGlobalRoot);
            }
            catch(InvalidResourceNameException e)
            {
                log.error("unexpected exception", e);
            }
        }
    }
}
