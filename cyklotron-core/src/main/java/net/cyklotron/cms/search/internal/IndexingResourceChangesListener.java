package net.cyklotron.cms.search.internal;

import java.util.HashSet;
import java.util.Set;

import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.IndexableResource;
import net.cyklotron.cms.search.IndexingFacility;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.event.ResourceChangeListener;
import org.objectledge.coral.event.ResourceCreationListener;
import org.objectledge.coral.event.ResourceDeletionListener;
import org.objectledge.coral.event.ResourceTreeChangeListener;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.relation.RelationModification;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ResourceInheritance;

/**
 * Implementation of resource changes listeners.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: IndexingResourceChangesListener.java,v 1.9 2005-07-29 20:52:08 pablo Exp $
 */
public class IndexingResourceChangesListener implements 
    ResourceChangeListener, ResourceDeletionListener,
    ResourceCreationListener, ResourceTreeChangeListener
{
    // deps ----------------------------------------------------------------------------------------

    /** logging facility */
    private Logger log;

    /** resource service - for getting resources */
    private CoralSessionFactory sessionFactory;

    /** search service - for managing index resources */
    private SearchService searchService;

    /** indexing facility */
    private IndexingFacility indexingFacility;

    // local ---------------------------------------------------------------------------------------

    /**
     * Creates the facility.
     * @param searchService
     */
    public IndexingResourceChangesListener(
        Logger logger,
        SearchService searchService,
        IndexingFacility indexingFacility, 
        CoralSessionFactory sessionFactory)
    {
        this.log = logger;        
        this.searchService = searchService;
        this.indexingFacility = indexingFacility;
        this.sessionFactory = sessionFactory;

        // register listeners
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            ResourceClass indexableResClass =
                coralSession.getSchema().getResourceClass(IndexableResource.CLASS_NAME);
            //coralSession.getEvent().addResourceCreationListener(this, indexableResClass);
            //coralSession.getEvent().addResourceChangeListener(this, indexableResClass);
            coralSession.getEvent().addResourceDeletionListener(this, indexableResClass);
            coralSession.getEvent().addResourceTreeChangeListener(this, null);
        }
        catch (EntityDoesNotExistException e)
        {
            //throw new ComponentInitializationError("IndexingFacility: Could not find '"+
            //    IndexableResource.CLASS_NAME+"' resource class", e);
            log.debug("Failed to register listener - cannot find Indexable resource class");
        }
        finally
        {
            coralSession.close();
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
        CoralSession coralSession = sessionFactory.getRootSession();
        // remove resource from branches and nodes
        try
        {
            Relation relation = searchService.getIndexedBranchesRelation(coralSession);
            RelationModification diff = new RelationModification();
            diff.removeInv(resource);
            coralSession.getRelationManager().updateRelation(relation, diff);
            diff = new RelationModification();
            relation = searchService.getIndexedNodesRelation(coralSession);
            diff.removeInv(resource);
            coralSession.getRelationManager().updateRelation(relation, diff);
            // remove resource from indexes
            IndexableResource iRes = (IndexableResource)resource;
            deleteFromIndexes(coralSession, iRes);
        }
        finally
        {
            coralSession.close();
        }
        
    }

    public void resourceTreeChanged(ResourceInheritance item, boolean added)
    {
        // TODO: need to change ARL event generation - two/three events generated on tree change
        // are useless since they impose statefull event listeners, what we need is something like:
        // - beforeResourceTreeChanged(Resource child, Resource oldParent, Resource newParent)
        // - afterResourceTreeChanged(Resource child, Resource oldParent, Resource newParent)
        if(item != null)
        {
            if(!added)
            {
                CoralSession coralSession = sessionFactory.getRootSession();
                try
                {
                    Resource child = item.getChild();
                    Resource parent = item.getParent();
                    IndexResource[] indexes = searchService.getBranchIndexes(coralSession, parent);
                    Set<Resource> resources = new HashSet();
                    collectResources(coralSession, child, resources);
                    long[] ids = new long[resources.size()];
                    int i = 0;
                    for(Resource res : resources)
                    {
                        ids[i] = res.getId();
                        i++;
                    }
                    for (IndexResource index : indexes)
                    {
                        try
                        {
                            indexingFacility.deleteFromIndex(index, ids);
                        }
                        catch(SearchException e)
                        {
                            log.error("IndexingFacility: colud not remove resources from index '" + 
                                index.getPath()+"'", e);
                        }
                    }
                }
                finally
                {
                    coralSession.close();
                }
            }
        }
    }

    // implementation ------------------------------------------------------------------------------

    private void collectResources(CoralSession coralSession, Resource resource, Set resources)
    {
        if(resource instanceof IndexableResource)
        {
            resources.add(resource);
        }
        Resource[] children = coralSession.getStore().getResource(resource);
        for (int i = 0; i < children.length; i++)
        {
            collectResources(coralSession, children[i], resources);
        }
    }
        
    private void deleteFromIndexes(CoralSession coralSession, IndexableResource iRes)
    {
        long[] ids = new long[] { iRes.getId() };
        IndexResource[] indexes = searchService.getIndexes(coralSession, iRes);
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
