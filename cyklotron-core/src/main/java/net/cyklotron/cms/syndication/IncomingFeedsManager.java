package net.cyklotron.cms.syndication;

import java.io.IOException;
import java.util.List;

import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.util.URI.MalformedURIException;

/**
 * Manages the incoming syndication feeds defined for the site..
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IncomingFeedsManager.java,v 1.2 2007-11-18 21:23:33 rafal Exp $
 */
public interface IncomingFeedsManager
{
    /** The parent node of the site incoming feeds (incoming) */
    public static final String INCOMING_FEEDS_ROOT = "incoming";

    /**
     * Create a feed for a site.
     * 
     * @param coralSession
     *            TODO
     * @param name
     *            the name of the newly created feed.
     * @param url
     *            the url of this feed.
     * @param interval
     *            feed content update interval in minutes.
     * @param template
     *            the transformation template for the feed, may be <code>null</code>.
     * @param site
     *            the site resource for which feed is created.
     * 
     * @throws FeedCreationException
     * @throws EmptyFeedNameException
     * @throws FeedAlreadyExistsException
     * @throws EmptyUrlException
     * @throws MalformedURIException
     * @throws InvalidResourceNameException
     */
    public IncomingFeedResource createFeed(CoralSession coralSession, String name, String url,
        int interval, String template, SiteResource site)
    throws EmptyFeedNameException,
        FeedCreationException, FeedAlreadyExistsException, EmptyUrlException,
        MalformedURIException, InvalidResourceNameException;
    
    /**
     * Rename a feed.
     * @param coralSession TODO
     * @param feed the renamed feed
     * @param name the new name of the feed.
     *
     * @throws EmptyFeedNameException
     * @throws FeedAlreadyExistsException 
     * @throws EmptyUrlException
     * @throws MalformedURIException
     * @throws InvalidResourceNameException 
     */
    public void updateFeed(CoralSession coralSession, IncomingFeedResource feed, String name, String url,
        int interval, String template)
    throws EmptyFeedNameException,
        FeedAlreadyExistsException, EmptyUrlException, MalformedURIException,
        InvalidResourceNameException;
    
    /**
     * Delete a feed.
     * @param coralSession TODO
     * @param feed feed to be deleted
     *
     * @throws EntityInUseException
     */
    public void deleteFeed(CoralSession coralSession, IncomingFeedResource feed)
    throws EntityInUseException;

    /**
     * Returns all feeds defined for the site
     * @param coralSession TODO
     * @param site the site resource for which feeds are defined.
     *
     * @throws SyndicationException 
     */
    public IncomingFeedResource[] getFeeds(CoralSession coralSession, SiteResource site)
    throws SyndicationException;

    /** Returns a parent resource for feeds defined for the site
     * @param coralSession TODO
     * @param site the site resource for which feeds are defined.
     *
     * @throws CannotCreateSyndicationRootException 
     * @throws TooManySyndicationRootsException 
     * @throws TooManyFeedsRootsException 
     * @throws CannotCreateFeedsRootException 
     *
     */
    public Resource getFeedsParent(CoralSession coralSession, SiteResource site)
    throws TooManySyndicationRootsException, CannotCreateSyndicationRootException,
        TooManyFeedsRootsException, CannotCreateFeedsRootException;

    /** Refreshes the feed content.
     * @param coralSession TODO
     * @param feed the feed being updated.
     *
     * @throws Exception 
     */
    public void refreshFeed(CoralSession coralSession, IncomingFeedResource feed)
    throws Exception;

    /**
     * Returns a list of transformation template names defined in the system.
     *
     * @return list of names of transformation templates.
     * @throws IOException 
     */
    public List getTransformationTemplates() throws IOException;
}
