package net.cyklotron.cms.search;

import java.util.Map;
import java.util.Set;

import net.cyklotron.cms.site.SiteResource;

import org.apache.lucene.store.Directory;

/**
 * Lucene indexes manipulation interface.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IndexingFacility.java,v 1.3 2005-01-19 08:22:54 pablo Exp $
 */
public interface IndexingFacility
{
    /** base directory for all indexes in system */
    public static final String BASE_DIRECTORY = "base_directory";
    
    /** default base directory for all indexes in system */
    public static final String DEFAULT_BASE_DIRECTORY = "/data/search";
    
    // methods ////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a fresh lucene index files.
     *
     * @param index the index resource for which direcories will be created.
     * @throws SearchException
     */
    public void createIndexFiles(IndexResource index)
        throws SearchException;

    /**
     * Returns the path of a directory for all indexes that belong to the site.
     *
     * @param site the site.
     * @return the index directory path.
     */
    public String getIndexFilesPath(SiteResource site, String indexName)
        throws SearchException;

    /**
     * Index the resources related with a given index.
     *
     * @param index the index resource.
     * @throws SearchException
     */
    public void reindex(IndexResource index)
        throws SearchException;

    /**
     * Index the resources related with a given index in a incremental manner.
     *
     * @param index the index resource.
     * @throws SearchException
     */
    public void indexMissing(IndexResource index)
        throws SearchException;

    /**
     * Delete the deleted resources related with a given index.
     *
     * @param index the index resource.
     * @throws SearchException
     */
    public void deleteDeleted(IndexResource index)
        throws SearchException;
    
    /**
     * Reindex (delete and add) the resources which are duplicated in a given index.
     *
     * @param index the index resource.
     * @throws SearchException
     */
    public void reindexDuplicated(IndexResource index)
        throws SearchException;
    
    /**
     * Optimize the index.
     *
     * @param index the index to be optimized.
     */
    public void optimize(IndexResource index)
        throws SearchException;

    /**
     * Returns a lucene directory for a given index.
     *
     * @param index the index resource
     * @return the index directory object
     */
    public Directory getIndexDirectory(IndexResource index)
        throws SearchException;

    /**
     * Returns a set of resource ids found in the given index.
     *
     * @param index the index resource
     * @return the set of ids as Long objects
     */
    public Set getIndexedResourceIds(IndexResource index)
        throws SearchException;

    /**
     * Returns a set of resource ids which should be but are not indexed by the given index.
     *
     * @param index the index resource
     * @return the set of ids as Long objects
     */
    public Set getMissingResourceIds(IndexResource index)
        throws SearchException;
    
    /**
     * Returns a set of resource ids which are still indexed by the given index and have been
     * already deleted from the system.
     *
     * @param index the index resource
     * @return the set of ids as Long objects
     */
    public Set getDeletedResourcesIds(IndexResource index)
        throws SearchException;
    
    /**
     * Returns a set of resource ids which are duplicated in the given index.
     *
     * @param index the index resource
     * @return the set of ids as Long objects
     */
    public Set getDuplicateResourceIds(IndexResource index)
    throws SearchException;

    /**
     * Returns map of resource sets keyed by index resoruces. This shows the mapping between
     * the index and indexed resource. The parameter is the set of resources to be assigned
     * to indexes.
     * 
     * @param resources the set of resources for which indexes are sought
     * @return map of found indexes with corresponding resources. 
     */
    public Map getResourcesByIndex(Set resources);    

    /**
     * Deletes the resources with given ids from the given index.
     * @param index the index to operate on.
     * @param ids resource ids to be deleted.
     * @throws SearchException
     */
    public void deleteFromIndex(IndexResource index, long[] ids)
        throws SearchException;

    /**
     * Deletes the given resources from the given index.
     * @param index the index to operate on.
     * @param iRes resources to be deleted.
     * @throws SearchException
     */
    public void deleteFromIndex(IndexResource index, IndexableResource[] iRes)
        throws SearchException;
    
    /**
     * Added given indexable resources to the index in a batch mode.
     * 
     * @param index
     * @param res
     */
    public void addToIndex(IndexResource index, IndexableResource[] res)
        throws SearchException;
}
