package net.cyklotron.cms.ngodatabase;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.LocalFileSystemProvider;

public abstract class AbstractIndex<T>
{
    protected static final StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

    private final IndexWriter writer;

    protected final IndexSearcher searcher;

    protected final Logger logger;

    private Thread updateThread = null;

    public AbstractIndex(FileSystem fileSystem, Logger logger, String indexPath)
        throws IOException
    {
        File indexLocation = ((LocalFileSystemProvider)fileSystem.getProvider("local"))
            .getFile(indexPath);
        Directory directory = new NIOFSDirectory(indexLocation);
        // remove stale write lock if one exists
        if(directory.fileExists("write.lock"))
        {
            directory.deleteFile("write.lock");
        }
        IndexWriter writer = null;
        try
        {
            writer = new IndexWriter(directory, analyzer, IndexWriter.MaxFieldLength.LIMITED);
        }
        catch(CorruptIndexException e)
        {
            logger.error("corrupt index detected, attempting to recover", e);
            CheckIndex checkIndex = new CheckIndex(directory);
            checkIndex.checkIndex();
            // try to reopen index
            writer = new IndexWriter(directory, analyzer, IndexWriter.MaxFieldLength.LIMITED);
        }
        this.writer = writer;
        this.searcher = new IndexSearcher(writer.getReader());
        this.logger = logger;
    }

    protected abstract Document toDocument(T item);
    
    protected abstract T fromDocument(Document doc);

    public synchronized void startUpdate()
        throws IOException
    {
        if(updateThread != null)
        {
            throw new IllegalStateException("update in progress");
        }
        updateThread = Thread.currentThread();
        writer.deleteAll();
    }

    public synchronized void addItem(T item)
        throws IOException
    {
        if(updateThread != Thread.currentThread())
        {
            throw new IllegalStateException("update in progress");
        }
        writer.addDocument(toDocument(item));
    }

    public synchronized void endUpdate()
        throws CorruptIndexException, IOException
    {
        if(updateThread != Thread.currentThread())
        {
            throw new IllegalStateException("update in progress");
        }
        writer.optimize();
        writer.commit();
        updateThread = null;
    }
}
