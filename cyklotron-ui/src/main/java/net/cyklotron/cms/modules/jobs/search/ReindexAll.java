package net.cyklotron.cms.modules.jobs.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.scheduler.Job;

import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.IndexingFacility;

/**
 * Reindexes all lucene indexes in cyklotron
 * 
 * @author Marek Lewandowski
 */
public class ReindexAll
    extends Job
{

    private Logger logger;

    private CoralSessionFactory coralSessionFactory;

    private IndexingFacility indexingFacility;

    public ReindexAll(CoralSessionFactory coralSessionFactory, Logger logger,
        IndexingFacility indexingFacility)
    {
        this.logger = logger;
        this.coralSessionFactory = coralSessionFactory;
        this.indexingFacility = indexingFacility;
    }

    @Override
    public void run(String[] arguments)
    {
        try(CoralSession session = coralSessionFactory.getRootSession())
        {
            logger.info("ReindexAll job has started at " + new Date().toString());
            QueryResults results = session.getQuery().executeQuery(
                "FIND RESOURCE FROM " + IndexResource.CLASS_NAME);
            Resource[] resources = results.getArray(1);
            // Resource[] resources = session.getStore().getResource(IndexResource.CLASS_NAME);
            logger.info("preparing to reindex " + resources.length + " indexes");

            Collection<IndexResource> indexes = new ArrayList<>();
            for(Resource resource : resources)
            {
                indexes.add((IndexResource)resource);
            }
            Collection<IndexResource> failed = new ArrayList<>();
            for(IndexResource index : indexes)
            {
                try
                {
                    indexingFacility.reindex(session, index);
                }
                catch(Exception e)
                {
                    logger.error("Reindexing of resource: " + index.toString() + " has failed", e);
                    failed.add(index);
                }
            }
            if(failed.size() > 0)
            {
                StringBuilder builder = new StringBuilder();
                for(IndexResource ir : failed)
                {
                    builder.append(ir.toString());
                    builder.append("\n");
                }
                logger.error("Failed reindexing " + failed.size()
                    + " indexes and they were as follows: " + builder.toString());
                logger.info("ReindexAll job has ended with errors at " + new Date().toString());
            }
            else
            {
                logger.info("ReindexAll job has ended successfully with no errors at "
                    + new Date().toString());
            }
        }
        catch(MalformedQueryException e1)
        {
            throw new RuntimeException("Query syntax is wrong, Fix it", e1);
        }
    }
}
