package net.cyklotron.cms.search.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.stempel.StempelStemmer;
import org.egothor.stemmer.Trie;
import org.objectledge.filesystem.FileSystem;

public class StempelStemmerFactoryImpl
    implements StempelStemmerFactory
{

    final private Trie stemmerTrie;

    private final static String DEFAULT_STEMMER_FILE_PATH = "org/apache/lucene/analysis/pl/"
        + PolishAnalyzer.DEFAULT_STEMMER_FILE;

    public StempelStemmerFactoryImpl(FileSystem fileSystem)
        throws IOException
    {
        this.stemmerTrie = StempelStemmer
            .load(fileSystem.getInputStream(DEFAULT_STEMMER_FILE_PATH));
    }

    @Override
    public Stemmer createStempelStemmer()
        throws IOException
    {
        final StempelStemmer stempelStemmer = new StempelStemmer(stemmerTrie);
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
