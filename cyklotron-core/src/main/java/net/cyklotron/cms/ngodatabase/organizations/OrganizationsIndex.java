// 
// Copyright (c) 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
// 
// * Redistributions of source code must retain the above copyright notice,  
//       this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//       this list of conditions and the following disclaimer in the documentation  
//       and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//       nor the names of its contributors may be used to endorse or promote products  
//       derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
// 

package net.cyklotron.cms.ngodatabase.organizations;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.spans.SpanFirstQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.util.Version;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.utils.Timer;

import net.cyklotron.cms.ngodatabase.AbstractIndex;
import net.cyklotron.cms.ngodatabase.Organization;
import net.cyklotron.cms.search.analysis.AlphanumericFilter;

/**
 * @author lukasz, rafal
 */
public class OrganizationsIndex
    extends AbstractIndex<Organization>
{
    // constants /////////////////////////////////////////////////////////////

    private static final int FUZZY_QUERY_PREFIX_LENGTH = 4;

    private static final float FUZZY_QUERY_MIN_SIMILARITY = 0.75f;

    private static final int MAX_RESULTS = 25;

    private static final int MAX_TOKEN_LENGTH = 25;

    private static final String INDEX_PATH = "ngo/database/incoming/index";

    public static final String ORGANIZATION_NAME_STOPWORDS_LOCATION = "net/cyklotron/cms/ngodatabase/organizations/name_stop_words.txt";

    public static final String STOPWORDS_ENCODING = "UTF-8";

    public OrganizationsIndex(FileSystem fileSystem, Logger log)
        throws IOException
    {
        super(fileSystem, log, INDEX_PATH);
    }

    protected Analyzer getAnalyzer(FileSystem fileSystem)
        throws IOException
    {
        return new OrganizationNameAnalyzer(fileSystem);
    }

    protected Document toDocument(Organization organization)
    {
        Document document = new Document();
        document
            .add(new NumericField("id", 4, Field.Store.YES, true).setLongValue(organization.getId()));
        document.add(new Field("name", organization.getName(), Field.Store.YES, Field.Index.ANALYZED,
            Field.TermVector.WITH_POSITIONS_OFFSETS));
        document.add(new Field("sort_by_name", getSortValue(organization.getName()),
            Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
        document.add(new Field("province", organization.getProvince(), Field.Store.YES,
            Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
        document.add(new Field("city", organization.getCity(), Field.Store.YES, Field.Index.ANALYZED,
            Field.TermVector.WITH_POSITIONS_OFFSETS));
        document.add(new Field("sort_by_city", getSortValue(organization.getCity()),
            Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
        document.add(new Field("street", organization.getStreet(), Field.Store.YES,
            Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
        document.add(new Field("postCode", organization.getPostCode(), Field.Store.YES,
            Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
        return document;
    }

    protected Organization fromDocument(Document document)
    {
        long id = Long.parseLong(document.get("id"));
        String name = document.get("name");
        String province = document.get("province");
        String city = document.get("city");
        String street = document.get("street");
        String postCode = document.get("postCode");
        return new Organization(id, name, province, city, street, postCode);
    }

    public Organization getOrganization(Long id)
    {
        try
        {
            Query query = NumericRangeQuery.newLongRange("id", id, id, true, true);
            return singleResult(getSearcher().search(query, 1));
        }
        catch(Exception e)
        {
            logger.error("search error", e);
            return null;
        }
    }

    public List<Organization> getOrganizations(String name, Locale locale)
    {
        try
        {
            BooleanQuery query = new BooleanQuery();
            List<Term> terms = analyze("name", name);
            int i = 0;
            for(Term term : terms)
            {
                if(FUZZY_QUERY_PREFIX_LENGTH < term.text().length())
                {
                    FuzzyQuery fuzzyQuery = new FuzzyQuery(term,FUZZY_QUERY_MIN_SIMILARITY,FUZZY_QUERY_PREFIX_LENGTH);
                    fuzzyQuery.setBoost((1 - (getSearcher().docFreq(term) / getSearcher().maxDoc()))/10);
                    query.add(fuzzyQuery, BooleanClause.Occur.SHOULD);
                }
                SpanFirstQuery spanFirstQuery = new SpanFirstQuery(new SpanTermQuery(term), ++i);
                spanFirstQuery.setBoost((terms.size()+1)-i);
                query.add(spanFirstQuery, BooleanClause.Occur.SHOULD);
                
                PrefixQuery prefixQuery = new PrefixQuery(term);
                prefixQuery.setBoost(1);
                query.add(prefixQuery, BooleanClause.Occur.SHOULD);
            }
            terms = analyze("city", name);
            SpanFirstQuery spanFirstQuery = new SpanFirstQuery(new SpanTermQuery(terms.get(terms.size()-1)), 1);
            spanFirstQuery.setBoost(1);
            query.add(spanFirstQuery, BooleanClause.Occur.SHOULD);
            PrefixQuery prefixQuery = new PrefixQuery(terms.get(terms.size()-1));
            prefixQuery.setBoost(1);
            query.add(prefixQuery, BooleanClause.Occur.SHOULD);
            
            Timer timer = new Timer();
            Sort sort = new Sort(new SortField[]{SortField.FIELD_SCORE,
                            new SortField("sort_by_name", locale),
                            new SortField("sort_by_city", locale)});
            List<Organization> results = results(getSearcher().search(query, null, MAX_RESULTS, sort));
            logger.debug("query: " + query.toString() + " " + results.size() + " in "
                + timer.getElapsedMillis() + "ms");
            return results;
        }
        catch(Exception e)
        {
            logger.error("search error", e);
            return Collections.emptyList();
        }
    }

    private static class OrganizationNameAnalyzer
        extends Analyzer
    {
        private final Set<String> stopWords;

        public OrganizationNameAnalyzer(FileSystem fileSystem)
            throws IOException
        {
            stopWords = WordlistLoader.getWordSet(fileSystem.getReader(
                ORGANIZATION_NAME_STOPWORDS_LOCATION, STOPWORDS_ENCODING));
        }

        private static final class SavedStreams
        {
            private final Tokenizer tokenizer;

            private final TokenStream tokenStream;

            public SavedStreams(Tokenizer tokenizer, TokenStream tokenStream)
            {
                this.tokenizer = tokenizer;
                this.tokenStream = tokenStream;
            }

            public TokenStream reset(Reader reader)
                throws IOException
            {
                tokenizer.reset(reader);
                return tokenStream;
            }
        }

        @Override
        public TokenStream reusableTokenStream(String fieldName, Reader reader)
            throws IOException
        {
            SavedStreams streams = (SavedStreams)getPreviousTokenStream();
            if(streams == null)
            {
                Tokenizer tokenizer = tokenizer(reader);
                TokenStream filteredTokenStream = filteredTokenStream(tokenizer);
                setPreviousTokenStream(new SavedStreams(tokenizer, filteredTokenStream));
                return filteredTokenStream;
            }
            else
            {
                return streams.reset(reader);
            }
        }

        @Override
        public TokenStream tokenStream(String fieldName, Reader reader)
        {
            return filteredTokenStream(tokenizer(reader));
        }

        private Tokenizer tokenizer(Reader reader)
        {
            StandardTokenizer tokenStream = new StandardTokenizer(Version.LUCENE_30, reader);
            tokenStream.setMaxTokenLength(MAX_TOKEN_LENGTH);
            return tokenStream;
        }

        private TokenStream filteredTokenStream(Tokenizer tokenStream)
        {
            TokenStream filteredTokenStream = new StandardFilter(tokenStream);
            filteredTokenStream = new LowerCaseFilter(filteredTokenStream);
            filteredTokenStream = new AlphanumericFilter(filteredTokenStream);
            filteredTokenStream = new StopFilter(true, filteredTokenStream, stopWords);
            return filteredTokenStream;
        }
    }

    private String getSortValue(String value)
    {
        return value.replaceAll("[^\\p{L}\\p{N}]", "").trim();
    }

}
