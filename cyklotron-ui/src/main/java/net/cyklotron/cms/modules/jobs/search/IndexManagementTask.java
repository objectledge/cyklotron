package net.cyklotron.cms.modules.jobs.search;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;

import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.IndexableResource;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;

/**
 * Performs added and modfied resources indexing and index optimisation on a given index.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IndexManagementTask.java,v 1.2 2005-02-10 17:46:14 rafal Exp $
 */
public class IndexManagementTask
{
    // instance variables --------------------------------------------------------------------------

    /** logging service */
    protected Logger log;

    /** The search service. */
    private SearchService searchService;

    /** The resource service. */
    private CoralSession coralSession;

    private IndexResource index;
    private Set modifiedResources;
    private Set addedResources;
    
    // initialization ------------------------------------------------------------------------------
    
    public IndexManagementTask(IndexResource index, Set modifiedResources, Set addedResources, 
        Logger log, SearchService searchService, CoralSession coralSession)
    {
        this.index = index;
        this.modifiedResources = modifiedResources;
        this.addedResources = addedResources;
        
        this.log = log;
        this.searchService = searchService;
        this.coralSession = coralSession;
    }

    // Job interface -------------------------------------------------------------------------------
    
    /**
     * Terminates the job asynchronously.
     *
     * <p>Not supported.</p>
     *
     * @param thread the Thread executing the job.
     */
    public void terminate(Thread thread)
    {
        // not supported
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        int result = LEFT_UNMODIFIED;
        try
        {
            result = reindexOrOptimiseIndex(index);
        }
        catch(SearchException e)
        {
            log.error("IndexManagementTask: problem while reindexing/optimising index", e);
        }

        // newly reindexed indexes do not need to have the resources added
        if(result == REINDEXED)
        {
            return;
        }

        // delete modified resources
        if(modifiedResources == null)
        {
            modifiedResources = new HashSet();
        }        
        if(modifiedResources.size() > 0)
        {
            IndexableResource[] iRes = (IndexableResource[]) modifiedResources.toArray(
                new IndexableResource[modifiedResources.size()]);
            try
            {
                searchService.getIndexingFacility().deleteFromIndex(index, iRes);
            }
            catch(SearchException e)
            {
                log.error("IndexManagementTask: problem while removing resources from index", e);
            }
        }        
        
        // index both added and modified resources
        if(addedResources == null)
        {
            addedResources = new HashSet();
        }
        modifiedResources.addAll(addedResources); // combine resource sets
        if(modifiedResources.size() > 0)
        {
            IndexableResource[] iRes = (IndexableResource[]) modifiedResources.toArray(
                    new IndexableResource[modifiedResources.size()]);
            try
            {
                searchService.getIndexingFacility().addToIndex(coralSession, index, iRes);
            }
            catch(SearchException e)
            {
                log.error("IndexManagementTask: problem while adding resources to index", e);
            }
        }
    }

    // index management ----------------------------------------------------------------------------

    private final int LEFT_UNMODIFIED = 0;
    private final int REINDEXED = 1;
    private final int OPTIMISED = 2;
    
    private int reindexOrOptimiseIndex(IndexResource index)
        throws SearchException
    {
        Directory luceneDirectory = searchService.getIndexingFacility().getIndexDirectory(index);

        boolean reindex = false;
        int numDocs = 0;
        long numChanges = 0L;
        IndexReader reader = null;
        try
        {
            reindex = !IndexReader.indexExists(luceneDirectory);
            if(!reindex)
            {
                numChanges = IndexReader.getCurrentVersion(luceneDirectory);
                reader = IndexReader.open(luceneDirectory, false);
                numDocs = reader.numDocs();
                reindex = (numDocs == 0);
            }
        }
        catch(IOException e)
        {
            log.warn("IndexManagementTask: Cannot get information for index '" +
                index.getPath() + "' index is probably broken - reindexing it", e);
            // index is probably broken - reindex it
            reindex = true;
        }
        finally
        {
            if(reader != null)
            {
                try
                {
                    reader.close();
                }
                catch(IOException e)
                {
                    log.error("IndexManagementTask: Cannot close IndexReader for index '"
                        + index.getPath() + "'", e);
                }
            }
        }

        if(reindex)
        {
            searchService.getIndexingFacility().reindex(coralSession, index);
            return REINDEXED;
        }
        else if((numChanges * 100L) / numDocs > 30)
        {
            // if num of changes > 30% of index size, perform optimisation
            searchService.getIndexingFacility().optimize(index);
            return OPTIMISED;
        }
        return LEFT_UNMODIFIED;
    }
}
