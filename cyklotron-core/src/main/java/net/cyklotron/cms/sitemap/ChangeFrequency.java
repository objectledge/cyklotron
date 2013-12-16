package net.cyklotron.cms.sitemap;

/**
 * Change frequency of a resource referenced in a site map.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public enum ChangeFrequency
{
    /** Changes every time the page is accessed. */
    ALWAYS,

    /** Changes once an hour. */
    HOURLY,

    /** Changes once a day. */
    DAILY,

    /** Changes once a week. */
    WEEKLY,

    /** Changes once a month. */
    MONTHLY,

    /** Changes once a year. */
    YEARLY,

    /** Never changes, an archived resource. */
    NEVER;
}
