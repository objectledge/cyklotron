package net.cyklotron.cms.httpfeed;

import net.labeo.services.Service;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;

import net.cyklotron.cms.site.SiteResource;

/**
 * This service manages the http feeds defined for the site..
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HttpFeedService.java,v 1.1 2005-01-12 20:45:02 pablo Exp $
 */
public interface HttpFeedService extends Service
{
    /** The name of the service (<code>"httpfeed"</code>). */
    public final static String SERVICE_NAME = "httpfeed";

    /** The logging facility where the service issues it's informational
     * messages. */
    public static final String LOGGING_FACILITY = "httpfeed";

    /** The parent node of the site httpfeeds (httpfeed) */
    public static final String SITE_FEEDS = "httpfeed";

    /**
     * Returns all feeds defined for the site
     *
     * @param site the site resource for which feeds are defined.
     */
    public HttpFeedResource[] getFeeds(SiteResource site)
    throws HttpFeedException;

    /** Returns a parent resource for feeds defined for the site
     *
     * @param site the site resource for which feeds are defined.
     *
     */
    public Resource getFeedsParent(SiteResource site)
    throws HttpFeedException;

    /** Refreshes a feed content.
     *
     * @param feed the feed being updated.
     * @param content new feed content
     * @param subject the subject performing refresh action.
     */
    public void refreshFeed(HttpFeedResource feed, String content, Subject subject);

    /**
     * Retrieves some content via HTTP.
     *
     * @param url URL which points to the content
     * @return body of a content or <code>null</code>
     */
    public String getContent(String url);
}

