/*
 */
package net.cyklotron.cms.related.internal;

import net.labeo.services.BaseService;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.BackendException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.event.ResourceDeletionListener;
import net.labeo.services.resource.event.ResourceTreeDeletionListener;
import net.labeo.services.resource.generic.CrossReference;

import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.related.RelationshipsResource;
import net.cyklotron.cms.related.RelationshipsResourceImpl;
import net.cyklotron.cms.site.SiteResource;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class RelatedServiceImpl 
    extends BaseService
    implements RelatedService, ResourceTreeDeletionListener, ResourceDeletionListener 
{
	// instance variables ///////////////////////////////////////////////////
    
    /** the resource service. */
    protected ResourceService resourceService;
    
    /** logger. */
    protected LoggingFacility log;
    
    // initialization ///////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public void start()
    {
        resourceService = (ResourceService)broker.
            getService(ResourceService.SERVICE_NAME);
        log =
            ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(
                LOGGING_FACILITY);
        resourceService.getEvent().addResourceDeletionListener(this, null);
        resourceService.getEvent().addResourceTreeDeletionListener(this, null);
    }

    // public interface /////////////////////////////////////////////////////
    
    /**
     * Returns the set of resources the given resource is related to.
     * 
     * @param res the Resource.
     * @return a set of Resources.
     */
    public Resource[] getRelatedTo(Resource res)
    {
        SiteResource site = CmsTool.getSite(res);
        Resource[] r = resourceService.getStore().getResource(site, "applications");
        if(r.length == 0)
        {
            throw new IllegalStateException("applications node missing under "+site.getPath());
        }
        r = resourceService.getStore().getResource(r[0], "related");
        if(r.length == 0)
        {
            return new Resource[0]; 
        }
        RelationshipsResource rel = (RelationshipsResource)r[0];
        if(rel.getXref() == null)
        {
            return new Resource[0];
        }
        CrossReference xref = rel.getXref();
        Resource[] targets = xref.get(res);
        return targets;
    }
    
    /**
     * Returns the set of resources the given resource is related from.
     * 
     * @param res the Resource.
     * @return a set of Resources.
     */
    public Resource[] getRelatedFrom(Resource res)
    {
        SiteResource site = CmsTool.getSite(res);
        Resource[] r = resourceService.getStore().getResource(site, "applications");
        if(r.length == 0)
        {
            throw new IllegalStateException("applications node missing under "+site.getPath());
        }
        r = resourceService.getStore().getResource(r[0], "related");
        if(r.length == 0)
        {
            return new Resource[0]; 
        }
        RelationshipsResource rel = (RelationshipsResource)r[0];
        if(rel.getXref() == null)
        {
            return new Resource[0];
        }
        CrossReference xref = rel.getXref();
        Resource[] sources = xref.getInv(res);
        return sources;
    }

    /**
     * Modifies the set of resources the given resource is related to. 
     * 
     * @param res the Resource.
     * @param targets a set of Resources.
     */    
    public void setRelatedTo(Resource res, Resource[] targets, Subject subject)
    {
        SiteResource site = CmsTool.getSite(res);
        Resource[] r = resourceService.getStore().getResource(site, "applications");
        if(r.length == 0)
        {
            throw new IllegalStateException("applications node missing under "+site.getPath());
        }
        Resource p = r[0];
        r = resourceService.getStore().getResource(r[0], "related");
        RelationshipsResource rel;
        if(r.length == 0)
        {
            try
            {
                rel = RelationshipsResourceImpl.createRelationshipsResource(resourceService, "related", p, subject);
            }
            catch(ValueRequiredException e)
            {
                throw new BackendException("unexpected exception", e);
            }
        }
        else
        {
            rel = (RelationshipsResource)r[0];
        }
        if(rel.getXref() == null)
        {
            rel.setXref(new CrossReference());
        }
        CrossReference xref = rel.getXref();
        xref.remove(res);
        xref.put(res, targets);
        rel.setXref(xref);
        rel.update(subject);
    }

	/**
	 * Called when a resource is deleted.
	 * 
	 * <p>Relationships with other resource within the same site are automatically
	 * cleared.</p>
	 */
	public void resourceDeleted(Resource res)
	{
		SiteResource site = CmsTool.getSite(res);
		if(site != null)
		{
			RelationshipsResource rel = getRelationshipsResource(site);
			CrossReference xref = rel.getXref();
			if(xref == null)
			{
				return;
			}
			xref.remove(res);
			xref.removeInv(res);
			try
			{
				Subject root = resourceService.getSecurity().getSubject(Subject.ROOT);
				rel.setXref(xref);
				rel.update(root);
			}
			catch(Exception e)
			{
				log.error("failed to auto clean relationships", e);
			}
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
    	SiteResource site = CmsTool.getSite(res);
        if(site != null)
        {
			RelationshipsResource rel = getRelationshipsResource(site);
			CrossReference xref = rel.getXref();
			if(xref == null)
			{
				return;
			}
			clearSubRelation(xref, res);
			try
			{
				Subject root = resourceService.getSecurity().getSubject(Subject.ROOT);
				rel.setXref(xref);
				rel.update(root);
			}
			catch(Exception e)
			{
				log.error("failed to auto clean relationships", e);
			}
        }
    }
    
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
    
    private void clearSubRelation(CrossReference xref, Resource res)
    {
		xref.remove(res);
		xref.removeInv(res);
		Resource[] children = resourceService.getStore().getResource(res);
		for(int i = 0; i < children.length; i++)
		{
			clearSubRelation(xref, children[i]); 
		}
    }
}
