package net.cyklotron.cms.search.internal;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;

import net.labeo.services.InitializationError;
import net.labeo.services.authentication.AuthenticationService;
import net.labeo.services.file.FileService;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;

import net.cyklotron.cms.ProtectedResource;
import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.IndexableResource;
import net.cyklotron.cms.search.IndexingFacility;
import net.cyklotron.cms.search.SearchConstants;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.SearchUtil;
import net.cyklotron.cms.site.SiteResource;

/**
 * Implementation of Indexing
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IndexingFacilityImpl.java,v 1.1 2005-01-12 20:44:34 pablo Exp $
 */
public class IndexingFacilityImpl implements IndexingFacility 
{
    /** logging facility */
    private LoggingFacility log;

    /** search service - for managing index resources */
    private SearchService searchService;

    /** resource service */
    private ResourceService resourceService;

    // local ---------------------------------------------------------------------------------------

    private IndexingFacilityUtil utility;
    private IndexingResourceChangesListener indexingListener;
    
    /** Lucene document construction facility. */
    private DocumentConstructor docConstructor;

    /** system anonymous subject */
    private Subject anonymousSubject;

    /**
     * Creates the facility.
     * @param log
     * @param searchService
     * @param fileService
     * @param resourceService
     */
    public IndexingFacilityImpl(
        LoggingFacility log,
        SearchService searchService,
        FileService fileService,
        ResourceService resourceService,
        AuthenticationService authenticationService)
    {
        this.searchService = searchService;
        this.resourceService = resourceService;
        this.log = log;        

        docConstructor = new DocumentConstructor(searchService.getBroker());
        
        utility = new IndexingFacilityUtil(searchService, fileService, resourceService,
            searchService.getConfiguration().get(BASE_DIRECTORY).asString(DEFAULT_BASE_DIRECTORY),
            searchService.getConfiguration().get("mergeFactor").asInt(20),
            searchService.getConfiguration().get("minMergeDocs").asInt(100),
            searchService.getConfiguration().get("maxMergeDocs").asInt(5000));
        
        // get anonymous subject
        try
        {
            anonymousSubject = resourceService.getSecurity().getSubject(
                authenticationService.getAnonymousUser().getName());
        }
        catch (EntityDoesNotExistException e)
        {
            throw new InitializationError("IndexingFacility: Could not find anonymous subject", e);
        }

        // register listeners
        indexingListener = new IndexingResourceChangesListener(
            log, searchService, this, resourceService);
    }

    // IndexingFacility methods --------------------------------------------------------------------

    /**
     * @{inheritDoc}
     */
    public String getIndexFilesPath(SiteResource site, String indexName)
        throws SearchException
    {
        return utility.getIndexFilesPath(site, indexName);
    }

    /**
     * @{inheritDoc}
     */
    public void createIndexFiles(IndexResource index) throws SearchException
    {
        utility.createIndexFiles(index);
    }

    /**
     * @{inheritDoc}
     */
    public Directory getIndexDirectory(IndexResource index) throws SearchException
    {
        return utility.getIndexDirectory(index);
    }

    /**
     * @{inheritDoc}
     */
    public void reindex(IndexResource index) throws SearchException
    {
        synchronized(index)
        {
            Directory dir = getIndexDirectory(index);
            try
            {
                // remove index data
                String[] files = dir.list();
                for (int i = 0; i < files.length; i++)
                {
                    dir.deleteFile(files[i]);
                }
            }
            catch (IOException e)
            {
                throw new SearchException(
                    "IndexingFacility: Could not delete index files for index '"+index.getPath()+
                    "' while reindexing the index", e);
            }
    
            IndexWriter indexWriter = 
                utility.openIndexWriter(dir, index, true, "reindexing the index");
    
            try
            {
                // go recursive on all branches
                List resources = searchService.getIndexedBranches(index);
                for (Iterator i = resources.iterator(); i.hasNext();)
                {
                    Resource branch = (Resource) (i.next());
                    index(branch, branch, indexWriter, index, true);
                }
    
                // go locally on nodes
                resources = searchService.getIndexedNodes(index);
                for (Iterator i = resources.iterator(); i.hasNext();)
                {
                    Resource branch = (Resource) (i.next());
                    index(branch, branch, indexWriter, index, false);
                }
            }
            catch (IOException e)
            {
                throw new SearchException(
                    "IndexingFacility: Could not index branches or nodes for index '"+
                    index.getPath()+"' while reindexing the index", e);
            }
            finally
            {
                utility.closeIndexWriter(indexWriter, index, "reindexing the index");
            }
        }
    }

