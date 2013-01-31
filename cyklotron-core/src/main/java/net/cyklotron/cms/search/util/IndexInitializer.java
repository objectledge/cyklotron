package net.cyklotron.cms.search.util;

import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;

public interface IndexInitializer
{
    void initEmptyIndexAt(Directory directory)
        throws IOException;

    boolean indexExistsAt(Directory directory);

    DirectoryReader forceCreateOrOpenIndex(Directory directory)
        throws IOException;
}
