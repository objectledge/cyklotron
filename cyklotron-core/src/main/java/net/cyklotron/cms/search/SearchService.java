package net.cyklotron.cms.search;

import java.util.List;
import java.util.Locale;

import net.cyklotron.cms.site.SiteResource;

import org.apache.lucene.analysis.Analyzer;
import org.jcontainer.dna.Configuration;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.table.TableFilter;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchService.java,v 1.5 2005-02-09 19:22:28 rafal Exp $
 */
public interface SearchService
    extends SearchConstants
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
     * 
     */
    public Configuration getConfiguration();
    
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
    public Resource getIndexesRoot(CoralSession coralSession, SiteResource site)
        throws SearchException;

    /**
     * Returns the search pool root resource for a given site.
     *
     * @param site the site.
     * @return the pool root resource for a given site.
     */
    public Resource getPoolsRoot(CoralSession coralSession,SiteResource site)
        throws SearchException;

    /**
     * Returns the root resource of a search application for a given site.
     *
     * @param site the site.
     * @return the search root resource for a given site.
     */
    public RootResource getSearchRoot(CoralSession coralSession,SiteResource site)
        throws SearchException;
    
    // manipulation methods ////////////////////////////////////////////////////////////////////////
    
    /**
     * Creates new index for a specified site.
     *
     * @param site the site resource.
     * @param name the name of the index.
     *
     * @return the index resource.
     * @throws SearchException
     */
    public IndexResource createIndex(CoralSession coralSession,SiteResource site, String name)
        throws SearchException;

    /**
     * Deletes a given index resource.
     *
     * @param index the index resource.
     * @throws SearchException
     */
    public void deleteIndex(CoralSession coralSession, IndexResource index)
        throws SearchException;

    // indexes information ////////////////////////////////////////////////////////////////////////

    /**
     * Returns indexes which index a given resource.
     * 
     * @param res resource for which indexes are sought
     * @return array of found indexes
     */
    public IndexResource[] getIndex(CoralSession coralSession, IndexableResource res);
    
    /**
     * Returns the search x-references resource used to define nodes and branches for indexes..
     * 
     * @return the singleton x-references resource. 
     */
    XRefsResource getXRefsResource();
    
    public Relation getIndexedBranchesRelation(CoralSession coralSession);
    
    public Relation getIndexedNodesRelation(CoralSession coralSession);
    
        /**
     * Get resource branches indexed by a given index.
     * 
     * @param index an index resource
     * @return list of found resources which define branches
     */
    public List getIndexedBranches(CoralSession coralSession, IndexResource index);
    /**
     * Set resource branches indexed by a given index.
     * 
     * @param index an index resource
     * @param resources list of resources which define branches
     */
    public void setIndexedBranches(CoralSession coralSession, IndexResource index, List resources);

    /**
     * Get single resources indexed by a given index.
     * 
     * @param index an index resource
     * @return list of found resources
     */
    public List getIndexedNodes(CoralSession coralSession, IndexResource index);
    /**
     * Set single resources indexed by a given index.
     * 
     * @param index an index resource
     * @param resources list of resources
     */
    public void setIndexedNodes(CoralSession coralSession, IndexResource index, List resources);

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
