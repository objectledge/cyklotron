package net.cyklotron.cms.search.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.stempel.StempelStemmer;
import org.objectledge.filesystem.FileSystem;

public class StempelStemmerFactoryImpl
    implements StempelStemmerFactory
{

    final private FileSystem fileSystem;

    private final static String DEFAULT_STEMMER_FILE_PATH = "org/apache/lucene/analysis/pl/"
        + PolishAnalyzer.DEFAULT_STEMMER_FILE;

    public StempelStemmerFactoryImpl(FileSystem fileSystem)
    {
        this.fileSystem = fileSystem;
    }

    @Override
    public Stemmer createStempelStemmer()
        throws IOException
    {
        final StempelStemmer stempelStemmer = new StempelStemmer(
            fileSystem.getInputStream(DEFAULT_STEMMER_FILE_PATH));
        return new Stemmer()
            {
                @Override
                public CharSequence stem(CharSequence term)
                {
                    return stempelStemmer.stem(term);
                }
            };
    }
}