    public void indexMissing(IndexResource index) throws SearchException
    {
        Set missingResources = 
            SearchUtil.getResources(resourceService, log, getMissingResourceIds(index));
        IndexableResource[] res = (IndexableResource[]) 
            missingResources.toArray(new IndexableResource[missingResources.size()]);
        addToIndex(index, res);
    }

    public void deleteDeleted(IndexResource index)
        throws SearchException
    {
        Set deletedResourcesIds = getDeletedResourcesIds(index);
        long[] ids = new long[deletedResourcesIds.size()];
        int i = 0;
        for (Iterator iter = deletedResourcesIds.iterator(); iter.hasNext(); i++)
        {
            ids[i] = ((Long)iter.next()).longValue();
        }
        deleteFromIndex(index, ids);
    }

    public void reindexDuplicated(IndexResource index)
        throws SearchException
    {
        Set duplicateResourcesIds = getDuplicateResourceIds(index);
        long[] ids = new long[duplicateResourcesIds.size()];
        int i = 0;
        for (Iterator iter = duplicateResourcesIds.iterator(); iter.hasNext(); i++)
        {
            ids[i] = ((Long)iter.next()).longValue();
        }
        deleteFromIndex(index, ids);
        Set resources = SearchUtil.getResources(resourceService, log, duplicateResourcesIds);
        IndexableResource[] res = 
            (IndexableResource[]) resources.toArray(new IndexableResource[resources.size()]);
        addToIndex(index, res); 
    }
    
    public void optimize(IndexResource index) throws SearchException
    {
        synchronized(index)
        {
            Directory dir = getIndexDirectory(index);
            IndexWriter indexWriter = 
                utility.openIndexWriter(dir, index, false, "optimizing the index");
            try
            {
                indexWriter.optimize();
            }
            catch (IOException e)
            {
                throw new SearchException("IndexingFacility: Problem optimising " +
                        "index '"+index.getPath()+"'", e);
            }
            finally
            {
                utility.closeIndexWriter(indexWriter, index, "incrementally optimizing the index");
            }
        }
    }
    
    /**
     * Chcecks if a given resource may be put in the given index.
     * 
     * @param node resource to be checked
     * @param index index to be checked
     * @return <code>true</code> if given resource may be indexed by a given index
     */
    private boolean liableForIndexing(IndexableResource node, IndexResource index)
    {
        if(!index.getPublic())
        {
            return true;
        }
        
        if(node instanceof ProtectedResource)
        {
            ProtectedResource protectedNode = (ProtectedResource)node;
            return protectedNode.canView(anonymousSubject, new Date());
        }
        return true;
    }

    // index info ----------------------------------------------------------------------------------

    public Set getIndexedResourceIds(IndexResource index)
        throws SearchException
    {
        return utility.getIndexedResourceIds(index);
    }

    public Set getMissingResourceIds(IndexResource index)
        throws SearchException
    {
        return utility.getMissingResourceIds(index);
    }
    
    public Set getDeletedResourcesIds(IndexResource index)
    throws SearchException
    {
        return utility.getDeletedResourcesIds(index);
    }

    public Set getDuplicateResourceIds(IndexResource index)
        throws SearchException
    {
        return utility.getDuplicateResourceIds(index);
    }

    /**
     * Returns map of resource sets keyed by index resoruces. This shows the mapping between
     * the index and indexed resource. The parameter is the set of resources to be assigned
     * to indexes.
     * 
     * @param resources the set of resources for which indexes are sought
     * @return map of found indexes with corresponding resources. 
     */
    public Map getResourcesByIndex(Set resources)    
    {
        Map resSetByIndex = new HashMap();
        Resource[] tmpIndexes = null;
        for (Iterator iter = resources.iterator(); iter.hasNext();)
        {
            IndexableResource res = (IndexableResource)iter.next();
            
            // add indexes indexing the resource as a node
            tmpIndexes = searchService.getIndexedNodesXRef().getInv(res);
            addResourceForIndexes(resSetByIndex, res, tmpIndexes);
            
            // add indexes indexing the resource as a part of a branch
            Resource resource = res;
            while (resource != null)
            {
                tmpIndexes = searchService.getIndexedBranchesXRef().getInv(resource);
                addResourceForIndexes(resSetByIndex, res, tmpIndexes);
                resource = resource.getParent();
            }
        }
        
        return resSetByIndex;
    }
    
