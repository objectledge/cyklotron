package net.cyklotron.cms.search.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.IndexResourceImpl;
import net.cyklotron.cms.search.IndexableResource;
import net.cyklotron.cms.search.IndexingFacility;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.RootResource;
import net.cyklotron.cms.search.RootResourceImpl;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.SearchingFacility;
import net.cyklotron.cms.search.XRefsResource;
import net.cyklotron.cms.search.searching.CategoryAnalyzer;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.labeo.LabeoRuntimeException;
import net.labeo.services.BaseService;
import net.labeo.services.InitializationError;
import net.labeo.services.authentication.AuthenticationService;
import net.labeo.services.file.FileService;
import net.labeo.services.logging.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.generic.CrossReference;
import net.labeo.services.resource.generic.NodeResourceImpl;
import net.labeo.services.resource.table.PathFilter;
import net.labeo.services.table.TableFilter;
import net.labeo.util.configuration.BaseParameterContainer;

import org.apache.lucene.analysis.Analyzer;

/**
 * Implementation of Search Service
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchServiceImpl.java,v 1.2 2005-01-18 17:38:08 pablo Exp $
 */
public class SearchServiceImpl extends BaseService implements SearchService
{
    // instance variables ////////////////////////////////////////////////////

    /** logging facility */
    private Logger log;

    /** resource service */
    private CoralSession resourceService;

    /** file service - for managing indexes */
    private FileService fileService;

    /** site service */
    private SiteService siteService;
    
    // local ////////////////////////////////////////////////////////////////////
    
    /** resource containing x-references used by search */
    private XRefsResource searchXRefs;

    /** system root subject */
    private Subject rootSubject;

    /** the searching facility. */
    private SearchingFacility searchingFacility;

    /** the indexing facility. */
    private IndexingFacility indexingFacility;

    // initialization ////////////////////////////////////////////////////////

