package net.cyklotron.cms.search.analysis;

import java.io.IOException;

public interface StempelStemmerFactory
{
    /**
     * Returns Stempel stemmer with default Stempel words table
     * 
     * @return Stemmer the Stempel stemmer
     * @throws IOException when default words table cannot be opened
     */
    Stemmer createStempelStemmer()
        throws IOException;
}
