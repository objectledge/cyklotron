package net.cyklotron.cms.search.internal;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.cyklotron.cms.ProtectedResource;
import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.IndexableResource;
import net.cyklotron.cms.search.IndexingFacility;
import net.cyklotron.cms.search.SearchConstants;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.InitializationError;
import net.labeo.services.authentication.AuthenticationService;
import net.labeo.services.file.FileService;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.resource.ResourceInheritance;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.generic.CrossReference;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;

/**
 * Implementation of Indexing
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IndexingFacilityUtil.java,v 1.1 2005-01-12 20:44:34 pablo Exp $
 */
public class IndexingFacilityUtil 
{
    // deps ----------------------------------------------------------------------------------------

    /** search service - for managing index resources */
    private SearchService searchService;

    /** file service - for managing index files */
    private FileService fileService;

    /** resource service */
    private ResourceService resourceService;

    // config --------------------------------------------------------------------------------------

    /** path of base directory for site indexes */
    private String sitesIndexesDirPath;

    /** lucene's mergeFactor
     * (number of segments joined in one go, lucene default 10) */
    private int mergeFactor;

    /** lucene's minMergeDocs
     * (size of the segment during indexing - minimal number of docs in memory, lucene default 10) */
    private int minMergeDocs;

    /** lucene's maxMergeDocs
     * (maximal size of the segment after optimisation, lucene default Integer.MAX_VALUE) */
    private int maxMergeDocs;


    // local ---------------------------------------------------------------------------------------

    /**
     * Creates the indexing utility.
     * @param searchService
     * @param fileService
     */
    public IndexingFacilityUtil(SearchService searchService, FileService fileService,
        ResourceService resourceService, 
        String sitesIndexesDirPath, int mergeFactor, int minMergeDocs, int maxMergeDocs)
    {
        this.searchService = searchService;
        this.fileService = fileService;
        this.resourceService = resourceService;
        this.sitesIndexesDirPath = sitesIndexesDirPath;
        try
        {
            checkDirectory(sitesIndexesDirPath);
        }
        catch (SearchException e)
        {
            throw new InitializationError("IndexingFacility: Cannot use base search directory", e);
        }
        
        this.mergeFactor = mergeFactor; 
        this.minMergeDocs = minMergeDocs;
        this.maxMergeDocs = maxMergeDocs;
    }

    // IndexingFacility methods --------------------------------------------------------------------

    public String getIndexFilesPath(SiteResource site, String indexName)
        throws SearchException
    {
        String path = sitesIndexesDirPath+"/"+site.getName()+"/"+indexName;
        try
        {
            checkDirectory(path);
        }
        catch (SearchException e)
        {
            throw new SearchException("IndexingFacility: Cannot use directory for index '"+indexName
               +"' in site '"+site.getName()+"'", e);
        }
        return path;
    }

    public void createIndexFiles(IndexResource index) throws SearchException
    {
        Directory dir = getIndexDirectory(index);
        synchronized(index)
        {
            IndexWriter indexWriter = openIndexWriter(dir, index, true, "creating empty index files");
            try
            {
                indexWriter.close();
            }
            catch (IOException e)
            {
                throw new SearchException(
                    "IndexingFacility: Could not close the index writer for index '"+
                    index.getPath()+"' while creating empty index files", e);
            }
        }
    }

    /**
     * Returns a lucene directory for a given index.
     *
     * @param index the index resource
     * @return the index directory object
     */
    public Directory getIndexDirectory(IndexResource index) throws SearchException
    {
        String path = index.getFilesLocation();
        checkDirectory(path);
        return new LabeoFSDirectory(fileService, path);
    }

    // IndexWriter management ----------------------------------------------------------------------
    
