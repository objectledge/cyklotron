package net.cyklotron.cms.search.internal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.labeo.services.InitializationError;
import net.labeo.services.logging.Logger;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.resource.ResourceInheritance;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.event.ResourceChangeListener;
import net.labeo.services.resource.event.ResourceCreationListener;
import net.labeo.services.resource.event.ResourceDeletionListener;
import net.labeo.services.resource.event.ResourceTreeChangeListener;
import net.labeo.services.resource.generic.CrossReference;

import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.IndexableResource;
import net.cyklotron.cms.search.IndexingFacility;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;

/**
 * Implementation of resource changes listeners.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IndexingResourceChangesListener.java,v 1.2 2005-01-18 17:38:08 pablo Exp $
 */
public class IndexingResourceChangesListener implements 
    ResourceChangeListener, ResourceDeletionListener,
    ResourceCreationListener, ResourceTreeChangeListener
{
    // deps ----------------------------------------------------------------------------------------

    /** logging facility */
    private Logger log;

    /** resource service - for getting resources */
    private CoralSession resourceService;

    /** search service - for managing index resources */
    private SearchService searchService;

    /** indexing facility */
    private IndexingFacility indexingFacility;

    // local ---------------------------------------------------------------------------------------

    /** system root subject */
    private Subject rootSubject;

    /**
     * Creates the facility.
     * @param log
     * @param searchService
     * @param resourceService
     */
    public IndexingResourceChangesListener(
        Logger log,
        SearchService searchService,
        IndexingFacility indexingFacility, 
        CoralSession resourceService)
    {
        this.log = log;        
        this.searchService = searchService;
        this.indexingFacility = indexingFacility;
        this.resourceService = resourceService;

        // get root subject
        try
        {
            rootSubject = resourceService.getSecurity().getSubject(Subject.ROOT);
        }
        catch (EntityDoesNotExistException e)
        {
            throw new InitializationError("IndexingFacility: Could not find root subject", e);
        }

        // register listeners
        try
        {
            ResourceClass indexableResClass =
                resourceService.getSchema().getResourceClass(IndexableResource.CLASS_NAME);
            //resourceService.getEvent().addResourceCreationListener(this, indexableResClass);
            //resourceService.getEvent().addResourceChangeListener(this, indexableResClass);
            resourceService.getEvent().addResourceDeletionListener(this, indexableResClass);
            //resourceService.getEvent().addResourceTreeChangeListener(this, null);
        }
        catch (EntityDoesNotExistException e)
        {
            throw new InitializationError("IndexingFacility: Could not find '"+
                IndexableResource.CLASS_NAME+"' resource class", e);
        }
    }

    // listeners -----------------------------------------------------------------------------------

    /**
     * This implementation does nothing.
     */
    public void resourceCreated(Resource resource)
    {
        // add to addition log??
    }

    /**
     * Does nothing.
     * An implementation or resource change listener for updating indexes it only removes outdated
     * resource.
     */
    public void resourceChanged(Resource resource, Subject subject)
    {
        //IndexableResource iRes = (IndexableResource)resource;
        //deleteFromIndexes(iRes);
    }

    /**
     * An implementation or resource deletion listener for updating indexes.
     *
     *<pre>get indexes which index the deleted resource
     * foreach index
     *      delete the resource
     *</pre>
     */
    public void resourceDeleted(Resource resource)
    {
        // remove resource from branches and nodes
        try
        {
            CrossReference xref = searchService.getIndexedBranchesXRef();
            xref.removeInv(resource);
            xref = searchService.getIndexedNodesXRef();
            xref.removeInv(resource);
            searchService.updateBranchesAndNodesXRef(rootSubject);
        }
        catch (ValueRequiredException e)
        {
            log.error("IndexingFacility: Cannot delete resource '"+resource.getPath()+
                "' from index cross reference", e);
        }
        
        // remove resource from indexes
        IndexableResource iRes = (IndexableResource)resource;
        deleteFromIndexes(iRes);
    }

    public void resourceTreeChanged(ResourceInheritance item, boolean added)
    {
        // TODO: need to change ARL event generation - two/three events generated on tree change
        // are useless since they impose statefull event listeners, what we need is something like:
        // - resourceTreeChanged(Resource child, Resource oldParent, Resource newParent)
        if(item != null)
        {
            if(!added)
            {
                Resource child = item.getChild();
                Set resources = new HashSet();
                collectResources(child, resources);
                Map resourcesByIndex = indexingFacility.getResourcesByIndex(resources);

                for (Iterator iter = resourcesByIndex.keySet().iterator(); iter.hasNext();)
                {
                    IndexResource index = (IndexResource)iter.next();
                    Set resSet = (Set) resourcesByIndex.get(index);
                    IndexableResource[] res = 
                        (IndexableResource[]) resSet.toArray(new IndexableResource[resSet.size()]);
                    try
                    {
                        indexingFacility.deleteFromIndex(index, res);
                    }
                    catch(SearchException e)
                    {
                        log.error("IndexingFacility: colud not remove resources from index '" + 
                            index.getPath()+"'", e);
                    }
                }
            }
        }
    }

    // implementation ------------------------------------------------------------------------------

    private void collectResources(Resource resource, Set resources)
    {
        if(resource instanceof IndexableResource)
        {
            resources.add(resource);
        }

        Resource[] children = resourceService.getStore().getResource(resource);
        for (int i = 0; i < children.length; i++)
        {
            collectResources(children[i], resources);
        }
    }
        
    private void deleteFromIndexes(IndexableResource iRes)
    {
        long[] ids = new long[] { iRes.getId() };
        IndexResource[] indexes = searchService.getIndex(iRes);
        for (int i = 0; i < indexes.length; i++)
        {
            IndexResource index = indexes[i];
            try
            {
                indexingFacility.deleteFromIndex(index, ids);
            }
            catch(SearchException e)
            {
                log.error("IndexingFacility: colud not remove resource #"+iRes.getIdString()+" '"+
                    iRes.getPath()+"' from index '"+index.getPath()+"'", e);
            }
        }
    }
}
