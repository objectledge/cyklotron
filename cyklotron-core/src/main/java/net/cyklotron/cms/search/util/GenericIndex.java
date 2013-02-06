package net.cyklotron.cms.search.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.store.Resource;
import org.objectledge.filesystem.FileSystem;

import net.cyklotron.cms.search.SearchConstants;
import net.cyklotron.cms.search.analysis.AnalyzerProvider;

/**
 * GenericIndex is wrapper around Lucene 4.0 index readers and writers which provide useful methods
 * like add, update, addAll, updateAll resources. These operations are transactional. To use
 * GenericIndex you should implement FromDocumentMapper, ToDocumentMapper contracts and create
 * instance of ResourceProvider. To create GenericIndex one should use GenericIndexFactory.
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

    private IndexWriter writer;

    private final FromDocumentMapper<T> fromDocumentMapper;

    private final ToDocumentMapper<T> toDocumentMapper;

    private final ResourceProvider<T> resourceProvider;

    private SearcherManager searcherManager;

    GenericIndex(FileSystem fileSystem, Logger logger, String indexPath,
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
        this.searcherManager = new SearcherManager(writer, false, null);
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
    public synchronized void add(T resource)
        throws IOException
    {
        addAll(Arrays.asList(resource));
    }

    public synchronized void update(T resource)
        throws IOException
    {
        Term identifier = toDocumentMapper.getIdentifier(resource);
        Document document = toDocumentMapper.toDocument(resource);
        writer.prepareCommit();
        try
        {
            writer.updateDocument(identifier, document);
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
    public synchronized void addAll(Collection<T> resources)
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
    public synchronized void updateAll(Collection<T> resources)
        throws IOException
    {
        writer.prepareCommit();
        for(T resource : resources)
        {
            try
            {
                writer.updateDocument(toDocumentMapper.getIdentifier(resource),
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
                Document document = toDocumentMapper.toDocument(resource);
                if(document != null)
                {
                    writer.addDocument(document);
                }
            }
            writer.commit();
        }
        catch(IOException e)
        {
            writer.rollback();
        }
    }

    Collection<String> getAllFieldsNames()
        throws IOException
    {
        Fields fields = MultiFields.getFields(reader);
        if(fields == null)
        {
            return Collections.emptyList();
        }
        Set<String> fieldNames = new HashSet<>();
        for(String fieldName : fields)
        {
            fieldNames.add(fieldName);
        }
        return fieldNames;
    }

    /**
     * Reindexed all resources. Operation can be cancelled by callback which receives progress
     * updates
     * 
     * @param resources
     * @return
     * @throws IOException
     */
    public synchronized void reindexAllCancellable(Cancellable callback)
        throws IOException
    {
        writer.prepareCommit();
        try
        {
            writer.deleteAll();
            for(T resource : resourceProvider)
            {
                Document document = toDocumentMapper.toDocument(resource);
                if(document != null)
                {
                    writer.addDocument(document);
                }
                if(callback.isCancelled())
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
            Document document = toDocumentMapper.toDocument(resource);
            if(document != null)
            {
                documents.add(document);
            }
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
