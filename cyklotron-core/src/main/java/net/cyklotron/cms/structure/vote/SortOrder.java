package net.cyklotron.cms.structure.vote;

/**
 * Specify vote results sorting order.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public enum SortOrder
{
    /** positive votes */
    POSITIVE,

    /** negative votes */
    NEGATIVE,
   
    /** positive + negative votes */
    TOTAL, 
    
    /** positive / (positive + negative) */
    POSITIVE_RATIO,
    
    /** negative / (positive + negative) */
    NEGATIVE_RATIO,
}
