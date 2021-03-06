/*
 */
package net.cyklotron.cms.related;

import java.util.Comparator;

import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

/**
 * Allows site maintainers to create explicit relationships between resources.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public interface RelatedService 
{
    // constants ////////////////////////////////////////////////////////////
    
    /** service name */
    public static final String SERVICE_NAME = "cms_related";
    
    /** logging facility */
    public static final String LOGGING_FACILITY = "cms";
    
    // public interface /////////////////////////////////////////////////////
    
    /**
     * Returns the set of resources the given resource is related to.
     * @param res the Resource.
     * @param manualSequence preferred sequence of resources for presentation, may be null.
     * @param autoSequence comparator for sorting resources not included in manualSequence, may be null.
     * 
     * @param 
     * @return a set of Resources.
     */
    public Resource[] getRelatedTo(CoralSession coralSession, Resource res, ResourceList<Resource> manualSequence, Comparator<Resource> autoSequence);
    
    /**
     * Returns the set of resources the given resource is related from.
     * 
     * @param res the Resource.
     * @return a set of Resources.
     */
    public Resource[] getRelatedFrom(CoralSession coralSession, Resource res);

    /**
     * Modifies the set of resources the given resource is related to. 
     * 
     * @param res the Resource.
     * @param targets a set of Resources.
     */    
    public void setRelatedTo(CoralSession coralSession, Resource res, Resource[] targets);
    
    
    public Relation getRelation(CoralSession coralSession);
    /**
     * Get the relation resource.
     * 
     * @param site the site
     * @return the relation resource with cross reference.
     */
    //public RelationshipsResource getRelationshipsResource(SiteResource site);
}
