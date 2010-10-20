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

package net.cyklotron.cms.ngodatabase;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
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
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;

import net.cyklotron.cms.search.analysis.AlphanumericFilter;

/**
 * @author lukasz, rafal
 */
public class OrganizationsIndex
    extends AbstractIndex<Organization>
{
    // constants /////////////////////////////////////////////////////////////

    private static final int FUZZY_QUERY_PREFIX_LENGTH = 3;

    private static final float FUZZY_QUERY_MIN_SIMILARITY = 0.75f;

    private static final int MAX_RESULTS = 25;

    private static final String STOPWORDS_LOCATION = "/net/cyklotron/cms/search/stopwords-pl_PL.txt";

    private static final int MAX_TOKEN_LENGTH = 25;

    private static final String INDEX_PATH = "ngo/database/incoming/index";

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
            .add(new NumericField("id", 4, Field.Store.YES, true).setLongValue(organization.id));
        document.add(new Field("name", organization.name, Field.Store.YES, Field.Index.ANALYZED,
            Field.TermVector.WITH_POSITIONS_OFFSETS));
        document.add(new Field("province", organization.province, Field.Store.YES,
            Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
        document.add(new Field("city", organization.city, Field.Store.YES, Field.Index.ANALYZED,
            Field.TermVector.WITH_POSITIONS_OFFSETS));
        document.add(new Field("street", organization.street, Field.Store.YES,
            Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
        document.add(new Field("postCode", organization.postCode, Field.Store.YES,
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
            return singleResult(searcher.search(query, 1));
        }
        catch(Exception e)
        {
            logger.error("search error", e);
            return null;
        }
    }

    public List<Organization> getOrganizations(String name)
    {
        try
        {
            BooleanQuery query = new BooleanQuery();
            List<Term> terms = analyze("name", name);
            for(Term term : terms)
            {
                Query fuzzyQuery = new FuzzyQuery(term, FUZZY_QUERY_MIN_SIMILARITY,
                    FUZZY_QUERY_PREFIX_LENGTH);
                query.add(fuzzyQuery, BooleanClause.Occur.MUST);
            }
            return results(searcher.search(query, null, MAX_RESULTS));
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
        private final Set<String> stopSet;

        public OrganizationNameAnalyzer(FileSystem fileSystem)
            throws IOException
        {
            Reader reader = fileSystem.getReader(STOPWORDS_LOCATION, "UTF-8");
            stopSet = WordlistLoader.getWordSet(reader);
        }

        private static final class SavedStreams
        {
            StandardTokenizer tokenStream;

            TokenStream filteredTokenStream;
        }

        @Override
        public TokenStream reusableTokenStream(String fieldName, Reader reader)
            throws IOException
        {
            SavedStreams streams = (SavedStreams)getPreviousTokenStream();
            if(streams == null)
            {
                streams = new SavedStreams();
                setPreviousTokenStream(streams);
                streams.tokenStream = new StandardTokenizer(Version.LUCENE_30, reader);
                streams.tokenStream.setMaxTokenLength(MAX_TOKEN_LENGTH);
                streams.filteredTokenStream = new StandardFilter(streams.tokenStream);
                streams.filteredTokenStream = new LowerCaseFilter(streams.filteredTokenStream);
                streams.filteredTokenStream = new AlphanumericFilter(streams.filteredTokenStream);
                streams.filteredTokenStream = new StopFilter(true, streams.filteredTokenStream,
                    stopSet);
            }
            else
            {
                streams.tokenStream.reset(reader);
            }
            return streams.filteredTokenStream;
        }

        @Override
        public TokenStream tokenStream(String fieldName, Reader reader)
        {
            StandardTokenizer tokenStream = new StandardTokenizer(Version.LUCENE_30, reader);
            tokenStream.setMaxTokenLength(MAX_TOKEN_LENGTH);
            TokenStream filteredTokenStream = new StandardFilter(tokenStream);
            filteredTokenStream = new LowerCaseFilter(filteredTokenStream);
            filteredTokenStream = new AlphanumericFilter(filteredTokenStream);
            filteredTokenStream = new StopFilter(true, filteredTokenStream, stopSet);
            return filteredTokenStream;
        }
    }
}
