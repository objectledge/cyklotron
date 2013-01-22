package net.cyklotron.cms.search.analysis;

import static org.fest.assertions.Assertions.assertThat;

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

    @Override
    protected void setUp()
        throws Exception
    {
        String[] words = ORIGINAL_TEXT.split(" ");
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

    public void testShouldAddStemsToOriginalStreamOfTokens()
        throws Exception
    {
        // given
        final String stopwords = "";
        final StringReader stopWordsReader = new StringReader(stopwords);

        TextAnalyzer analyzer = new TextAnalyzer(Version.LUCENE_40, stopWordsReader,
            new StemmerPL());
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
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_40, analyzer);
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
        Stemmer stemmer = new StemmerPL();
        analyzerPerField.put(SearchConstants.FIELD_INDEX_ABBREVIATION, new TextAnalyzer(
            Version.LUCENE_40, getStopwordsReader(), stemmer));
        analyzerPerField.put(SearchConstants.FIELD_INDEX_TITLE, new TextAnalyzer(Version.LUCENE_40,
            getStopwordsReader(), stemmer));
        analyzerPerField.put(SearchConstants.FIELD_INDEX_CONTENT, new TextAnalyzer(
            Version.LUCENE_40, getStopwordsReader(), stemmer));
        return new PerFieldAnalyzerWrapper(new TextAnalyzer(Version.LUCENE_40), analyzerPerField);
    }

    private Reader getStopwordsReader()
    {
        return new StringReader("");
    }

    private Object[][] getDocuments()
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
    {
        Stemmer stemmer = new StemmerPL();
        String[] words = content.split(" ");
        Collection<String> stemmed = new ArrayList<>();
        for(String word : words)
        {
            stemmed.add(word);
            stemmed.add(stemmer.stem(word));
        }
        return stemmed;
    }
}