    /**
     * Starts the service - the search service must be started on broker start in order to listen
     * to resource tree changes.
     */
    public void start()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(LOGGING_FACILITY);
        resourceService = (CoralSession)broker.getService(CoralSession.SERVICE_NAME);
        fileService = (FileService)broker.getService(FileService.SERVICE_NAME);
        siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);

        Resource[] ress = resourceService.getStore().getResourceByPath("/cms/search");
        if (ress.length == 0)
        {
            throw new InitializationError("cannot find x-references resource for search service");
        }
        else if (ress.length > 1)
        {
            throw new InitializationError("too many x-reference resources for search service");
        }
        searchXRefs = (XRefsResource)ress[0];

		// get root subjects
        try
        {
            rootSubject = resourceService.getSecurity().getSubject(Subject.ROOT);
        }
        catch (EntityDoesNotExistException e)
        {
            throw new InitializationError("Could not find root subject");
        }

        AuthenticationService authenticationService =
            (AuthenticationService)broker.getService(AuthenticationService.SERVICE_NAME);
        // prepare indexing facility (registers listeners)
        indexingFacility = new IndexingFacilityImpl(log, this, fileService, resourceService, authenticationService);
        // prepare searching facility
        searchingFacility = new SearchingFacilityImpl(log, indexingFacility, resourceService, authenticationService);
    }

    // search service ////////////////////////////////////////////////////////

    public IndexingFacility getIndexingFacility()
    {
        return indexingFacility;
    }

    public SearchingFacility getSearchingFacility()
    {
        return searchingFacility;
    }
    
    public Resource getIndexesRoot(SiteResource site) throws SearchException
    {
        Resource[] roots = null;
        if (site != null)
        {
            Resource searchRoot = getSearchRoot(site);
            roots = resourceService.getStore().getResource(searchRoot, "indexes");
            if (roots.length == 0)
            {
                try
                {
                    return NodeResourceImpl.createNodeResource(resourceService, "indexes", searchRoot, rootSubject);
                }
                catch (ValueRequiredException e)
                {
                    throw new SearchException("Couldn't create indexes node");
                }
            }
            if (roots.length > 1)
            {
                throw new SearchException("multiple indexes roots for site " + site.getName());
            }
        }
        return roots[0];
    }

    public Resource getPoolsRoot(SiteResource site) throws SearchException
    {
        Resource[] roots = null;
        if (site != null)
        {
            Resource searchRoot = getSearchRoot(site);
            roots = resourceService.getStore().getResource(searchRoot, "pools");
            if (roots.length == 0)
            {
                try
                {
                    return NodeResourceImpl.createNodeResource(resourceService, "pools", searchRoot, rootSubject);
                }
                catch (ValueRequiredException e)
                {
                    throw new SearchException("Couldn't create pools node");
                }
            }
            if (roots.length > 1)
            {
                throw new SearchException("multiple index pools roots for site " + site.getName());
            }
        }
        return roots[0];
    }

    public RootResource getSearchRoot(SiteResource site) throws SearchException
    {
        Resource[] roots = null;
        roots = resourceService.getStore().getResource(site, "search");
        if (roots.length == 1)
        {
            return (RootResource)roots[0];
        }
        if (roots.length == 0)
        {
            try
            {
                return RootResourceImpl.createRootResource(resourceService, "search", site, new BaseParameterContainer(), rootSubject);
            }
            catch (ValueRequiredException e)
            {
                throw new SearchException("Couldn't create search root node");
            }
        }
        throw new SearchException("Too many search root resources for site: " + site.getName());
    }

    public IndexResource createIndex(SiteResource site, String name, Subject subject) throws SearchException
    {
        Resource parent = getIndexesRoot(site);
        if (resourceService.getStore().getResource(parent, name).length > 0)
        {
            throw new SearchException("cannot create many indexes with the same name '" + name + "'");
        }

        String indexDirectoryPath = indexingFacility.getIndexFilesPath(site, name);

        IndexResource index = null;
        try
        {
            index = IndexResourceImpl.createIndexResource(resourceService, name, parent, indexDirectoryPath, subject);
        }
        catch (ValueRequiredException e)
        {
            throw new SearchException("ValueRequiredException: ", e);
        }
        return index;
    }

    public void deleteIndex(IndexResource index, Subject subject) throws SearchException
    {
        // save some data for later
        String indexResourcePath = index.getPath();
        String indexDirectoryPath = index.getFilesLocation();

        // remove from pools
        /* WARN: this is very unefficient!!  Maybe we should introduce an XRef for
         * index - pool relation, this would speed up index deleting
         *
         * TODO: Use a FIND RESOURCE FROM RESOURCECLASS=''; and store this prepared query.
         */
        SiteResource[] sites = siteService.getSites();
        for (int i = 0; i < sites.length; i++)
        {
            Resource parent = getPoolsRoot(sites[i]);
            Resource[] pools = resourceService.getStore().getResource(parent);
            for (int j = 0; j < pools.length; j++)
            {
                Resource res = pools[j];
                if(res instanceof PoolResource)
                {
                    PoolResource pool = (PoolResource) (res);
                    List indexes = pool.getIndexes();
                    if (indexes.remove(index))
                    {
                        pool.setIndexes(indexes);
                        pool.update(subject);
                    }
                }
            }
        }


        try
        {
            // remove from index - branch x-ref
            List empty = new ArrayList();
            setIndexedNodes(index, empty);
            setIndexedBranches(index, empty);
            updateBranchesAndNodesXRef(subject);

            // delete index resource
            resourceService.getStore().deleteResource(index);
        }
        catch (ValueRequiredException e)
        {
            throw new SearchException("cannot remove index resource from branches/nodes crossreferences", e);
        }
        catch (EntityInUseException e)
        {
            throw new SearchException("cannot remove index resource", e);
        }

        // delete index files
        try
        {
            String[] files = fileService.list(indexDirectoryPath);
            for (int i = 0; i < files.length; i++)
            {
                fileService.delete(indexDirectoryPath + "/" + files[i]);
            }
            fileService.delete(indexDirectoryPath);
        }
        catch (IOException e)
        {
            throw new SearchException("cannot delete index files '" + indexResourcePath + "'", e);
        }
    }

    public IndexResource[] getIndex(IndexableResource res)
    {
        // add indexes indexing the resource as a node
        Resource[] tmp = searchXRefs.getIndexedNodes().getInv(res);
        IndexResource[] indexes = new IndexResource[tmp.length];
        System.arraycopy(tmp, 0, indexes, 0, tmp.length);
        Set indexesSet = new HashSet(indexes.length * 2 + 4);
        indexesSet.addAll(Arrays.asList(indexes));

        // add indexes indexing the resource as a part of a branch
        Resource resource = res;
        while (resource != null)
        {
            tmp = searchXRefs.getIndexedBranches().getInv(resource);
            indexes = new IndexResource[tmp.length];
            System.arraycopy(tmp, 0, indexes, 0, tmp.length);
            indexesSet.addAll(Arrays.asList(indexes));
            resource = resource.getParent();
        }

        indexes = new IndexResource[indexesSet.size()];
        indexes = (IndexResource[]) (indexesSet.toArray(indexes));
        return indexes;
    }

    public Analyzer getAnalyzer(Locale locale)
    {
        // TODO: Implement it using an Analyzer registry for languages and language field in index. \\
        // analyser registry should be refactored out as an external component
        return new CategoryAnalyzer();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////

    public XRefsResource getXRefsResource()
    {
        return searchXRefs;
    }
    
    public List getIndexedBranches(IndexResource index)
    {
        return getXRef(searchXRefs.getIndexedBranches(), index);
    }

    public void setIndexedBranches(IndexResource index, List resources)
    {
        CrossReference xref = searchXRefs.getIndexedBranches();
        modifyXRef(xref, index, resources);
        try
        {
            searchXRefs.setIndexedBranches(xref);
        }
        catch (ValueRequiredException e)
        {
            throw new LabeoRuntimeException("lost indexed branches XRef object", e);
        }
    }

    public List getIndexedNodes(IndexResource index)
    {
        return getXRef(searchXRefs.getIndexedNodes(), index);
    }

    public void setIndexedNodes(IndexResource index, List resources)
    {
        CrossReference xref = searchXRefs.getIndexedNodes();
        modifyXRef(xref, index, resources);
        try
        {
            searchXRefs.setIndexedNodes(xref);
        }
        catch (ValueRequiredException e)
        {
            throw new LabeoRuntimeException("lost indexed nodes XRef object", e);
        }
    }

    public CrossReference getIndexedBranchesXRef()
    {
        return searchXRefs.getIndexedBranches();
    }

    public CrossReference getIndexedNodesXRef()
    {
        return searchXRefs.getIndexedNodes();
    }

    public void updateBranchesAndNodesXRef(Subject subject)
        throws ValueRequiredException
    {
        try
        {
            searchXRefs.setIndexedBranches(searchXRefs.getIndexedBranches());
            searchXRefs.setIndexedNodes(searchXRefs.getIndexedNodes());
        }
        catch (ValueRequiredException e)
        {
            // this should not happen
            throw new RuntimeException(e);
        }
        searchXRefs.update(subject);
    }

    private List getXRef(CrossReference xref, Resource res)
    {
        return Arrays.asList(xref.get(res));
    }

    private void modifyXRef(CrossReference xref, IndexResource index, List resources)
    {
        xref.remove(index);
        Resource[] ress = new Resource[resources.size()];
        ress = (Resource[]) (resources.toArray(ress));
        xref.put(index, ress);
    }

    // path filtering //////////////////////////////////////////////////////////////////////////////

    public TableFilter getBranchFilter(SiteResource site)
    {
        return new PathFilter(site, config.getStrings("accepted_path"));
    }
}
