package net.cyklotron.cms.search;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;

import net.labeo.services.Service;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.event.ResourceChangeListener;
import net.labeo.services.resource.event.ResourceCreationListener;
import net.labeo.services.resource.event.ResourceDeletionListener;
import net.labeo.services.resource.event.ResourceTreeChangeListener;
import net.labeo.services.resource.generic.CrossReference;
import net.labeo.services.table.TableFilter;

import net.cyklotron.cms.site.SiteResource;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchService.java,v 1.1 2005-01-12 20:44:36 pablo Exp $
 */
public interface SearchService
    extends Service, SearchConstants
{
    /** The name of the service (<code>"search"</code>). */
    public final static String SERVICE_NAME = "search";

    /**
     * The logging facility where the service issues it's informational messages.
     */
    public static final String LOGGING_FACILITY = "search";

    // facilities access methods ///////////////////////////////////////////////////////////////////
    /**
     * @return the indexing facility used by search
     */
    public IndexingFacility getIndexingFacility();

    /**
     * @return the searching facility used by search
     */
    public SearchingFacility getSearchingFacility();
    
    // access methods //////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the indexes root resource for a given site.
     *
     * @param site the site.
     * @return the indexes root resource for a given site.
     */
    public Resource getIndexesRoot(SiteResource site)
        throws SearchException;

    /**
     * Returns the search pool root resource for a given site.
     *
     * @param site the site.
     * @return the pool root resource for a given site.
     */
    public Resource getPoolsRoot(SiteResource site)
        throws SearchException;

    /**
     * Returns the root resource of a search application for a given site.
     *
     * @param site the site.
     * @return the search root resource for a given site.
     */
    public RootResource getSearchRoot(SiteResource site)
        throws SearchException;
    
    // manipulation methods ////////////////////////////////////////////////////////////////////////
    
    /**
     * Creates new index for a specified site.
     *
     * @param site the site resource.
     * @param name the name of the index.
     * @param subject the creator.
     *
     * @return the index resource.
     * @throws SearchException
     */
    public IndexResource createIndex(SiteResource site, String name, Subject subject)
        throws SearchException;

    /**
     * Deletes a given index resource.
     *
     * @param index the index resource.
     * @param subject the subject performing the operation.
     * @throws SearchException
     */
    public void deleteIndex(IndexResource index, Subject subject)
        throws SearchException;

    // indexes information ////////////////////////////////////////////////////////////////////////

    /**
     * Returns indexes which index a given resource.
     * 
     * @param res resource for which indexes are sought
     * @return array of found indexes
     */
    public IndexResource[] getIndex(IndexableResource res);
    
    /**
     * Returns the search x-references resource used to define nodes and branches for indexes..
     * 
     * @return the singleton x-references resource. 
     */
    public XRefsResource getXRefsResource();

    /**
     * Get resource branches indexed by a given index.
     * 
     * @param index an index resource
     * @return list of found resources which define branches
     */
    public List getIndexedBranches(IndexResource index);
    /**
     * Set resource branches indexed by a given index.
     * 
     * @param index an index resource
     * @param resources list of resources which define branches
     */
    public void setIndexedBranches(IndexResource index, List resources);

    /**
     * Get single resources indexed by a given index.
     * 
     * @param index an index resource
     * @return list of found resources
     */
    public List getIndexedNodes(IndexResource index);
    /**
     * Set single resources indexed by a given index.
     * 
     * @param index an index resource
     * @param resources list of resources
     */
    public void setIndexedNodes(IndexResource index, List resources);

    /**
     * @return indexes &lt;-&gt; indexed resource tree branches cross reference
     */    
    public CrossReference getIndexedBranchesXRef();

    /**
     * @return indexes &lt;-&gt; indexed resource tree nodes cross reference
     */    
    public CrossReference getIndexedNodesXRef();

    /**
     * Updates x-reference defining a relation between indexes and indexed resources and branches.
     * 
     * @param subject a subject performing an operation
     */
    public void updateBranchesAndNodesXRef(Subject subject)
        throws ValueRequiredException;
    
    // other //////////////////////////////////////////////////////////////////////////////////////

    /**
     * Return an analyzer for a given locale. This will allow a free implementation of analyzers
     * depending on languages set for indexes.
     *
     * @param locale the language
     * @return a analyzer which fits a given locale
     */
    public Analyzer getAnalyzer(Locale locale);

    /**
     * Returns a filter for filtering the branch tree while editing an index.
     */
    public TableFilter getBranchFilter(SiteResource site);
}
