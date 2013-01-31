package net.cyklotron.cms.search.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.store.Resource;
import org.objectledge.filesystem.FileSystem;

import net.cyklotron.cms.search.SearchConstants;
import net.cyklotron.cms.search.analysis.AnalyzerProvider;

/**
 * GenericIndex is wrapper around Lucene 4.0 index readers and writers which provide useful methods
 * like addResource, updateResource. These operations are transactional. To use GenericIndex you
 * should implement FromDocumentMapper, ToDocumentMapper contracts and create instance of
 * ResourceProvider. To create GenericIndex one should use GenericIndexFactory.
 * 
 * @see FromDocumentMapper
 * @see ToDocumentMapper
 * @see ResourceProvider
 * @see GenericIndexFactory
 * @see AnalyzerProvider
 * @author Marek Lewandowski
 * @param <T> concrete resource type
 */
public class GenericIndex<T extends Resource>
    implements Closeable
{

    private final Logger logger;

    private final Directory directory;

    private Analyzer analyzer;

    private IndexReader reader;

    private IndexSearcher searcher;

    private IndexWriter writer;

    private final FromDocumentMapper<T> fromDocumentMapper;

    private final ToDocumentMapper<T> toDocumentMapper;

    private final ResourceProvider<T> resourceProvider;

    public GenericIndex(FileSystem fileSystem, Logger logger, String indexPath,
        AnalyzerProvider analyzerProvider, FromDocumentMapper<T> fromDocumentMapper,
 ToDocumentMapper<T> toDocumentMapper,
        ResourceProvider<T> resourceProvider, Directory directory)
        throws IOException
    {
        this.logger = logger;
        this.directory = directory;
        this.resourceProvider = resourceProvider;
        this.analyzer = analyzerProvider.getAnalyzer();
        this.reader = DirectoryReader.open(directory);
        this.writer = getWriter();
        this.toDocumentMapper = toDocumentMapper;
        this.fromDocumentMapper = fromDocumentMapper;
        searcher = new IndexSearcher(reader);
    }

    protected IndexWriter getWriter()
        throws IOException
    {
        IndexWriterConfig conf = new IndexWriterConfig(SearchConstants.LUCENE_VERSION, analyzer);
        return new IndexWriter(directory, conf);
    }

    /**
     * Add new resource to index
     * 
     * @param resource
     * @throws IOException
     */
    public synchronized void addResource(T resource)
        throws IOException
    {
        addResourcesInBatch(Arrays.asList(resource));
    }

    public synchronized void updateResource(T resource)
        throws IOException
    {
        Term uniqueTerm = toDocumentMapper.getUniqueTerm(resource);
        Document document = toDocumentMapper.toDocument(resource);
        writer.prepareCommit();
        try
        {
            writer.updateDocument(uniqueTerm, document);
            writer.commit();
        }
        catch(IOException e)
        {
            logger.error("Failed to update resource " + resource, e);
            writer.rollback();
        }
    }

    /**
     * Adds new resources to index.
     * 
     * @param resources
     * @throws IOException
     */
    public synchronized void addResourcesInBatch(Collection<T> resources)
        throws IOException
    {
        writer.prepareCommit();
        Collection<Document> documents = getDocuments(resources);
        try
        {
            writer.addDocuments(documents);
            writer.commit();
        }
        catch(IOException e)
        {
            logger.error("Failed to update resources " + resources, e);
            writer.rollback();
        }

    }

    /**
     * Removes only documents which should be updated
     * 
     * @param resources
     * @throws IOException
     */
    public synchronized void updateResourcesInBatch(Collection<T> resources)
        throws IOException
    {
        writer.prepareCommit();
        for(T resource : resources)
        {
            try
            {
                writer.updateDocument(toDocumentMapper.getUniqueTerm(resource),
                    toDocumentMapper.toDocument(resource));
                writer.commit();
            }
            catch(IOException e)
            {
                logger.error("Failed to update resources " + resources, e);
                writer.rollback();
            }
        }
    }
    
    /**
     * Reindexes all resources. Deletes all documents and adds them again
     * 
     * @param resources
     * @return
     * @throws IOException
     */
    public synchronized void reindexAll()
        throws IOException
    {
        writer.prepareCommit();
        try
        {
            writer.deleteAll();
            for(T resource : resourceProvider)
            {
                writer.addDocument(toDocumentMapper.toDocument(resource));
            }
            writer.commit();
        }
        catch(IOException e)
        {
            writer.rollback();
        }
    }

    /**
     * Reindexed all resources. Operation can be cancelled by callback which receives progress
     * updates
     * 
     * @param resources
     * @return
     * @throws IOException
     */

    public synchronized void reindexAllCancellable(ResourceProcessedCallback callback)
        throws IOException
    {
        writer.prepareCommit();
        try
        {
            writer.deleteAll();
            for(T resource : resourceProvider)
            {
                writer.addDocument(toDocumentMapper.toDocument(resource));
                if(!callback.shouldContinue())
                {
                    throw new IOException("Rollback");
                }
            }
            writer.commit();
        }
        catch(IOException e)
        {
            writer.rollback();
        }
    }

    private Collection<Document> getDocuments(Collection<T> resources)
    {
        Collection<Document> documents = new ArrayList<>();
        for(T resource : resources)
        {
            documents.add(toDocumentMapper.toDocument(resource));
        }
        return documents;
    }

    @Override
    public void close()
        throws IOException
    {
        if(writer != null)
        {
            writer.close();
        }
        if(reader != null)
        {
            reader.close();
        }
        directory.close();
    }
}
