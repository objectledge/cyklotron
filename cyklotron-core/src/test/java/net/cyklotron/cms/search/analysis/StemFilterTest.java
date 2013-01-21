package net.cyklotron.cms.search.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StemFilterTest
{
    // source of words: news @ wp.pl
    // text has been down cased by hand
    private final static String textToStem = "to uczyniło go prezydentem usa zdjęcia"
        + "poród i śmierć na plebanii arcybiskup pisze do papieża"
        + "politycy mogą mieć wyborców w nosie, ale do czasu"
        + "decyzja zapadła rosja wysyła samoloty w rejon wojny"
        + "pis chce przełożenia debaty posłowie idą na pogrzeb"
        + "złowrogie prognozy nikt nie może czuć się bezpieczny" + "michael winner nie żyje"
        + "czy mors-zabójca istniał naprawdę Najnowsze badania"
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
    {

    }

}
