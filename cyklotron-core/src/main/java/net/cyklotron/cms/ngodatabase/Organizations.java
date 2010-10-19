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

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.LocalFileSystemProvider;

/**
 * @author lukasz, rafal
 */
public class Organizations
{
    // constants /////////////////////////////////////////////////////////////

    private static final String INDEX_PATH = "ngo/database/incoming/index";
    
    private static final StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

    private final IndexWriter writer;

    private final IndexSearcher searcher;
    
    private final Logger log;

    public Organizations(FileSystem fileSystem, Logger log)
        throws IOException
    {
        File indexLocation = ((LocalFileSystemProvider)fileSystem.getProvider("local"))
            .getFile(INDEX_PATH);
        Directory directory = new NIOFSDirectory(indexLocation);
        // remove stale write lock if one exists
        if(directory.fileExists("write.lock"))
        {
            directory.deleteFile("write.lock");
        }
        IndexWriter writer = null;
        try
        {
            writer = new IndexWriter(directory, analyzer,
                IndexWriter.MaxFieldLength.LIMITED);
        }
        catch(CorruptIndexException e)
        {
            log.error("corrupt index detected, attempting to recover", e);
            CheckIndex checkIndex = new CheckIndex(directory);
            checkIndex.checkIndex();
            // try to reopen index
            writer = new IndexWriter(directory, analyzer,
                IndexWriter.MaxFieldLength.LIMITED);
        }             
        this.writer = writer;
        this.searcher = new IndexSearcher(writer.getReader());
        this.log = log;
    }

    public void startInput()
        throws IOException
    {
        writer.deleteAll();
    }

    public void addOrganization(Organization organization)
        throws CorruptIndexException, IOException
    {
        writer.addDocument(Organization.toDocument(organization));
    }

    public void endInput()
        throws CorruptIndexException, IOException
    {
        writer.optimize();
        writer.commit();
    }

    public Organization getOrganization(Long id)
    {
        Query query = NumericRangeQuery.newLongRange("id", id, id, true, true);
        try
        {
            TopDocs result = searcher.search(query, 1);
            if(result.totalHits == 1)
            {
                return Organization.fromDocument(searcher.doc(result.scoreDocs[0].doc));
            }
        }
        catch(Exception e)
        {
            log.error("search error", e);
        }
        return null;
    }

    public List<Organization> getOrganizations(String substring)
    {
        List<Organization> organizations = new ArrayList<Organization>();
        try
        {
            PhraseQuery query = new PhraseQuery();
            TokenStream ts = analyzer.reusableTokenStream("name", new StringReader(substring));
            ts.reset();
            TermAttribute ta = ts.getAttribute(TermAttribute.class);
            while(ts.incrementToken())
            {
                query.add(new Term("name", ta.term()));
            }
            ts.end();
            ts.close();
            TopDocs result = searcher.search(query, null, 20, new Sort(new SortField(null, SortField.SCORE)));
            for(ScoreDoc scoreDoc : result.scoreDocs)
            {
                organizations.add(Organization.fromDocument(searcher.doc(scoreDoc.doc)));
            }
        }
        catch(Exception e)
        {
            log.error("search error", e);
        }
        return organizations;
    }
}