    /**
     * Opens an index writer and sets configured parameters.
     * 
     * @param dir lucene direcotry with the index to be written
     * @param index index resource representing the index
     * @param createIndex set to <code>true</code> if the index should be newly created (emptied)
     * @param whileMsg a part of the exeception message to inform about preformed operation
     * @return the index writer
     * @throws SearchException on problems with index writer opening 
     */
    public IndexWriter openIndexWriter(Directory dir, IndexResource index, boolean createIndex,
        String whileMsg)
        throws SearchException
    {
        try
        {
            IndexWriter indexWriter = new IndexWriter(dir, getAnalyzer(index), createIndex);
            indexWriter.mergeFactor = mergeFactor;
            indexWriter.minMergeDocs = minMergeDocs;
            indexWriter.maxMergeDocs = maxMergeDocs;
            return indexWriter;
        }
        catch (IOException e)
        {
            throw new SearchException("IndexingFacility: Could not create IndexWriter for " +
                    "index '"+index.getPath()+"' while "+whileMsg, e);
        }
    }

    /**
     * Closes a given index writer.
     * 
     * @param indexWriter the index writer to be closed.
     * @param index index resource representing the index
     * @param whileMsg a part of the exeception message to inform about preformed operation
     * @throws SearchException on problems with index writer closing 
     */
    public void closeIndexWriter(IndexWriter indexWriter, IndexResource index, String whileMsg)
        throws SearchException
    {
        try
        {
            indexWriter.close();
        }
        catch (IOException e)
        {
            throw new SearchException("IndexingFacility: Could not close IndexWriter for " +
                    "index '"+index.getPath()+"' while "+whileMsg, e);
        }
    }

    // IndexReader management ---------------------------------------------------------------------------------------------
    
    /**
     * Opens an index reader.
     * 
     * @param index index resource representing the index
     * @param whileMsg a part of the exeception message to inform about preformed operation
     * @return the index reader
     * @throws SearchException on problems with index reader opening 
     */
    public IndexReader openIndexReader(IndexResource index, String whileMsg)
        throws SearchException
    {
        try
        {
            Directory dir = getIndexDirectory(index);
            return IndexReader.open(dir);
        }
        catch (SearchException e)
        {
            throw new SearchException("IndexingFacility: Cannot get directory for index '"+
                index.getPath()+"' while "+whileMsg, e);
        }
        catch (IOException e)
        {
            throw new SearchException("IndexingFacility: Cannot get index reader for index '"+
                index.getPath()+"' while "+whileMsg, e);
        }
    }
    
    /**
     * Closes a given index reader.
     * 
     * @param indexReader the index reader to be closed.
     * @param index index resource representing the index
     * @param whileMsg a part of the exeception message to inform about preformed operation
     * @throws SearchException on problems with index reader closing 
     */
    public void closeIndexReader(IndexReader indexReader, IndexResource index, String whileMsg)
        throws SearchException
    {
        try
        {
            indexReader.close();
        }
        catch (IOException e)
        {
            throw new SearchException("IndexingFacility: Could not close IndexReader for index '"+
                index.getPath()+"' while "+whileMsg, e);
        }
    }
    
    // ---------------------------------------------------------------------------------------------

    public Set getIndexedResourceIds(IndexResource index)
    throws SearchException
    {
        // get index ids and exclude ids from the tree 
        IndexReader indexReader = openIndexReader(index, "getting indexed resources ids");
    
        // get index ids
        Set ids = new HashSet();
        
        try
        {
            TermEnum te = indexReader.terms();
            while(te.next())
            {
                Term t = te.term();
                if(t.field().equals(SearchConstants.FIELD_ID))
                {
                    ids.add(Long.valueOf(t.text()));
                }
            }
            te.close();
        }
        catch(IOException e)
        {
            throw new SearchException("IndexingFacility: Could not get id terms set from '"+
                index.getPath()+"' while getting indexed resources ids", e);
        }
        
        closeIndexReader(indexReader, index, "getting indexed resources ids");
        
        return ids;
    }
    
    public Set getMissingResourceIds(IndexResource index)
        throws SearchException
    {
        // get tree ids and exclude ids from the index
        Set indexIds = getIndexedResourceIds(index);
    
        Set missingIds = new HashSet(128);
        // get ids existing only in the tree
        // go recursive on all branches
        List resources = searchService.getIndexedBranches(index);
        for (Iterator i = resources.iterator(); i.hasNext();)
        {
            Resource branch = (Resource) (i.next());
            addIds(branch, missingIds, indexIds, true);
        }
        // go locally on nodes
        resources = searchService.getIndexedNodes(index);
        for (Iterator i = resources.iterator(); i.hasNext();)
        {
            Resource branch = (Resource) (i.next());
            addIds(branch, missingIds, indexIds, false);
        }
    
        return missingIds;
    }
    
