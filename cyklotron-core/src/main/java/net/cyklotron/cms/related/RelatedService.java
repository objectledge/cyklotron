/*
 */
package net.cyklotron.cms.related;

import net.labeo.services.Service;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;

import net.cyklotron.cms.site.SiteResource;

/**
 * Allows site maintainers to create explicit relationships between resources.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public interface RelatedService 
    extends Service
{
    // constants ////////////////////////////////////////////////////////////
    
    /** service name */
    public static final String SERVICE_NAME = "cms_related";
    
    /** logging facility */
    public static final String LOGGING_FACILITY = "cms";
    
    // public interface /////////////////////////////////////////////////////
    
    /**
     * Returns the set of resources the given resource is related to.
     * 
     * @param res the Resource.
     * @return a set of Resources.
     */
    public Resource[] getRelatedTo(Resource res);
    
    /**
     * Returns the set of resources the given resource is related from.
     * 
     * @param res the Resource.
     * @return a set of Resources.
     */
    public Resource[] getRelatedFrom(Resource res);

    /**
     * Modifies the set of resources the given resource is related to. 
     * 
     * @param res the Resource.
     * @param targets a set of Resources.
     * @param subject the subject that performs the operation.
     */    
    public void setRelatedTo(Resource res, Resource[] targets, Subject subject);
    
    /**
     * Get the relation resource.
     * 
     * @param site the site
     * @return the relation resource with cross reference.
     */
    public RelationshipsResource getRelationshipsResource(SiteResource site);
}
