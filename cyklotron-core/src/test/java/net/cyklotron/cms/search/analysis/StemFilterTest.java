package net.cyklotron.cms.search.analysis;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.stempel.StempelStemmer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.fest.assertions.Fail;

import net.cyklotron.cms.search.SearchConstants;

public class StemFilterTest
    extends TestCase
{
    // source of words: news @ wp.pl
    // text has been down cased by hand
    private final static String ORIGINAL_TEXT = "to uczyniło go prezydentem usa zdjęcia"
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

    StempelStemmerFactory factory;

    @Override
    protected void setUp()
        throws Exception
    {
        factory = mock(StempelStemmerFactory.class);

        final StempelStemmer stempelStemmer = new StempelStemmer(
            PolishAnalyzer.class.getResourceAsStream(PolishAnalyzer.DEFAULT_STEMMER_FILE));
        when(factory.createStempelStemmer()).thenReturn(new Stemmer()
            {
                @Override
                public CharSequence stem(CharSequence term)
                {
                    return stempelStemmer.stem(term);
                }

            });

        String[] words = ORIGINAL_TEXT.split(" ");
        Stemmer stemmer = factory.createStempelStemmer();
        originalWords = Arrays.asList(words);
        expectedStreamOfWords = new ArrayList<>();
        stemmedWords = new ArrayList<>();
        for(String original : words)
        {
            // String stem =
            CharSequence stem = stemmer.stem(original);
            String stemStr = null;
            if(stem != null)
                stemStr = stem.toString();
            stemmedWords.add(stemStr);
            expectedStreamOfWords.add(original);
            expectedStreamOfWords.add(stemStr);
        }
    }

    public void testShouldAddStemsToOriginalStreamOfTokens()
        throws Exception
    {
        // given
        final String stopwords = "";
        final StringReader stopWordsReader = new StringReader(stopwords);

        TextAnalyzer analyzer = new TextAnalyzer(SearchConstants.LUCENE_VERSION, stopWordsReader,
            factory.createStempelStemmer());
        TokenStream stream = analyzer.tokenStream("field", new StringReader(ORIGINAL_TEXT));

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

    public void testShouldNotFaiWhenWritingIntoIndex()
        throws Exception
    {
        // given
        Object[][] documents = getDocuments();
        Document document = (Document) documents[0][0];
        Map<String, Collection<String>> fieldToTerms = (Map<String, Collection<String>>)documents[0][1];
        
        RAMDirectory directory = new RAMDirectory();
        String stopwords = "";
        final StringReader stopWordsReader = new StringReader(stopwords);

        Analyzer analyzer = createPerFieldAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(SearchConstants.LUCENE_VERSION, analyzer);
        IndexWriter writer = new IndexWriter(directory, indexWriterConfig);
        
        // when
        try
        {
            writer.addDocument(document);
        }
        catch(IOException ex)
        {
            Fail.fail("should not throw errors", ex);
        }
        finally
        {
            writer.close();
        }
        // then
        DirectoryReader reader = DirectoryReader.open(directory);
        Fields fields = MultiFields.getFields(reader);
        Iterator<String> iterator = fields.iterator();
        String field;
        while(iterator.hasNext())
        {
            field = iterator.next();
            Terms terms = fields.terms(field);
            TermsEnum termsEnum = terms.iterator(null);
            BytesRef term;
            while((term = termsEnum.next()) != null)
            {
                String termStr = term.utf8ToString();
                assertThat(termStr).isIn(fieldToTerms.get(field));
            }
        }
    }


    private Analyzer createPerFieldAnalyzer()
        throws IOException
    {

        Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
        analyzerPerField.put(SearchConstants.FIELD_CATEGORY, new NewlineSeparatedAnalyzer());
        Stemmer stemmer = factory.createStempelStemmer();
        analyzerPerField.put(SearchConstants.FIELD_INDEX_ABBREVIATION, new TextAnalyzer(
            SearchConstants.LUCENE_VERSION, getStopwordsReader(), stemmer));
        analyzerPerField.put(SearchConstants.FIELD_INDEX_TITLE, new TextAnalyzer(SearchConstants.LUCENE_VERSION,
            getStopwordsReader(), stemmer));
        analyzerPerField.put(SearchConstants.FIELD_INDEX_CONTENT, new TextAnalyzer(
            SearchConstants.LUCENE_VERSION, getStopwordsReader(), stemmer));
        return new PerFieldAnalyzerWrapper(new TextAnalyzer(SearchConstants.LUCENE_VERSION), analyzerPerField);
    }

    private Reader getStopwordsReader()
    {
        return new StringReader("");
    }

    private Object[][] getDocuments()
        throws IOException
    {
        Document dt1 = new Document();
        Map<String, Collection<String>> dt1fieldToTerms = new HashMap<>();
        String dt1TitleStr = "przykładowy tytuł jakiegoś dokumentu";
        Collection<String> dt1stemmedTitle = getStemmed(dt1TitleStr);
        dt1fieldToTerms.put(SearchConstants.FIELD_INDEX_TITLE, dt1stemmedTitle);
        TextField dt1Title = new TextField(SearchConstants.FIELD_INDEX_TITLE, dt1TitleStr,
            Store.YES);
        String defaultText = "ja chcieć tralala kiełbasa mmm lubie to";
        TextField dtDefault = new TextField("defaultFieldName",
 defaultText, Store.NO);
        Collection<String> dt1stemmedContent = getStemmed(ORIGINAL_TEXT);
        dt1fieldToTerms.put(SearchConstants.FIELD_INDEX_CONTENT, dt1stemmedContent);
        TextField dt1Content = new TextField(SearchConstants.FIELD_INDEX_CONTENT, ORIGINAL_TEXT,
            Store.YES);
        dt1.add(dt1Title);
        dt1.add(dtDefault);
        dt1.add(dt1Content);

        dt1fieldToTerms.put("defaultFieldName", Arrays.asList(defaultText.split(" ")));

        return new Object[][] { { dt1, dt1fieldToTerms } };
    }

    private Collection<String> getStemmed(String content)
        throws IOException
    {
        Stemmer stemmer = factory.createStempelStemmer();
        String[] words = content.split(" ");
        Collection<String> stemmed = new ArrayList<>();
        for(String word : words)
        {
            stemmed.add(word);
            CharSequence stem = stemmer.stem(word);
            String stemStr = null;
            if(stem != null)
            {
                stemStr = stem.toString();
            }
            stemmed.add(stemStr);
        }
        return stemmed;
    }
}
