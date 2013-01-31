package net.cyklotron.cms.search.util;

import java.io.IOException;

import org.apache.commons.lang3.Validate;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.jcontainer.dna.Logger;

import net.cyklotron.cms.search.SearchConstants;
import net.cyklotron.cms.search.analysis.AnalyzerProvider;

public class IndexInitializerImpl
    implements IndexInitializer
{

    private final Logger logger;

    public IndexInitializerImpl(Logger logger)
    {
        this.logger = logger;
    }

    @Override
    public void initEmptyIndexAt(Directory directory)
        throws IOException
    {
        IndexWriterConfig conf = new IndexWriterConfig(SearchConstants.LUCENE_VERSION,
            AnalyzerProvider.DEFAULT_PROVIDER.getAnalyzer());
        IndexWriter indexWriter;
        indexWriter = new IndexWriter(directory, conf);
        indexWriter.close();
    }

    @Override
    public boolean indexExistsAt(Directory directory)
    {
        Validate.notNull(directory);
        return DirectoryReader.indexExists(directory);
    }

    @Override
    public DirectoryReader openIndex(Directory directory)
        throws IOException
    {
        Validate.notNull(directory);
        clearLock(directory);
        if(indexExistsAt(directory))
        {
            try
            {
                return DirectoryReader.open(directory);
            }
            catch(CorruptIndexException e)
            {
                logger.error("corrupt index detected, attempting to recover", e);
                CheckIndex checkIndex = new CheckIndex(directory);
                try
                {
                    checkIndex.checkIndex();
                    return DirectoryReader.open(directory);
                }
                catch(IOException ioe)
                {
                    logger.error("attempt to recover failed", ioe);
                    throw ioe;
                }
            }
        }
        else
        {
            initEmptyIndexAt(directory);
            return DirectoryReader.open(directory);
        }
    }

    private void clearLock(Directory directory)
        throws IOException
    {
        if(directory.fileExists("write.lock"))
        {
            directory.deleteFile("write.lock");
        }
    }

}