    private void addIds(Resource resource, Set ids, Set excludedIdsSet, boolean recursive)
    {
        Long id = resource.getIdObject();
        if(!excludedIdsSet.contains(id))
        {
            ids.add(id);
        }
        if (recursive)
        {
            Resource[] children = resourceService.getStore().getResource(resource);
            for (int i = 0; i < children.length; i++)
            {
                addIds(children[i], ids, excludedIdsSet, recursive);
            }
        }
    }
    
    public Set getDeletedResourcesIds(IndexResource index)
    throws SearchException
    {
        // get index ids
        Set ids = getIndexedResourceIds(index);
    
        // remove ids existing in the tree
        // go recursive on all branches
        List resources = searchService.getIndexedBranches(index);
        for (Iterator i = resources.iterator(); i.hasNext();)
        {
            Resource branch = (Resource) (i.next());
            removeIds(branch, ids, true);
        }
        // go locally on nodes
        resources = searchService.getIndexedNodes(index);
        for (Iterator i = resources.iterator(); i.hasNext();)
        {
            Resource branch = (Resource) (i.next());
            removeIds(branch, ids, false);
        }
    
        return ids;
    }
    
    private void removeIds(Resource resource, Set ids, boolean recursive)
    {
        ids.remove(resource.getIdObject());
        if (recursive)
        {
            Resource[] children = resourceService.getStore().getResource(resource);
            for (int i = 0; i < children.length; i++)
            {
                removeIds(children[i], ids, recursive);
            }
        }
    }

    public Set getDuplicateResourceIds(IndexResource index)
        throws SearchException
    {
        // get index ids and check duplicates 
        IndexReader indexReader = openIndexReader(index, "getting duplicate indexed resources ids");
    
        // duplicate index ids
        Set duplicateIds = new HashSet();
        
        try
        {
            TermEnum te = indexReader.terms();
            while(te.next())
            {
                Term t = te.term();
                if(t.field().equals(SearchConstants.FIELD_ID))
                {
                    Long tId = Long.valueOf(t.text());
                    if(te.docFreq() > 1)
                    {
                        duplicateIds.add(tId);
                    }
                }
            }
            te.close();
        }
        catch(IOException e)
        {
            throw new SearchException("IndexingFacility: Could not get id terms set from '"+
                index.getPath()+"' while getting duplicate indexed resources ids", e);
        }
        
        closeIndexReader(indexReader, index, "getting duplicate indexed resources ids");
        
        return duplicateIds;
    }
    
    // util ----------------------------------------------------------------------------------------
    
    public Resource getBranch(IndexResource index, IndexableResource resource)
    {
        CrossReference nodesXref = searchService.getIndexedNodesXRef();
        CrossReference branchesXref = searchService.getIndexedBranchesXRef();
            
        Resource branch = resource;
        while (branch != null)
        {
            if(nodesXref.hasRef(index, branch) || branchesXref.hasRef(index, branch))
            {
                return branch; // return early to get the most specific (nearest) branch
            }
            else
            {
                // get more general branch
                branch = branch.getParent();
            }
        }
        return null;
    }

    // implementation ------------------------------------------------------------------------------

    /**
     * Checks if a directory with a given path exists, creates it checks if it can be
     * read and written. If not an exception with a proper message is thrown
     *
     * @param path path of a direcotry to be checked
     */
    private void checkDirectory(String path) throws SearchException
    {
        if (!fileService.exists(path))
        {
            try
            {
                fileService.mkdirs(path);
            }
            catch (IOException e)
            {
                throw new SearchException("IndexingFacility: cannot create directory '"+
                    path+"'", e);
            }
        }
        if (!fileService.canRead(path))
        {
            throw new SearchException("IndexingFacility: cannot read directory '"+path+"'");
        }
        if (!fileService.canWrite(path))
        {
            throw new SearchException("IndexingFacility: cannot write into directory '"+path+"'");
        }
    }

    private Analyzer getAnalyzer(IndexResource index)
    {
        return searchService.getAnalyzer((Locale) null);
    }
}
