package net.cyklotron.cms.aggregation;

import net.labeo.services.Service;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;

import net.cyklotron.cms.site.SiteResource;

/**
 * Provides content aggregation & recommnedation framework. 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: AggregationService.java,v 1.1 2005-01-12 20:44:57 pablo Exp $
 */
public interface AggregationService
    extends Service
{
    // constants ////////////////////////////////////////////////////////////

    /** service name constant. */
    public static final String SERVICE_NAME = "cms_aggregation";
    
    /** the logging facility name. */
    public static final String LOGGING_FACILITY = "cms";    
    
    // public API ///////////////////////////////////////////////////////////

    /**
     * List recommendations made towards the specified site.
     * 
     * @param site the site to which the recommendations are made.
     * @return array of recommendation objects.
     * @throws AggregationException if the operation fails.
     */
    public RecommendationResource[] getPendingRecommendations(SiteResource site)
        throws AggregationException;
        
    /**
     * Lists recommendations made from a site by a certain idividual. 
     * 
     * @param site the site from which recommendations are made.
     * @param subject the individual
     * @return array of recommendation objects.
     * @throws AggregationException if the operation fails.
     */
    public RecommendationResource[] getSubmittedRecommendations( 
        SiteResource site, Subject subject)
        throws AggregationException;
        
    /**
     * Lists the commends made on a specific recommendation.
     * 
     * @param rec the recommendation.
     * @return list of comments, sorted chronologically.
     * @throws AggregationException if the operation fails.
     */
    public RecommendationCommentResource[] getComments(RecommendationResource 
        rec)
        throws AggregationException;
    
    /**
     * Submit a new recommendation.
     * 
     * @param resource a resource to recommend.
     * @param site the site the resource is recommended to
     * @param comment an optional comment.
     * @param subject the subject that performs the operation.
     * @throws AggregationException if the operation fails.
     */
    public void submitRecommendation(Resource resource, SiteResource site, 
        String comment, Subject subject)
        throws AggregationException;
                
    /**
     * Reject a pending recommendation.
     * 
     * @param rec the recommendation.
     * @param comment an obligatory comment.
     * @param subject the subject that performs the operation.
     * @throws AggregationException if the operation fails.
     */
    public void rejectRecommendation(RecommendationResource rec, 
        String comment, Subject subject)
        throws AggregationException;

    /**
     * Resubmit a rejected recommendation.
     * 
     * @param rec the recommendation.
     * @param comment an obligatory comment.
     * @param subject the subject that performs the operation.
     * @throws AggregationException if the operation fails.
     */
    public void resubmitRecommendation(RecommendationResource rec, 
        String comment, Subject subject)
        throws AggregationException;
    
    /**
     * Discard a pending or rejected recommendation.
     * 
     * @param rec the recommendation.
     * @param subject the subject that performs the operation.
     * @throws AggregationException if the operation fails.
     */    
    public void discardRecommendation(RecommendationResource rec, 
        Subject subject)
        throws AggregationException;

    /**
     * Creates an import record.
     * 
     * <p>Calling this method wipes out any existing recommendations for
     * the resource - destination site pairs, regardles of their state.</p>
     * 
     * @param source the imported resource.
     * @param destination the imported resource's copy in the destination site.
     * @param subject the subject that performs the operation.
     * @throws AggregationException
     */
    public void createImport(Resource source, Resource destination, Subject subject)
        throws AggregationException; 
        
    /**
     * Returns resources imported to a site.
     * 
     * @return resources imported to a site.      
     * @throws AggregationException if the operation fails.
     */
    public ImportResource[] getImports(SiteResource destination)
        throws AggregationException;

    /**
     * Returns resources exported from a site.
     * 
     * @return resources exported from a site.      
     * @throws AggregationException if the operation fails.
     */
    public ImportResource[] getExports(SiteResource source)
        throws AggregationException;

    /**
     * Check if a resource is already recommended to a site.
     * 
     * @param resource the resource.
     * @param site the site.
     * @return <code>true</code> if the resource is already recommended.
     * @throws AggregationException if the operation fails.
     */        
    public boolean isRecommendedTo(Resource resource, SiteResource site)
        throws AggregationException;

    /**
     * Get the sites a given resource may be imported to.
     * 
     * @param resource the resource.
     * @return an array of site resources.
     * @throws AggregationException if the operation fails.
     */        
    public SiteResource[] getValidImportSites(Resource resource)
        throws AggregationException;

    /**
     * Get the sites a given resource may be recommended to.
     * 
     * @param resource the resource.
     * @return an array of site resources.
     * @throws AggregationException if the operation fails.
     */            
    public SiteResource[] getValidRecommendationSites(Resource resource)
        throws AggregationException; 

    /**
      * Checks whether the resource can be imported.
      * 
      * @param source the resource to be copied.
      * @param target the target parent resource.
      * @param subject the subject.
      * @return <code>true</code> if the resource can be imported to the site.
      * @throws AggregationException if the operation fails.
      */
    public boolean canImport(Resource source, Resource target, Subject subject);
    
}
