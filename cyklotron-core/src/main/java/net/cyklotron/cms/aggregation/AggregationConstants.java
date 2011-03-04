/*
 */
package net.cyklotron.cms.aggregation;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public interface AggregationConstants
{
    /** Destination is up-to-date with source. */
    public static final int IMPORT_UPTODATE = 0;
    
    /** Source was modified after copy was made. */
    public static final int IMPORT_MODIFIED = 1;
    
    /** Source was deleted after copy was made. */
    public static final int IMPORT_DELETED = 2;
    
    /** Recommendation is waiting for target site maintainer's attention. */
    public static final int RECOMMENDATION_PENDING = 0;
    
    /** Recommendation was rejected by target site maintainer. */
    public static final int RECOMMENDATION_REJECTED= 2;
}
