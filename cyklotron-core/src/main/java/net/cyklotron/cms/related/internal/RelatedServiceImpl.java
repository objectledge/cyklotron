package net.cyklotron.cms.related.internal;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.AmbigousEntityNameException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.entity.EntityExistsException;
import org.objectledge.coral.event.ResourceDeletionListener;
import org.objectledge.coral.event.ResourceTreeDeletionListener;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.relation.RelationModification;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.related.RelatedService;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class RelatedServiceImpl 
    implements RelatedService, ResourceTreeDeletionListener, ResourceDeletionListener 
{
    public static final String RELATION_NAME = "related.Relation";
    
	// instance variables ///////////////////////////////////////////////////
    /** coral session factory */
    private CoralSessionFactory sessionFactory;
    
    /** logger. */
    private Logger log;
    
    /** relation */
    private Relation relatedRelation;
    
    // initialization ///////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public RelatedServiceImpl(CoralSessionFactory sessionFactory, Logger logger)
    {
        this.log = logger;
        this.sessionFactory = sessionFactory;
        CoralSession coralSession = sessionFactory.getRootSession();
        coralSession.getEvent().addResourceDeletionListener(this, null);
        coralSession.getEvent().addResourceTreeDeletionListener(this, null);
        coralSession.close();
    }

    // public interface /////////////////////////////////////////////////////
    
    /**
     * Returns the set of resources the given resource is related to.
     * 
     * @param res the Resource.
     * @return a set of Resources.
     */
    public Resource[] getRelatedTo(CoralSession coralSession, Resource res)
    {
        Relation relation = getRelation(coralSession);
        return relation.get(res);
    }
    
    /**
     * Returns the set of resources the given resource is related from.
     * 
     * @param res the Resource.
     * @return a set of Resources.
     */
    public Resource[] getRelatedFrom(CoralSession coralSession, Resource res)
    {
        Relation relation = getRelation(coralSession);
        return relation.getInverted().get(res);
    }

    /**
     * Modifies the set of resources the given resource is related to. 
     * 
     * @param res the Resource.
     * @param targets a set of Resources.
     */    
    public void setRelatedTo(CoralSession coralSession, Resource res, Resource[] targets)
    {
        Relation relation = getRelation(coralSession);
        RelationModification modification = new RelationModification();
        modification.add(res, targets);                
        coralSession.getRelationManager().updateRelation(relation, modification);
    }

    // resource deletion listener implementation
    
	/**
	 * Called when a resource is deleted.
	 * 
	 * <p>Relationships with other resource within the same site are automatically
	 * cleared.</p>
	 */
	public void resourceDeleted(Resource res)
	{
        CoralSession coralSession = sessionFactory.getRootSession();
        Relation relation = getRelation(coralSession);
        RelationModification modification = new RelationModification();
        try
        {
            modification.remove(res);
            modification.removeInv(res);
            coralSession.getRelationManager().updateRelation(relation, modification);
        }
        finally
        {
            coralSession.close();
        }
	}
    
    /**
     * Called when a resource tree is deleted.
     * 
     * <p>Relationships with other resource within the same site are automatically
     * cleared.</p>
     */
    public void resourceTreeDeleted(Resource res)
    {
        CoralSession coralSession = sessionFactory.getRootSession();
        Relation relation = getRelation(coralSession);
        RelationModification modification = new RelationModification();
        try
        {
            clearSubRelation(coralSession, modification, res);
            coralSession.getRelationManager().updateRelation(relation, modification);
        }
        finally
        {
            coralSession.close();
        }
    }
    
    //TODO see mayby it's not needed
    /**
    public RelationshipsResource getRelationshipsResource(SiteResource site)
    {
		Resource[] r = resourceService.getStore().getResource(site, "applications");
		if(r.length == 0)
		{
			throw new IllegalStateException("applications node missing under "+site.getPath());
		}
		r = resourceService.getStore().getResource(r[0], "related");
		if(r.length == 0)
		{
			return null; 
		}
		RelationshipsResource rel = (RelationshipsResource)r[0];
		return rel;
    }
    */
    
    private void clearSubRelation(CoralSession coralSession, 
        RelationModification diff, Resource res)
    {
		diff.remove(res);
		diff.removeInv(res);
		Resource[] children = coralSession.getStore().getResource(res);
		for(int i = 0; i < children.length; i++)
		{
			clearSubRelation(coralSession, diff, children[i]); 
		}
    }
    
    /**
     * 
     * @param coralSession the coral session.
     * @return the relation.
     */
    public Relation getRelation(CoralSession coralSession)
    {     
        if(relatedRelation != null)
        {
            return relatedRelation;
        }
        try
        {
            relatedRelation = coralSession.getRelationManager().
                                   getRelation(RELATION_NAME);
        }
        catch(AmbigousEntityNameException e)
        {
            throw new IllegalStateException("ambiguous related relation");
        }
        catch(EntityDoesNotExistException e)
        {
            //ignore it.
        }
        if(relatedRelation != null)
        {
            return relatedRelation;
        }
        try
        {
            createRelation(coralSession, RELATION_NAME);
        }
        catch(EntityExistsException e)
        {
            throw new IllegalStateException("the related relation already exists");
        }
        return relatedRelation;
    }    
    
    /**
     * 
     * 
     * @param coralSession the coralSession. 
     */
    private synchronized void createRelation(CoralSession coralSession, String name)
        throws EntityExistsException
    {
        if(relatedRelation == null)
        {
            relatedRelation = coralSession.getRelationManager().
                createRelation(RELATION_NAME);
        }
    }
    
}
