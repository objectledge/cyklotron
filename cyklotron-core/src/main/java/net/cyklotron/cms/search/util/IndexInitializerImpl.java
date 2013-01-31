package net.cyklotron.cms.search.util;

import java.io.IOException;

import org.apache.commons.lang3.Validate;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

import net.cyklotron.cms.search.SearchConstants;
import net.cyklotron.cms.search.analysis.AnalyzerProvider;

public class IndexInitializerImpl
    implements IndexInitializer
{

    @Override
    public void initEmptyIndexAt(Directory directory)
        throws IOException
    {
        IndexWriterConfig conf = new IndexWriterConfig(SearchConstants.LUCENE_VERSION,
            AnalyzerProvider.DEFAULT_PROVIDER.getAnalyzer());
        IndexWriter indexWriter;
        indexWriter = new IndexWriter(directory, conf);
        indexWriter.close();

        return;
    }

    @Override
    public boolean indexExistsAt(Directory directory)
    {
        Validate.notNull(directory);
        return DirectoryReader.indexExists(directory);
    }

    @Override
    public DirectoryReader forceCreateOrOpenIndex(Directory directory)
        throws IOException
    {
        Validate.notNull(directory);
        ensureNoLocks(directory);
        if(indexExistsAt(directory))
        {
            try
            {
                return DirectoryReader.open(directory);
            }
            catch(CorruptIndexException e)
            {
                CheckIndex checkIndex = new CheckIndex(directory);
                checkIndex.checkIndex();
                // try to reopen index
                return DirectoryReader.open(directory);
            }
        }
        else
        {
            initEmptyIndexAt(directory);
            return DirectoryReader.open(directory);
        }
    }

    private void ensureNoLocks(Directory directory)
        throws IOException
    {
        if(directory.fileExists("write.lock"))
        {
            directory.deleteFile("write.lock");
        }
    }

}
