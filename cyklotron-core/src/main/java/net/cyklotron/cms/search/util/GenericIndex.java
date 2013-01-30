package net.cyklotron.cms.search.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.store.Resource;
import org.objectledge.filesystem.FileSystem;

import net.cyklotron.cms.search.SearchConstants;
import net.cyklotron.cms.search.analysis.AnalyzerProvider;

public class GenericIndex<T extends Resource>
    implements Closeable
{

    private Logger logger;

    private Directory directory;

    private Analyzer analyzer;

    private IndexReader reader;

    private IndexSearcher searcher;


    public GenericIndex(FileSystem fileSystem, Logger logger, String indexPath,
        AnalyzerProvider analyzerProvider,
        IndexInitializator indexInitializator, FromDocumentMapper<T> fromDocumentMapper,
        ToDocumentMapper<T> toDocumentMapper, Directory directory)
        throws IOException
    {
        this.logger = logger;
        this.directory = directory;
        this.analyzer = analyzerProvider.getAnalyzer();
        this.reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
    }

    protected IndexWriter getWriter()
        throws IOException
    {
        IndexWriterConfig conf = new IndexWriterConfig(SearchConstants.LUCENE_VERSION, analyzer);
        return new IndexWriter(directory, conf);
    }

    public synchronized void addResource(T resource)
    {
        // TODO
    }

    public synchronized void updateResource(T resource)
    {
        // TODO
    }

    public synchronized void addResourcesInBatch(Collection<T> resoures)
    {
        // TODO
    }

    public synchronized void updateResourcesInBatch(Collection<T> resources)
    {
        // TODO
    }

    @Override
    public void close()
        throws IOException
    {
        // TODO
        // close searchers, writers, readers. Close directory
    }
}
