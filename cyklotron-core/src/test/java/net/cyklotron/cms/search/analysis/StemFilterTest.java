package net.cyklotron.cms.search.analysis;

import static org.fest.assertions.Assertions.assertThat;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StemFilterTest
{
    // source of words: news @ wp.pl
    // text has been down cased by hand
    private final static String textToStem = "to uczyniło go prezydentem usa zdjęcia"
        + "poród i śmierć na plebanii arcybiskup pisze do papieża"
        + "politycy mogą mieć wyborców w nosie ale do czasu"
        + "decyzja zapadła rosja wysyła samoloty w rejon wojny"
        + "pis chce przełożenia debaty posłowie idą na pogrzeb"
        + "złowrogie prognozy nikt nie może czuć się bezpieczny" + "michael winner nie żyje"
        + "czy mors zabójca istniał naprawdę ajnowsze badania"
        + "burda na warszawskiej pradze kobiety zatrzymane" + "kaczyńscy w końcu razem";

    Collection<String> originalWords;

    Collection<String> stemmedWords;

    List<String> expectedStreamOfWords;

    @BeforeMethod
    public void generateWordsAndStems()
    {
        String[] words = textToStem.split(" ");
        Stemmer stemmer = new StemmerPL();
        originalWords = Arrays.asList(words);
        expectedStreamOfWords = new ArrayList<>();
        stemmedWords = new ArrayList<>();
        for(String original : words)
        {
            String stem = stemmer.stem(original);
            stemmedWords.add(stem);
            expectedStreamOfWords.add(original);
            expectedStreamOfWords.add(stem);
        }
    }

    @Test
    public void should_add_stems_to_original_stream_of_tokens()
        throws Exception
    {
        // given
        final String stopwords = "";
        final StringReader stopWordsReader = new StringReader(stopwords);

        TextAnalyzer analyzer = new TextAnalyzer(Version.LUCENE_40, stopWordsReader,
            new StemmerPL());
        TokenStream stream = analyzer.tokenStream("field", new StringReader(textToStem));

        // get the CharTermAttribute from the TokenStream
        CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);

        // when
        List<String> stemmedResults = new ArrayList<>();
        try
        {
            stream.reset();
            while(stream.incrementToken())
            {
                stemmedResults.add(termAtt.toString());
            }
            stream.end();
        }
        finally
        {
            stream.close();
            analyzer.close();
        }

        // then
        // assertThat(stemmedResults).contains(originalWords).contains(stemmedWords);
        assertThat(stemmedResults).isEqualTo(expectedStreamOfWords);
    }

}