    private void addResourceForIndexes(Map resSetByIndex, Resource res, Resource[] tmpIndexes)
    {
        for (int i = 0; i < tmpIndexes.length; i++)
        {
            IndexResource index = (IndexResource) tmpIndexes[i];
            Set resSet = (Set) resSetByIndex.get(index);
            if(resSet == null)
            {
                resSet = new HashSet();
            }
            resSet.add(res);
            resSetByIndex.put(index, resSet);
        }
    }    
    
    // adding to index -----------------------------------------------------------------------------
    
    public void addToIndex(IndexResource index, IndexableResource[] res)
        throws SearchException
    {
        synchronized(index)
        {
            // get index data directory
            Directory dir = getIndexDirectory(index);
    
            IndexWriter indexWriter = 
                utility.openIndexWriter(dir, index, false, "adding resources to the index");

            for (int i = 0; i < res.length; i++)
            {
                IndexableResource resource = res[i];
                Resource branch = utility.getBranch(index, resource);
                
                // add to index
                if(branch != null && liableForIndexing(resource, index))
                {
                    // cache the document maybe
                    // - need a kind of temporary cache while adding resources to many indexes
                    Document doc = docConstructor.createDocument(resource, branch);
                    if(doc == null)
                    {
                        log.error("IndexingFacility: Could not create Document for resource #"+
                            resource.getIdString()+" '"+resource.getPath()+"'");
                    }
                    else
                    {
                        try
                        {
                            indexWriter.addDocument(doc);
                        }
                        catch(IOException e)
                        {
                            log.error(
                            "IndexingFacility: Could not add document to IndexWriter for index '"+
                            index.getPath()+"' for resource #"+resource.getIdString()+" '"+
                            resource.getPath()+"'", e);
                        }
                    }
                }
            }
            
            utility.closeIndexWriter(indexWriter, index, "adding resources to the index");
        }
    }

    // deleting from index -------------------------------------------------------------------------
    
    public void deleteFromIndex(IndexResource index, IndexableResource[] iRes)
        throws SearchException
    {
        long[] id = new long[iRes.length];
        for (int i = 0; i < iRes.length; i++)
        {
            id[i] = iRes[i].getId();
        }
        deleteFromIndex(index, id);
    }

    public void deleteFromIndex(IndexResource index, long[] ids)
        throws SearchException
    {
        String[] id = new String[ids.length];
        for (int i = 0; i < ids.length; i++)
        {
            id[i] = Long.toString(ids[i]);
        }
        
        synchronized(index)
        {
            IndexReader indexReader = utility.openIndexReader(index, "removing resources # "+id);

            // delete old docs
            Term idTerm = null;
            for (int i = 0; i < id.length; i++)
            {
                try
                {
                    idTerm = new Term(SearchConstants.FIELD_ID, id[i]);
                    indexReader.delete(idTerm);
                }
                catch (IOException e)
                {
                    log.error("IndexingFacility: Could not remove resource #"+id[i]+
                    " from  index '"+index.getPath()+"'", e);
                }
            }

            utility.closeIndexReader(indexReader, index, "removing resources # "+id);
        }
    }    
    
    // implementation ------------------------------------------------------------------------------

    /**
     * index the resource tree.
     *
     * @param node the resource tree node.
     * @param branch branch under which current node is indexed 
     * @param indexWriter the opened index writer.
     * @param indexReader the index reader used to check if the resource is already indexed.
     * @param index the index resource representing the lucene index.
     * @param recursive <code>true</code> if indexing should be recursive.
     */
    private void index(Resource node, Resource branch, IndexWriter indexWriter,
        IndexResource index, boolean recursive)
    throws IOException
    {
        if (node instanceof IndexableResource)
        {
        	IndexableResource res = (IndexableResource)node;
            // add to index
        	if(liableForIndexing(res, index))
        	{
	            Document doc = docConstructor.createDocument(res, branch);
                if(doc == null)
                {
                    log.error("IndexingFacility: Could not create Document for resource '"+
                        res.getPath()+"'");
                }
	            else
	            {
	                indexWriter.addDocument(doc);
	            }
			}
        }

        if (recursive)
        {
            Resource[] children = resourceService.getStore().getResource(node);
            for (int i = 0; i < children.length; i++)
            {
                index(children[i], branch, indexWriter, index, recursive);
            }
        }
    }
}
